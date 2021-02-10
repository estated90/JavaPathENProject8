package tourGuide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.dto.Geolocalisation;
import tourGuide.dto.NearbyAttractions;
import tourGuide.dto.UserNewPreferences;
import tourGuide.exception.LocalisationException;
import tourGuide.exception.RewardException;
import tourGuide.exception.UserNoTFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tripPricer.Provider;

public class TestTourGuideService {

    private static Locale locale = new Locale("en", "US");

    @Test
    public void getUserLocation() throws InterruptedException, ExecutionException, RewardException {
	Locale.setDefault(locale);
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
	tourGuideService.tracker.stopTracking();
	assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Test
    public void addUser() throws UserNoTFoundException {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

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
    public void getAllUsers() throws UserNoTFoundException {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

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
    public void trackUser() throws InterruptedException, ExecutionException, RewardException {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

	tourGuideService.tracker.stopTracking();

	assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Test
    public void getNearbyAttractions() throws InterruptedException, ExecutionException, RewardException {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

	List<NearbyAttractions> attractions = tourGuideService.getNearByAttractions(visitedLocation, user);

	tourGuideService.tracker.stopTracking();

	assertEquals(5, attractions.size());
    }

    @Test
    public void getTripDeals() {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

	List<Provider> providers = tourGuideService.getTripDeals(user);

	tourGuideService.tracker.stopTracking();

	assertEquals(5, providers.size());
    }

    @Test
    public void updatePreferences() throws UserNoTFoundException {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	tourGuideService.addUser(user);

	List<User> allUsers = tourGuideService.getAllUsers();
	user = allUsers.get(0);
	UserPreferences userPreferences = user.getUserPreferences();
	CurrencyUnit currency = Monetary.getCurrency("USD");

	assertEquals(Integer.MAX_VALUE, userPreferences.getAttractionProximity());
	assertEquals(Money.of(0, currency), userPreferences.getLowerPricePoint());

	UserNewPreferences newUserPreferences = new UserNewPreferences();
	newUserPreferences.setAttractionProximity(200);
	newUserPreferences.setHighPricePoint(30000);
	newUserPreferences.setLowerPricePoint(100);
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

    @Test
    public void getAllUsersCurrentLocation() throws InterruptedException, ExecutionException, LocalisationException,
	    UserNoTFoundException, RewardException {
	GpsUtil gpsUtil = new GpsUtil();
	RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	InternalTestHelper.setInternalUserNumber(0);
	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	User user = new User(UUID.randomUUID(), "test", "00099", "jon@tourGuide.com");
	User user2 = new User(UUID.randomUUID(), "test2", "000100", "jon2@tourGuide.com");

	tourGuideService.addUser(user);
	tourGuideService.addUser(user2);

	for (int i = 0; i < 3; i++) {
	    tourGuideService.trackUserLocation(user);
	    tourGuideService.trackUserLocation(user2);
	}

	Map<String, Geolocalisation> allUsers = tourGuideService.gettAllCurrentLocation();

	tourGuideService.tracker.stopTracking();

	assertEquals(2, allUsers.size());
	assertTrue(allUsers.containsKey(user.getUserId().toString()));
	assertNotNull(allUsers.get(user2.getUserId().toString()).getLatitude());
	assertNotNull(allUsers.get(user2.getUserId().toString()).getLongitude());

    }

}
