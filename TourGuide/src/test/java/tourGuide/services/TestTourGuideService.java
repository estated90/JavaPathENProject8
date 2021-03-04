package tourGuide.services;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import SharedObject.model.Location;
import SharedObject.model.Provider;
import SharedObject.model.VisitedLocation;
import tourGuide.dto.NearbyAttractions;
import tourGuide.dto.UserNewPreferences;
import tourGuide.exception.LocalisationException;
import tourGuide.exception.RewardException;
import tourGuide.exception.UserNoTFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.model.UserPreferences;
import tourGuide.service.TourGuideService;

@DisplayName("Tour guide services Tests")
@SpringBootTest
public class TestTourGuideService {

	private static Locale locale = new Locale("en", "US");
	@Autowired
	private TourGuideService tourGuideService;
	private User user;
	private User user2;
	
	@BeforeAll
	public static void setUp() {
		InternalTestHelper.setInternalUserNumber(0);
		Locale.setDefault(locale);
	}
	
	@BeforeEach
	public void setUpBeforeEach() {
		user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
	}
	
	@AfterEach
	public void cleanUp() {
		tourGuideService.getInternalUserMap().remove(user.getUserName());
		tourGuideService.getInternalUserMap().remove(user2.getUserName());
	}

	@Test
	public void getUserLocation() throws InterruptedException, ExecutionException, RewardException {
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertEquals(user.getUserId(), visitedLocation.getUserId());
	}

	@Test
	public void addUser() throws UserNoTFoundException {
		User user3 = new User(UUID.randomUUID(), "jon3", "000", "jon2@tourGuide.com");
		tourGuideService.addUser(user3);
		
		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());
		User retrivedUser3 = tourGuideService.getUser(user3.getUserName());

		tourGuideService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
		assertEquals(user3, retrivedUser3);
		
		tourGuideService.getInternalUserMap().remove(user3.getUserName());
	}

	@Test
	public void getAllUsers() throws UserNoTFoundException {
		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() throws InterruptedException, ExecutionException, RewardException {
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.getUserId());
	}

	@Test
	public void getNearbyAttractions() throws InterruptedException, ExecutionException, RewardException {
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		List<NearbyAttractions> attractions = tourGuideService.getNearByAttractions(visitedLocation, user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}

	@Test
	public void getTripDeals() {
		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, providers.size());
	}

	@Test
	public void updatePreferences() throws UserNoTFoundException {
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

		for (int i = 0; i < 3; i++) {
			tourGuideService.trackUserLocation(user);
			tourGuideService.trackUserLocation(user2);
		}

		Map<String, Location> allUsers = tourGuideService.gettAllCurrentLocation();

		tourGuideService.tracker.stopTracking();

		assertEquals(3, allUsers.size());
		assertTrue(allUsers.containsKey(user.getUserId().toString()));
		assertNotNull(allUsers.get(user2.getUserId().toString()).getLongitude());
		assertNotNull(allUsers.get(user2.getUserId().toString()).getLatitude());

	}

}
