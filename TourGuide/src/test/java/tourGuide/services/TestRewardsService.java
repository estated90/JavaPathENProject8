package tourGuide.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import SharedObject.model.Attraction;
import SharedObject.model.VisitedLocation;
import tourguide.exception.LocalisationException;
import tourguide.exception.RewardException;
import tourguide.exception.UserNoTFoundException;
import tourguide.helper.InternalTestHelper;
import tourguide.model.User;
import tourguide.model.UserReward;
import tourguide.proxies.GpsUtilFeign;
import tourguide.service.RewardsService;
import tourguide.service.TourGuideService;

@DisplayName("Rewards services Tests")
@SpringBootTest
class TestRewardsService {

	private static Locale locale = new Locale("en", "US");
	@Autowired
	private GpsUtilFeign gpsUtilFeign;
	@Autowired
	private TourGuideService tourGuideService;
	@Autowired
	private RewardsService rewardsService;

	@BeforeAll
	public static void setUp() {
		InternalTestHelper.setInternalUserNumber(1);
		Locale.setDefault(locale);
	}

	@Test
	void userGetRewards() throws InterruptedException, ExecutionException, RewardException, LocalisationException {
		rewardsService.setProximityBuffer(10);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtilFeign.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		assertEquals(1, userRewards.size());
	}

	@Test
	void isWithinAttractionProximity() {
		Attraction attraction = gpsUtilFeign.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	void nearAllAttractions() throws UserNoTFoundException, RewardException {
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsUtilFeign.getAttractions().size(), userRewards.size());
	}

}
