package tourGuide;

import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterAll;
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
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
@WebMvcTest
public class TourGuideControllerTest {

	private ExecutorService executorService = Executors.newFixedThreadPool(1000);

	@Autowired
	private TourGuideController tourGuideController;

	@Autowired
	private MockMvc mockMvc;

	@AfterAll
	static void tearDownAfterClass() throws Exception {
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

}
