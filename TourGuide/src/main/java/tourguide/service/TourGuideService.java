package tourguide.service;

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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import SharedObject.model.Location;
import SharedObject.model.Provider;
import SharedObject.model.VisitedLocation;
import tourguide.dto.NearbyAttractions;
import tourguide.dto.UserNewPreferences;
import tourguide.exception.LocalisationException;
import tourguide.exception.ProviderNoTFoundException;
import tourguide.exception.RewardException;
import tourguide.exception.UserNoTFoundException;
import tourguide.helper.InternalTestHelper;
import tourguide.model.User;
import tourguide.model.UserPreferences;
import tourguide.model.UserReward;
import tourguide.proxies.GpsUtilFeign;
import tourguide.proxies.TripPriceFeign;
import tourguide.tracker.Tracker;
import tourguide.utils.Utils;

/**
 * @author Nico
 *
 */
@Service
public class TourGuideService {
	@Autowired
	private GpsUtilFeign gpsUtilFeign;
	@Autowired
	private TripPriceFeign tripPricer;
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private RewardsService rewardsService;
	public final Tracker tracker;
	boolean testMode = true;

	/**
	 * @param rewardsService The RewardService
	 */
	public TourGuideService(RewardsService rewardsService) {
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

	/**
	 * @param user The User
	 * @return The list of user rewards
	 * @throws RewardException Raise exception if error
	 */
	public List<UserReward> getUserRewards(User user) throws RewardException {
		try {
			logger.info("Retrieving user with reward for user : {}", user.getUserName());
			return user.getUserRewards();
		} catch (Exception ex) {
			throw new RewardException("Reward was not found", ex.getMessage());
		}
	}

	/**
	 * @param user The User
	 * @return The visited Location
	 * @throws LocalisationException Exception if not found
	 */
	public VisitedLocation getUserLocation(User user) throws LocalisationException {
		logger.info("Retrieving user location for user : {}", user.getUserName());
		try {
			return (user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation() : trackUserLocation(user);
		} catch (Exception ex) {
			logger.error("Localisation of user {} was not retrieved properly", user.getUserName());
			logger.error(ex.getMessage());
			throw new LocalisationException("the localization of user was not retrieved");
		}
	}

	/**
	 * @param userName User name of user
	 * @return The User
	 * @throws UserNoTFoundException If no user found
	 */
	public User getUser(String userName) throws UserNoTFoundException {
		logger.info("Retrieving user with username : {}", userName);
		User user = internalUserMap.get(userName);
		if (user != null) {
			logger.info("User found : {}", user);
			return user;
		} else {
			logger.error("User do not exist in DB : {}", userName);
			throw new UserNoTFoundException(userName);
		}

	}

	/**
	 * @return All users
	 * @throws UserNoTFoundException If no user found
	 */
	public List<User> getAllUsers() throws UserNoTFoundException {
		logger.info("Retrieving all users");
		try {
			return internalUserMap.values().stream().collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error("Retreval of all user failed");
			throw new UserNoTFoundException("Error while retrieving all users");
		}
	}

	/**
	 * @param user The User
	 * @throws UserNoTFoundException if user already in DB
	 */
	public void addUser(User user) throws UserNoTFoundException {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		} else {
			logger.error("User already in DB : {}", user.getUserName());
			throw new UserNoTFoundException("User already exists in DB");
		}
	}

	/**
	 * @param user The User
	 * @return The list of providers
	 * @throws ProviderNoTFoundException Exception if result is null
	 */
	public List<Provider> getTripDeals(User user) throws ProviderNoTFoundException {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
		if (tripPricer.getPrice(TRIPPRICERAPIKEY, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(),
				cumulatativeRewardPoints) != null) {
			List<Provider> providers = tripPricer.getPrice(TRIPPRICERAPIKEY, user.getUserId(),
					user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
					user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
			user.setTripDeals(providers);
			return providers;
		} else {
			logger.error("No provider have been found for user: {}", user.getUserName());
			throw new ProviderNoTFoundException("No provider have been found");
		}
	}

	/**
	 * @param user The User
	 * @return The Visited location
	 * @throws LocalisationException Exception if result is null
	 */
	public VisitedLocation trackUserLocation(User user) throws LocalisationException {
		logger.info("Tracking location user : {}", user.getUserName());
		VisitedLocation visitedLocation = gpsUtilFeign.getUserLocation(user.getUserId());
		if (visitedLocation != null) {
			user.addToVisitedLocations(visitedLocation);
			rewardsService.calculateRewards(user);
			return visitedLocation;
		} else {
			logger.error("No localization have been found for user: {}:", user.getUserName());
			throw new LocalisationException("No localisation have been found");
		}
	}

	/**
	 * @param users The list of Users
	 */
	public void trackAllUserLocation(List<User> users) {
		int minutesWait = 15;
		ExecutorService executorService = Executors.newFixedThreadPool(1000);
		logger.info("Trackig all user location asynchroniously");
		for (User user : users) {
			Runnable runnableTask = () -> {
				Locale.setDefault(new Locale("en", "US"));
				try {
					trackUserLocation(user);
				} catch (LocalisationException e) {
					logger.error("No localization have been found while tracking all users");
					logger.error(e.getMessage());
				}
			};
			executorService.execute(runnableTask);
		}
		Utils.awaitTerminationAfterShutdown(executorService, minutesWait);
		executorService.shutdown();
		logger.info("*************** Tracking users finished ****************");
	}

	/**
	 * @param visitedLocation The visited locqtion
	 * @param user            The User
	 * @return List of attraction near user
	 */
	public List<NearbyAttractions> getNearByAttractions(VisitedLocation visitedLocation, User user) {
		List<NearbyAttractions> nearbyAttractions = new ArrayList<>();
		gpsUtilFeign.getAttractions().forEach(attraction -> {
			NearbyAttractions nearByAttraction = new NearbyAttractions();
			nearByAttraction.setAttractionName(attraction.getAttractionName());
			nearByAttraction.setDistance(rewardsService.getDistance(attraction, visitedLocation.getLocation()));
			nearByAttraction.setAttractionLatitude(attraction.getLatitude());
			nearByAttraction.setAttractionLongitude(attraction.getLongitude());
			nearByAttraction.setRewardPoints(rewardsService.getRewardPoints(attraction, user));
			nearByAttraction.setUserLatitude(visitedLocation.getLocation().getLatitude());
			nearByAttraction.setUserLongitude(visitedLocation.getLocation().getLongitude());
			nearbyAttractions.add(nearByAttraction);
		});
		Collections.sort(nearbyAttractions, Comparator.comparingDouble(NearbyAttractions::getDistance));
		nearbyAttractions.subList(5, nearbyAttractions.size()).clear();
		return nearbyAttractions;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				tracker.stopTracking();
			}
		});
	}

	/**
	 * @param user The user
	 * @param userNewPreferences The DTO user preferences
	 * @return The User preferences
	 */
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

	/**
	 * @return All localization for all users
	 * @throws LocalisationException Exception if no localization found
	 */
	public Map<String, Location> gettAllCurrentLocation() throws LocalisationException {
		logger.info("Getting all the user localisation in DB");
		try {
			Map<String, Location> allUsersLocation = new HashMap<>();
			List<User> users = getAllUsers();
			for (User user : users) {
				allUsersLocation.put(user.getUserId().toString(), user.getLastVisitedLocation().getLocation());
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
	private static final String TRIPPRICERAPIKEY = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes
	// internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();

	/**
	 * @return the internalUserMap
	 */
	public Map<String, User> getInternalUserMap() {
		return internalUserMap;
	}

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
		logger.debug("Created {} internal test users.", InternalTestHelper.getInternalUserNumber());
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
				new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime())));
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
