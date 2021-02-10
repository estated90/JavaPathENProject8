package tourGuide.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.dto.UserNewPreferences;
import tourGuide.exception.LocalisationException;
import tourGuide.exception.RewardException;
import tourGuide.exception.UserNoTFoundException;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.utils.Utils;
import tripPricer.Provider;

@RestController
public class TourGuideController {

    private Logger logger = LoggerFactory.getLogger(TourGuideController.class);
    @Autowired
    private TourGuideService tourGuideService;
    @Autowired
    private Utils utils;

    @RequestMapping("/")
    public String index() {
	logger.info("Redirecting to greeting message");
	return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName)
	    throws InterruptedException, ExecutionException, UserNoTFoundException, RewardException {
	logger.info("{} is using /getLocation", userName);
	VisitedLocation visitedLocation = tourGuideService.getUserLocation(utils.getUser(userName));
	return JsonStream.serialize(visitedLocation.location);
    }

    @PostMapping(value = "/postPreferences", params = "userName")
    public UserPreferences postPreferences(@RequestParam String userName,
	    @Valid @RequestBody UserNewPreferences userPreferences) throws UserNoTFoundException {
	logger.info("{} is using /postPreferences with {}", userName, userPreferences);
	User user = utils.getUser(userName);
	return tourGuideService.updatePreferences(user, userPreferences);
    }

    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName)
	    throws InterruptedException, ExecutionException, UserNoTFoundException, RewardException {
	logger.info("{} is using /getNearbyAttractions", userName);
	VisitedLocation visitedLocation = tourGuideService.getUserLocation(utils.getUser(userName));
	return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation, utils.getUser(userName)));
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) throws UserNoTFoundException, RewardException {
	logger.info("{} is using /getRewards", userName);
	return JsonStream.serialize(tourGuideService.getUserRewards(utils.getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() throws LocalisationException {
	logger.info("User is using /getAllCurrentLocations");
	String out = JsonStream.serialize(tourGuideService.gettAllCurrentLocation());
	System.out.println(out);
	return JsonStream.serialize(tourGuideService.gettAllCurrentLocation());
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws UserNoTFoundException {
	logger.info("{} is using /getTripDeals", userName);
	List<Provider> providers = tourGuideService.getTripDeals(utils.getUser(userName));
	return JsonStream.serialize(providers);
    }

}