package tourguide.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.output.JsonStream;

import SharedObject.model.VisitedLocation;
import tourguide.dto.UserNewPreferences;
import tourguide.exception.LocalisationException;
import tourguide.exception.ProviderNoTFoundException;
import tourguide.exception.RewardException;
import tourguide.exception.UserNoTFoundException;
import tourguide.model.User;
import tourguide.service.TourGuideService;
import tourguide.utils.Utils;

@RefreshScope
@RestController
public class TourGuideController {

	private Logger logger = LoggerFactory.getLogger(TourGuideController.class);
	@Autowired
	private TourGuideService tourGuideService;
	@Autowired
	private Utils utils;
	
	private static final String ENABLECONVERT = "System was enable to convert object to String";

	@GetMapping("/")
	public String index() {
		logger.info("Redirecting to greeting message");
		return "Greetings from TourGuide!";
	}

	@GetMapping(value = "/getLocation", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getLocation(@RequestParam @Valid String userName)
			throws UserNoTFoundException, LocalisationException {
		userName = correctPatern(userName);
		logger.info("{} is using /getLocation", userName);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(tourGuideService.getUserLocation(utils.getUser(userName)));
		} catch (JsonProcessingException e) {
			logger.error(ENABLECONVERT);
			return null;
		}
	}

	@PostMapping(value = "/postPreferences", params = "userName")
	public ResponseEntity<String> postPreferences(@RequestParam String userName,
			@Valid @RequestBody UserNewPreferences userPreferences) throws UserNoTFoundException {
		userName = correctPatern(userName);
		logger.info("{} is using /postPreferences with {}", userName, userPreferences);
		User user = utils.getUser(userName);
		tourGuideService.updatePreferences(user, userPreferences);
		return ResponseEntity.ok("Preferences updated");
	}

	@GetMapping(value = "/getNearbyAttractions", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getNearbyAttractions(@RequestParam String userName)
			throws UserNoTFoundException, LocalisationException {
		userName = correctPatern(userName);
		logger.info("{} is using /getNearbyAttractions", userName);
		VisitedLocation visitedLocation = tourGuideService.getUserLocation(utils.getUser(userName));
		return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation, utils.getUser(userName)));
	}

	@GetMapping(value = "/getRewards", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getRewards(@RequestParam String userName) throws UserNoTFoundException, RewardException {
		userName = correctPatern(userName);
		logger.info("{} is using /getRewards", userName);
		return JsonStream.serialize(tourGuideService.getUserRewards(utils.getUser(userName)));
	}

	@GetMapping(value = "/getAllCurrentLocations", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAllCurrentLocations() throws LocalisationException {
		logger.info("User is using /getAllCurrentLocations");
		return JsonStream.serialize(tourGuideService.gettAllCurrentLocation());
	}

	@GetMapping(value = "/getTripDeals", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getTripDeals(@RequestParam String userName) throws UserNoTFoundException, ProviderNoTFoundException {
		userName = correctPatern(userName);
		logger.info("{} is using /getTripDeals", userName);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(tourGuideService.getTripDeals(utils.getUser(userName)));
		} catch (JsonProcessingException e) {
			logger.error(ENABLECONVERT);
			return null;
		}
	}

	@GetMapping(value = "/trackUser", produces = MediaType.APPLICATION_JSON_VALUE)
	public String trackUser(@RequestParam String userName) throws UserNoTFoundException, LocalisationException {
		userName = correctPatern(userName);
		logger.info("{} is using /getTripDeals", userName);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(tourGuideService.trackUserLocation(utils.getUser(userName)));
		} catch (JsonProcessingException e) {
			logger.error(ENABLECONVERT);
			return null;
		}
	}

	private String correctPatern(String param1) {
		return param1.replaceAll("[\n\r|\t]", "_");
	}
}