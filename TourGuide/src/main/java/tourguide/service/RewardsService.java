package tourguide.service;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import SharedObject.model.Attraction;
import SharedObject.model.Location;
import SharedObject.model.VisitedLocation;
import tourguide.model.User;
import tourguide.model.UserReward;
import tourguide.proxies.GpsUtilFeign;
import tourguide.proxies.RewardCentralFeign;
import tourguide.utils.Utils;

/**
 * @author Nico
 *         <p>
 *         The service that manage all rewards related service
 *         </p>
 *
 */
@Service
public class RewardsService {

	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private Logger logger = LoggerFactory.getLogger(RewardsService.class);
	@Autowired
	private GpsUtilFeign gpsUtilFeign;
	@Autowired
	private RewardCentralFeign rewardCentralFeign;
	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;

	/**
	 * @param proximityBuffer The proximity buffer
	 */
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	/**
	 * @param user User UUID
	 *             <p>
	 *             Calculate the rewards for one user according to visited location
	 *             </p>
	 */
	public void calculateRewards(User user) {
		logger.info("Calculating reward for {}", user.getUserName());
		CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<Attraction> attractions = new CopyOnWriteArrayList<>();
		userLocations.addAll(user.getVisitedLocations());
		attractions.addAll(gpsUtilFeign.getAttractions());
		for (VisitedLocation visitedLocation : userLocations) {
			for (Attraction attraction : attractions) {
				if (user.getUserRewards().stream()
						.noneMatch(r -> r.attraction.getAttractionName().equals(attraction.getAttractionName()))
						&& nearAttraction(visitedLocation, attraction)) {
					user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
				}
			}
		}
	}

	/**
	 * @param user        User UUID
	 * @param attractions The list of attraction of the providers
	 *                    <p>
	 *                    Method used only when calculating all rewards for all
	 *                    users
	 *                    </p>
	 * 
	 */
	public void calculateAllRewards(User user, List<Attraction> attractions) {
		logger.info("Calculating reward for {}", user.getUserName());
		CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();
		userLocations.addAll(user.getVisitedLocations());
		userLocations.stream().forEach(visitedLocation -> attractions.stream().forEach(attraction -> {
			if (user.getUserRewards().stream()
					.noneMatch(r -> r.attraction.getAttractionName().equals(attraction.getAttractionName()))
					&& nearAttraction(visitedLocation, attraction)) {
				user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
			}
		}));
	}

	/**
	 * @param users List of user to calculate
	 *              <p>
	 *              Multiple threads, limited in a time of minutes
	 *              </p>
	 */
	public void calculateAllRewards(List<User> users) {
		int minutesWait = 20;
		ExecutorService executorService = Executors.newFixedThreadPool(1000);
		logger.info("Calculating  all the user rewards");
		List<Attraction> attractions = gpsUtilFeign.getAttractions();
		for (User user : users) {
			Runnable runnableTask = () -> {
				Locale.setDefault(new Locale("en", "US"));
				calculateAllRewards(user, attractions);
			};
			executorService.execute(runnableTask);
		}
		Utils.awaitTerminationAfterShutdown(executorService, minutesWait);
		executorService.shutdown();
		logger.info("*************** Reward Calculation finished ****************");
	}

	/**
	 * @param attraction The attraction
	 * @param location The location
	 * @return Boolean if within range
	 */
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) < attractionProximityRange;
	}

	/**
	 * @param visitedLocation the visited location
	 * @param attraction the attraction
	 * @return Boolean if within range
	 */
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.getLocation()) < proximityBuffer;
	}

	/**
	 * @param attraction The attraction
	 * @param user The user UUID
	 * @return The points from visiting an attraction
	 */
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardCentralFeign.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	/**
	 * @param attraction The attraction
	 * @param loc2 The second location
	 * @return the value of the distance in miles
	 */
	public double getDistance(Attraction attraction, Location loc2) {
		double lat1 = Math.toRadians(attraction.getLatitude());
		double lon1 = Math.toRadians(attraction.getLongitude());
		double lat2 = Math.toRadians(loc2.getLatitude());
		double lon2 = Math.toRadians(loc2.getLongitude());

		double angle = Math
				.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;

	}

}
