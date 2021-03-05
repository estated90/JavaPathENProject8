/**
 * 
 */
package tourGuide.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import tourGuide.exception.ApiError;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

/**
 * @author nicol
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Controller Tests")
public class TourGuideControllerTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TourGuideService tourGuideService;
	@Autowired
	private RewardsService rewardService;
	
	@BeforeAll
	public static void setUp() {
		InternalTestHelper.setInternalUserNumber(1);
	}

	@Test
	public void whenGetHomePage_thenGreetingMessage() throws Exception {
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.get("/").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		assertEquals("Greetings from TourGuide!", result.getResponse().getContentAsString());
	}

	@Test
	public void whenGetLocation_thenReturnLocation() throws Exception {
		String username = "internalUser0";
		User user = tourGuideService.getInternalUserMap().get(username);
		mockMvc.perform(MockMvcRequestBuilders.get("/getLocation").param("userName", username)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.userId", is(user.getUserId().toString())));
	}
	
	@Test
	public void whenGetLocationAndNoLocation_thenReturnLocation() throws Exception {
		String username = "internalUser0";
		User user = tourGuideService.getInternalUserMap().get(username);
		mockMvc.perform(MockMvcRequestBuilders.get("/getLocation").param("userName", username)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.userId", is(user.getUserId().toString())));
	}

	@Test
	public void whenPostRequestToPreferencessAndValidPreferences_thenCorrectResponse() throws Exception {
		String preferences = "{\"attractionProximity\":\"1\",\"lowerPricePoint\":\"100\",\"highPricePoint\":\"1000\",\"tripDuration\":\"1\",\"ticketQuantity\":\"1\",\"numberOfAdults\":\"1\",\"numberOfChildren\":\"0\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/postPreferences").param("userName", "internalUser0")
				.content(preferences).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void whenPostRequestToPreferencessAndInialidPreferences_thenWrongResponse() throws Exception {
		String preferences = "{\"attractionProximity\":\"-1\",\"lowerPricePoint\":\"100000\",\"highPricePoint\":\"1000\",\"tripDuration\":\"0\",\"ticketQuantity\":\"0\",\"numberOfAdults\":\"0\",\"numberOfChildren\":\"-1\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/postPreferences").param("userName", ("internalUser0"))
				.content(preferences).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors", hasSize(6)))
				.andExpect(jsonPath("$.errors", hasItem("ticketQuantity: Ticket quantity must be at least one")))
				.andExpect(jsonPath("$.errors", hasItem("attractionProximity: The proximity cannot be negative")))
				.andExpect(jsonPath("$.errors",
						hasItem("userNewPreferences: The higher price must be superior to the lower")))
				.andExpect(jsonPath("$.errors", hasItem("tripDuration: Trip duration must be at least one day")))
				.andExpect(jsonPath("$.errors", hasItem("numberOfAdults: One adult must be participating")))
				.andExpect(jsonPath("$.errors", hasItem("numberOfChildren: Children cannot be negative")));
	}

	@Test
	public void whenGettingNearbyAttractions_thenCorrectResponse() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getNearbyAttractions").param("userName", "internalUser0")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[0].distance").isNumber())
				.andExpect(jsonPath("$[0].attractionName").isString())
				.andExpect(jsonPath("$[0].rewardPoints").isNumber())
				.andExpect(jsonPath("$[0].attractionLongitude").isNumber())
				.andExpect(jsonPath("$[0].attractionLatitude").isNumber())
				.andExpect(jsonPath("$[0].userLatitude").isNumber())
				.andExpect(jsonPath("$[0].userLongitude").isNumber());
	}
	
	@Test
	public void whenGettingRewards_thenCorrectResponse() throws Exception {
		List<User> users = tourGuideService.getAllUsers();
		rewardService.setProximityBuffer(Integer.MAX_VALUE);
		rewardService.calculateAllRewards(users);
		mockMvc.perform(MockMvcRequestBuilders.get("/getRewards").param("userName", "internalUser0")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", hasSize(26)));
	}

	@Test
	public void whenGettingAllUsers_thenCorrectResponse() throws Exception {
		User user = tourGuideService.getUser("internalUser0");
		String id = user.getUserId().toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/getAllCurrentLocations"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$." + id).isMap())
				.andExpect(jsonPath("$." + id + ".longitude").isNotEmpty())
				.andExpect(jsonPath("$." + id + ".longitude").isNumber())
				.andExpect(jsonPath("$." + id + ".latitude").isNotEmpty())
				.andExpect(jsonPath("$." + id + ".latitude").isNumber());
	}
	
	@Test
	public void whenGettingTripDeals_thenCorrectResponse() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getTripDeals").param("userName", "internalUser0"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[0].name").isString())
				.andExpect(jsonPath("$[0].price").isNumber())
				.andExpect(jsonPath("$[0].tripId").isString());
	}
	
	@Test
	public void whenTrackUser_thenCorrectResponse() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/trackUser").param("userName", "internalUser0"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.userId").isString())
				.andExpect(jsonPath("$.location").isMap())
				.andExpect(jsonPath("$.timeVisited").isNumber());
	}

	@Test
	public void whenPostRequestToPreferencessAndInvalidUser_thenThrowErrorResponse() throws Exception {
		String preferences = "{\"attractionProximity\":\"1\",\"lowerPricePoint\":\"100\",\"highPricePoint\":\"1000\",\"tripDuration\":\"1\",\"ticketQuantity\":\"1\",\"numberOfAdults\":\"1\",\"numberOfChildren\":\"0\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/postPreferences").param("userName", ("jon")).content(preferences)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(MockMvcResultMatchers.status().isNotFound()).andExpect(content()
						.json(MAPPER.writeValueAsString(new ApiError(HttpStatus.NOT_FOUND, "jon", "User not Found"))));
	}

	@Test
	public void whenGetRequestToPreferencessAndValidPreferences_thenErrorResponse() throws Exception {
		String preferences = "{\"attractionProximity\":\"1\",\"lowerPricePoint\":\"100\",\"highPricePoint\":\"1000\",\"tripDuration\":\"1\",\"ticketQuantity\":\"1\",\"numberOfAdults\":\"1\",\"numberOfChildren\":\"0\"}";
		mockMvc.perform(MockMvcRequestBuilders.get("/postPreferences").param("userName", ("internalUser0"))
				.content(preferences).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
				.andExpect(jsonPath("$.status", is("METHOD_NOT_ALLOWED")))
				.andExpect(jsonPath("$.message", is("Request method 'GET' not supported")))
				.andExpect(jsonPath("$.errors",
						hasItem("GET method is not supported for this request. Supported methods are POST ")));
	}

}
