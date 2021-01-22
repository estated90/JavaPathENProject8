/**
 * 
 */
package tourGuide.Controller;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.Application;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

/**
 * @author nicol
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
@WebMvcTest
class TourGuideControllerTest2 {

	private ExecutorService executorService = Executors.newFixedThreadPool(1000);

	@Autowired
	private MockMvc mockMvc;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void whenPostRequestToPreferencessAndValidPreferences_thenCorrectResponse() throws Exception {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.addUser(user);

		MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, Charset.forName("UTF-8"));
		String preferences = "{\"attractionProximity\":\"1\",\"lowerPricePoint\":\"100\",\"highPricePoint\":\"1000\",\"tripDuration\":\"0\",\"ticketQuantity\":\"0\",\"numberOfAdults\":\"0\",\"numberOfChildren\":\"0\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/postPreferences").content(preferences)
				.contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(textPlainUtf8));
	}
	
	@Test
	public void whenPostRequestToPreferencessAndValidPreferences_thenWrongResponse() throws Exception {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executorService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executorService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.addUser(user);

		MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, Charset.forName("UTF-8"));
		String preferences = "{\"attractionProximity\":\"1\",\"lowerPricePoint\":\"100000\",\"highPricePoint\":\"1000\",\"tripDuration\":\"0\",\"ticketQuantity\":\"0\",\"numberOfAdults\":\"0\",\"numberOfChildren\":\"0\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/postPreferences").content(preferences)
				.contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(textPlainUtf8));
	}

}
