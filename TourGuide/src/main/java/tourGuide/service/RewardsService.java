package tourGuide.service;

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
import tourGuide.exception.RewardException;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.proxies.GpsUtilFeign;
import tourGuide.proxies.RewardCentralFeign;
import tourGuide.utils.Utils;

@Service
public class RewardsService {

	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private Logger logger = LoggerFactory.getLogger(RewardsService.class);
	@Autowired
	private GpsUtilFeign gpsUtilFeign;
	@SuppressWarnings("unused")
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private RewardCentralFeign rewardCentralFeign;

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewards(User user) throws RewardException {
		logger.info("Calculating reward for {}", user.getUserName());
		try {
			CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();
			CopyOnWriteArrayList<Attraction> attractions = new CopyOnWriteArrayList<>();
			userLocations.addAll(user.getVisitedLocations());
			attractions.addAll(gpsUtilFeign.getAttractions());
			userLocations.stream().forEach(visitedLocation -> {
				attractions.stream().forEach(attraction -> {
					if (user.getUserRewards().stream()
							.noneMatch(r -> r.attraction.getAttractionName().equals(attraction.getAttractionName()))
							&& nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(
								new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				});
			});
		} catch (Exception ex) {
			logger.error("Error while calculating the reward for {}", user.getUserName());
			logger.error(ex.getMessage());
			throw new RewardException("Reward was not calculated for " + user.getUserName(), ex.getMessage());
		}
	}
	
	public void calculateAllRewards(User user, List<Attraction> attractions) throws RewardException {
		logger.info("Calculating reward for {}", user.getUserName());
		try {
			CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();
			userLocations.addAll(user.getVisitedLocations());
			userLocations.stream().forEach(visitedLocation -> {
				attractions.stream().forEach(attraction -> {
					if (user.getUserRewards().stream()
							.noneMatch(r -> r.attraction.getAttractionName().equals(attraction.getAttractionName()))
							&& nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(
								new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				});
			});
		} catch (Exception ex) {
			logger.error("Error while calculating the reward for {}", user.getUserName());
			logger.error(ex.getMessage());
			throw new RewardException("Reward was not calculated for " + user.getUserName(), ex.getMessage());
		}
	}

	public void calculateAllRewards(List<User> users) {
		int minutesWait = 20;
		ExecutorService executorService = Executors.newFixedThreadPool(1000);
		logger.info("Calculating  all the user rewards");
		List<Attraction> attractions = gpsUtilFeign.getAttractions();
		for (User user : users) {
			Runnable runnableTask = () -> {
				Locale.setDefault(new Locale("en", "US"));
				try {
					calculateAllRewards(user, attractions);
				} catch (RewardException ex) {
					logger.error("Error while calcuilating the reward for {}", user.getUserName());
				}
			};
			executorService.execute(runnableTask);
		}
		Utils.awaitTerminationAfterShutdown(executorService, minutesWait);
		executorService.shutdown();
		logger.info("*************** Reward Calculation finished ****************");
	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return !(getDistance(attraction, location) > attractionProximityRange);
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return !(getDistance(attraction, visitedLocation.getLocation()) > proximityBuffer);
	}

	public int getRewardPoints(Attraction attraction, User user) {
		return rewardCentralFeign.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

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
