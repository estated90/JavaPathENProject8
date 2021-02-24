package tourGuide.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.exception.RewardException;
import tourGuide.exception.UserNoTFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.Attraction;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.model.VisitedLocation;
import tourGuide.proxies.GpsUtilFeign;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

@DisplayName("Rewards services Tests")
public class TestRewardsService {

    private static Locale locale = new Locale("en", "US");
	@Autowired  
	private GpsUtilFeign gpsUtilFeign;

    @Test
    public void userGetRewards() throws InterruptedException, ExecutionException, RewardException {
	Locale.setDefault(locale);
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	Attraction attraction = gpsUtilFeign.getAttractions().get(0);
	user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
	tourGuideService.trackUserLocation(user);
	List<UserReward> userRewards = user.getUserRewards();
	tourGuideService.tracker.stopTracking();
	assertEquals(1, userRewards.size());
    }

    @Test
    public void isWithinAttractionProximity() {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	Attraction attraction = gpsUtilFeign.getAttractions().get(0);
	assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }

    @Test
    public void nearAllAttractions() throws UserNoTFoundException, RewardException {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	rewardsService.setProximityBuffer(Integer.MAX_VALUE);

	InternalTestHelper.setInternalUserNumber(1);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
	List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
	tourGuideService.tracker.stopTracking();

	assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
    }

}
