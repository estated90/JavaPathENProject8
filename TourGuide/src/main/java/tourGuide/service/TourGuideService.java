package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.dto.NearbyAttractions;
import tourGuide.dto.UserNewPreferences;
import tourGuide.exception.UserNoTFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;
	private ExecutorService executorService;

	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService, ExecutorService executorService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		this.executorService = executorService;
		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) throws InterruptedException, ExecutionException {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation()
				: trackUserLocation(user).get();
		return visitedLocation;
	}

	public User getUser(String userName) throws UserNoTFoundException {
		User user = internalUserMap.get(userName);
		if (user!=null) {
			return user;
		}else {
			logger.error("User do not exist in DB");
			throw new UserNoTFoundException(userName);
		}
		
	}

	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}

	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public CompletableFuture<VisitedLocation> trackUserLocation(User user) {
		return CompletableFuture.supplyAsync(() -> {
			VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
			user.addToVisitedLocations(visitedLocation);
			rewardsService.calculateRewards(user);
			return visitedLocation;
		}, executorService);

	}

	public List<NearbyAttractions> getNearByAttractions(VisitedLocation visitedLocation, User user) {
		List<NearbyAttractions> nearbyAttractions = new ArrayList<>();
		Map<Double, NearbyAttractions> attractionSorted = new HashMap<>();
		
		for (Attraction attraction : gpsUtil.getAttractions()) {
			NearbyAttractions nearByAttraction = new NearbyAttractions();
			double distance = rewardsService.getDistance(visitedLocation.location, attraction);
			nearByAttraction.setDistance(distance);
			nearByAttraction.setLatitude(attraction.latitude);
			nearByAttraction.setLongitude(attraction.longitude);
			nearByAttraction.setRewardPoints(rewardsService.getRewardPoints(attraction, user));
			nearByAttraction.setVisitedLocation(visitedLocation);
			attractionSorted.put(distance, nearByAttraction);
		}
		int i = 5;
		for (Entry<Double, NearbyAttractions> returnedAttraction : new TreeMap<>(attractionSorted).entrySet()) {
			if (i != 0) {
				nearbyAttractions.add(returnedAttraction.getValue());
				i--;
			} else break;
		}
		return nearbyAttractions;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}

	public UserPreferences updatePreferences(User user, UserNewPreferences userNewPreferences) {
		CurrencyUnit currency = Monetary.getCurrency("USD");
		UserPreferences userPreferences = user.getUserPreferences();
		userPreferences.setAttractionProximity(userNewPreferences.getAttractionProximity());
		userPreferences.setHighPricePoint(Money.of(userNewPreferences.getHighPricePoint(), currency));
		userPreferences.setLowerPricePoint(Money.of(userNewPreferences.getLowerPricePoint(), currency));
		userPreferences.setNumberOfAdults(userNewPreferences.getNumberOfAdults());
		userPreferences.setNumberOfChildren(userNewPreferences.getNumberOfChildren());
		userPreferences.setTicketQuantity(userNewPreferences.getTicketQuantity());
		userPreferences.setTripDuration(userNewPreferences.getTripDuration());
		user.setUserPreferences(userPreferences);
		internalUserMap.put(user.getUserName(), user);
		return user.getUserPreferences();
	}

	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes
	// internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();

	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
					new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
