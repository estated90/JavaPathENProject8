package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import tourGuide.dto.Geolocalisation;
import tourGuide.dto.NearbyAttractions;
import tourGuide.dto.UserNewPreferences;
import tourGuide.exception.LocalisationException;
import tourGuide.exception.UserNoTFoundException;
import tourGuide.exception.RewardException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tourGuide.utils.Utils;
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

	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) throws RewardException {
		try {
			logger.error("Retrieving user with reward for user : {}", user.getUserName());
			return user.getUserRewards();
		} catch (Exception ex) {
			throw new RewardException("Reward was not found", ex.getMessage());
		}
	}

	public VisitedLocation getUserLocation(User user)
			throws InterruptedException, ExecutionException, RewardException, LocalisationException {
		logger.error("Retrieving user location for user : {}", user.getUserName());
		try {
			VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation()
					: trackUserLocation(user);
			return visitedLocation;
		} catch (Exception ex) {
			logger.error("Localisation of user {} was not retrieved properly", user.getUserName());
			throw new LocalisationException("the localization of user was not retrieved");
		}
	}

	public User getUser(String userName) throws UserNoTFoundException {
		logger.error("Retrieving user with username : {}", userName);
		User user = internalUserMap.get(userName);
		if (user != null) {
			logger.error("User found : {}", user);
			return user;
		} else {
			logger.error("User do not exist in DB : {}", userName);
			throw new UserNoTFoundException(userName);
		}

	}

	public List<User> getAllUsers() throws UserNoTFoundException {
		logger.error("Retrieving all users");
		try {
			return internalUserMap.values().stream().collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error("Retreval of all user failed");
			throw new UserNoTFoundException("Error while retrieving all users");
		}
	}

	public void addUser(User user) throws UserNoTFoundException {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		} else {
			logger.error("User already in DB : {}", user.getUserName());
			throw new UserNoTFoundException("User already exists in DB");
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

	public VisitedLocation trackUserLocation(User user) throws RewardException {
		logger.info("Tracking location user : {}", user.getUserName());
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;

	}

	public void trackAllUserLocation(List<User> users) {
		int minutesWait = 15;
		ExecutorService executorService = Executors.newFixedThreadPool(1000);
		logger.info("Trackig all user location asynchroniously");
		for (User user : users) {
			Runnable runnableTask = () -> {
				Locale.setDefault(new Locale("en", "US"));
				try {
					trackUserLocation(user);
				} catch (RewardException ex) {
					logger.error("Error while calcuilating the reward for {} and error {}", user.getUserName(),
							ex.getMessage());
					ex.printStackTrace();
				}
			};
			executorService.execute(runnableTask);
		}
		Utils.awaitTerminationAfterShutdown(executorService, minutesWait);
		executorService.shutdown();
		logger.info("*************** Tracking users finished ****************");
	}

	public List<NearbyAttractions> getNearByAttractions(VisitedLocation visitedLocation, User user) {
		List<NearbyAttractions> nearbyAttractions = new ArrayList<>();
		Map<Double, NearbyAttractions> attractionSorted = new HashMap<>();

		for (Attraction attraction : gpsUtil.getAttractions()) {
			NearbyAttractions nearByAttraction = new NearbyAttractions();
			double distance = rewardsService.getDistance(visitedLocation.location, attraction);
			nearByAttraction.setAttractionName(attraction.attractionName);
			nearByAttraction.setDistance(distance);
			nearByAttraction.setAttractionLatitude(attraction.latitude);
			nearByAttraction.setAttractionLongitude(attraction.longitude);
			nearByAttraction.setRewardPoints(rewardsService.getRewardPoints(attraction, user));
			nearByAttraction.setUserLatitude(visitedLocation.location.latitude);
			nearByAttraction.setUserLongitude(visitedLocation.location.longitude);
			attractionSorted.put(distance, nearByAttraction);
		}
		int i = 5;
		for (Entry<Double, NearbyAttractions> returnedAttraction : new TreeMap<>(attractionSorted).entrySet()) {
			if (i != 0) {
				nearbyAttractions.add(returnedAttraction.getValue());
				i--;
			} else
				break;
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

	public Map<String, Geolocalisation> gettAllCurrentLocation() throws LocalisationException {
		logger.info("Getting all the user localisation in DB");
		try {
			Map<String, Geolocalisation> allUsersLocation = new HashMap<String, Geolocalisation>();
			List<User> users = getAllUsers();
			for (User user : users) {
				List<VisitedLocation> visitedLocation = user.getVisitedLocations();
				Comparator<VisitedLocation> byDate = new Comparator<VisitedLocation>() {
					public int compare(VisitedLocation c1, VisitedLocation c2) {
						return Long.valueOf(c1.timeVisited.getTime()).compareTo(c2.timeVisited.getTime());
					}
				};
				Collections.sort(visitedLocation, byDate.reversed());
				Geolocalisation geolocalisation = new Geolocalisation(visitedLocation.get(0).location.longitude,
						visitedLocation.get(0).location.latitude);
				allUsersLocation.put(user.getUserId().toString(), geolocalisation);
			}
			logger.info("All the user localisation have been retrieved : {}", allUsersLocation);
			return allUsersLocation;
		} catch (Exception ex) {
			logger.error("User localisations were not retrieved in DB");
			throw new LocalisationException(ex.getMessage());
		}
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
		int internalUser = InternalTestHelper.getInternalUserNumber();
		IntStream.range(0, internalUser).forEach(i -> {
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
