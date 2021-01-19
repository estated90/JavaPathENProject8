package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tripPricer.Provider;

public class TestTourGuideService {

    private ExecutorService executorService = Executors.newFixedThreadPool(1000);
    private static Locale locale = new Locale("en", "US");

    @Test
    public void getUserLocation() throws InterruptedException, ExecutionException {
	Locale.setDefault(locale);
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).get();
	tourGuideService.tracker.stopTracking();
	assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Test
    public void addUser() {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

	tourGuideService.addUser(user);
	tourGuideService.addUser(user2);

	User retrivedUser = tourGuideService.getUser(user.getUserName());
	User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

	tourGuideService.tracker.stopTracking();

	assertEquals(user, retrivedUser);
	assertEquals(user2, retrivedUser2);
    }

    @Test
    public void getAllUsers() {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

	tourGuideService.addUser(user);
	tourGuideService.addUser(user2);

	List<User> allUsers = tourGuideService.getAllUsers();

	tourGuideService.tracker.stopTracking();

	assertTrue(allUsers.contains(user));
	assertTrue(allUsers.contains(user2));
    }

    @Test
    public void trackUser() throws InterruptedException, ExecutionException {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	CompletableFuture<VisitedLocation> visitedLocation = tourGuideService.trackUserLocation(user);

	tourGuideService.tracker.stopTracking();

	assertEquals(user.getUserId(), visitedLocation.get().userId);
    }

    @Test
    public void getNearbyAttractions() throws InterruptedException, ExecutionException {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	CompletableFuture<VisitedLocation> visitedLocation = tourGuideService.trackUserLocation(user);

	List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation.get());

	tourGuideService.tracker.stopTracking();

	assertEquals(5, attractions.size());
    }

    @Test
    public void getTripDeals() {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

	List<Provider> providers = tourGuideService.getTripDeals(user);

	tourGuideService.tracker.stopTracking();

	assertEquals(5, providers.size());
    }
    
    @Test
    public void updatePreferences() {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);
	
	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	tourGuideService.addUser(user);
	
	List<User> allUsers = tourGuideService.getAllUsers();
	user = allUsers.get(0);
	UserPreferences userPreferences = user.getUserPreferences();
	CurrencyUnit currency = Monetary.getCurrency("USD");
	
	assertEquals(Integer.MAX_VALUE, userPreferences.getAttractionProximity());
	assertEquals(Money.of(0, currency), userPreferences.getLowerPricePoint());
	
	UserPreferences newUserPreferences = new UserPreferences();
	newUserPreferences.setAttractionProximity(200);
	newUserPreferences.setHighPricePoint(Money.of(30000, currency));
	newUserPreferences.setLowerPricePoint(Money.of(100, currency));
	newUserPreferences.setNumberOfAdults(8);
	newUserPreferences.setNumberOfChildren(10);
	newUserPreferences.setTicketQuantity(2);
	newUserPreferences.setTripDuration(3);
	
	tourGuideService.updatePreferences(user, newUserPreferences);
	
	user = tourGuideService.getAllUsers().get(0);
	userPreferences = user.getUserPreferences();
	
	assertEquals(200, userPreferences.getAttractionProximity());
	assertEquals(Money.of(30000, currency), userPreferences.getHighPricePoint());
	assertEquals(Money.of(100, currency), userPreferences.getLowerPricePoint());
	assertEquals(8, userPreferences.getNumberOfAdults());
	assertEquals(10, userPreferences.getNumberOfChildren());
	assertEquals(2, userPreferences.getTicketQuantity());
	assertEquals(3, userPreferences.getTripDuration());
	
	List<Provider> providers = tourGuideService.getTripDeals(user);
	
	assertEquals(5, providers.size());
    }

}
