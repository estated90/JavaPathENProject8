package tourGuide.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.output.JsonStream;

import tourGuide.dto.UserNewPreferences;
import tourGuide.exception.LocalisationException;
import tourGuide.exception.RewardException;
import tourGuide.exception.UserNoTFoundException;
import tourGuide.model.Attraction;
import tourGuide.model.User;
import tourGuide.model.VisitedLocation;
import tourGuide.proxies.GpsUtilFeign;
import tourGuide.service.TourGuideService;
import tourGuide.utils.Utils;
import tripPricer.Provider;

@RefreshScope
@RestController
public class TourGuideController {

	private Logger logger = LoggerFactory.getLogger(TourGuideController.class);
	@Autowired
	private TourGuideService tourGuideService;
	@Autowired
	private Utils utils;
	@Autowired
	private GpsUtilFeign gpsUtilFeign;

	@GetMapping("/")
	public String index() {
		logger.info("Redirecting to greeting message");
		return "Greetings from TourGuide!";
	}

	@GetMapping("/getLocation")
	public String getLocation(@RequestParam String userName) throws InterruptedException, ExecutionException,
			UserNoTFoundException, RewardException, LocalisationException {
		logger.info("{} is using /getLocation", userName);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(tourGuideService.getUserLocation(utils.getUser(userName)));
			return json;
		} catch (JsonProcessingException e) {
			logger.error("System was enable to convert object to String");
			return null;
		}
	}

	@PostMapping(value = "/postPreferences", params = "userName")
	public ResponseEntity<String> postPreferences(@RequestParam String userName,
			@Valid @RequestBody UserNewPreferences userPreferences) throws UserNoTFoundException {
		logger.info("{} is using /postPreferences with {}", userName, userPreferences);
		User user = utils.getUser(userName);
		tourGuideService.updatePreferences(user, userPreferences);
		return ResponseEntity.ok("Preferences updated");
	}

	@GetMapping("/getNearbyAttractions")
	public String getNearbyAttractions(@RequestParam String userName) throws InterruptedException, ExecutionException,
			UserNoTFoundException, RewardException, LocalisationException {
		logger.info("{} is using /getNearbyAttractions", userName);
		VisitedLocation visitedLocation = tourGuideService.getUserLocation(utils.getUser(userName));
		return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation, utils.getUser(userName)));
	}

	@GetMapping("/getRewards")
	public String getRewards(@RequestParam String userName) throws UserNoTFoundException, RewardException {
		logger.info("{} is using /getRewards", userName);
		return JsonStream.serialize(tourGuideService.getUserRewards(utils.getUser(userName)));
	}

	@GetMapping("/getAllCurrentLocations")
	public String getAllCurrentLocations() throws LocalisationException {
		logger.info("User is using /getAllCurrentLocations");
		return JsonStream.serialize(tourGuideService.gettAllCurrentLocation());
	}

	@GetMapping("/getTripDeals")
	public String getTripDeals(@RequestParam String userName) throws UserNoTFoundException {
		logger.info("{} is using /getTripDeals", userName);
		List<Provider> providers = tourGuideService.getTripDeals(utils.getUser(userName));
		return JsonStream.serialize(providers);
	}

	@GetMapping("/trackUser")
	public String trackUser(@RequestParam String userName) throws UserNoTFoundException, RewardException {
		logger.info("{} is using /getTripDeals", userName);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(tourGuideService.trackUserLocation(utils.getUser(userName)));
			return json;
		} catch (JsonProcessingException e) {
			logger.error("System was enable to convert object to String");
			return null;
		}
	}
	
	@GetMapping("/getAttractions")
	public List<Attraction> getTripDeals() throws UserNoTFoundException {
		
		return gpsUtilFeign.getAttractions();
	}

}