package tourGuide.performance;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tourGuide.exception.UserNoTFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.Attraction;
import tourGuide.model.User;
import tourGuide.model.VisitedLocation;
import tourGuide.proxies.GpsUtilFeign;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

@DisplayName("Overall performance tests")
@SpringBootTest
public class TestPerformance {

	@Autowired  
	private GpsUtilFeign gpsUtilFeign;
	@Autowired
	private TourGuideService tourGuideService;
	@Autowired
	private RewardsService rewardsService;
	private static Locale locale = new Locale("en", "US");
	
	@BeforeAll
	public static void setUp() {
		InternalTestHelper.setInternalUserNumber(100000);
		Locale.setDefault(locale);
	}
	
    //private ExecutorService executorService = Executors.newFixedThreadPool(1000);

    /*
     * A note on performance improvements:
     * 
     * The number of users generated for the high volume tests can be easily
     * adjusted via this method:
     * 
     * InternalTestHelper.setInternalUserNumber(100000);
     * 
     * 
     * These tests can be modified to suit new solutions, just as long as the
     * performance metrics at the end of the tests remains consistent.
     * 
     * These are performance metrics that we are trying to hit:
     * 
     * highVolumeTrackLocation: 100,000 users within 15 minutes:
     * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
     * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     * highVolumeGetRewards: 100,000 users within 20 minutes:
     * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
     * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     */

    @Test
    public void highVolumeTrackLocation() throws UserNoTFoundException {
	List<User> allUsers = new ArrayList<>();
	allUsers = tourGuideService.getAllUsers();

	StopWatch stopWatch = new StopWatch();
	stopWatch.start();
	tourGuideService.trackAllUserLocation(allUsers);
	stopWatch.stop();
	tourGuideService.tracker.stopTracking();

	System.out.println("highVolumeTrackLocation: Time Elapsed: "
		+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
	assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Test
    public void highVolumeGetRewards() throws UserNoTFoundException {
	StopWatch stopWatch = new StopWatch();
	stopWatch.start();

	Attraction attraction = gpsUtilFeign.getAttractions().get(0);
	List<User> allUsers = new ArrayList<>();
	allUsers = tourGuideService.getAllUsers();
	allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

	rewardsService.calculateAllRewards(allUsers);

	for (User user : allUsers) {
	    assertTrue(user.getUserRewards().size() > 0);
	}
	stopWatch.stop();
	tourGuideService.tracker.stopTracking();

	System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toMinutes(stopWatch.getTime())
		+ " seconds.");
	assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

}
