package RewardsService.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerRewardTest {

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Locale.setDefault(new Locale("en", "US"));
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void whenRequestRewardsPoint_sentOk() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getAttractionRewardPoints")
				.param("attractionId", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5"))
				.param("userId", ("084291e4-c10b-479f-8c38-c50abaca9f89")))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void whenRequestRewardPointWrongUUID_sentBadRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getAttractionRewardPoints").param("attractionId", (""))
				.param("userId", (""))).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.message", is("Required UUID parameter 'attractionId' is not present")))
				.andExpect(jsonPath("$.errors", hasItem("attractionId parameter is missing")));
	}

	@Test
	public void whenRequestRewardsPointAsPost_sentBadRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/getAttractionRewardPoints")
				.param("attractionId", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5"))
				.param("userId", ("084291e4-c10b-479f-8c38-c50abaca9f89")))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
				.andExpect(jsonPath("$.status", is("METHOD_NOT_ALLOWED")))
				.andExpect(jsonPath("$.message", is("Request method 'POST' not supported")))
				.andExpect(jsonPath("$.errors",
						hasItem("POST method is not supported for this request. Supported methods are GET ")));
	}

	@Test
	public void whenRequestRewardsPointBadParameter_sentBadRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getAttractionRewardPoints")
				.param("attractionId", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5")).param("userId", ("jon")))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.message", is(
						"Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; nested exception is java.lang.IllegalArgumentException: Invalid UUID string: jon")))
				.andExpect(jsonPath("$.errors",
						hasItem("userId should be of type java.util.UUID")));
	}
}
