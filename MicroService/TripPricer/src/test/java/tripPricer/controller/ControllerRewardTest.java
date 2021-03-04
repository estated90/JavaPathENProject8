package tripPricer.controller;

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
	public void whenGettingPrice_sentOk() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getPrice")
				.param("tripPricerApiKey", "test-server-api-key")
				.param("userId", "084291e4-c10b-479f-8c38-c50abaca9f89")
				.param("numberOfAdults", "1")
				.param("numberChildren", "8")
				.param("tripDuration", "4")
				.param("cumulatativeRewardPoints", "300"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].name").isString())
				.andExpect(jsonPath("$[0].price").isNotEmpty())
				.andExpect(jsonPath("$[0].tripId").isNotEmpty());
	}

	@Test
	public void whenGettingPriceWrongParam_sentBadRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getPrice")
				.param("tripPricerApiKey", "test-server-api-key")
				.param("userId", "")
				.param("numberOfAdults", "1")
				.param("numberChildren", "8")
				.param("tripDuration", "4")
				.param("cumulatativeRewardPoints", "300"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.message", is("Required UUID parameter 'userId' is not present")))
				.andExpect(jsonPath("$.errors", hasItem("userId parameter is missing")));
	}

	@Test
	public void whenRequestGettingPricerAsPost_sentBadRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/getPrice")
				.param("tripPricerApiKey", "test-server-api-key")
				.param("userId", "")
				.param("numberOfAdults", "1")
				.param("numberChildren", "8")
				.param("tripDuration", "4")
				.param("cumulatativeRewardPoints", "300"))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
				.andExpect(jsonPath("$.status", is("METHOD_NOT_ALLOWED")))
				.andExpect(jsonPath("$.message", is("Request method 'POST' not supported")))
				.andExpect(jsonPath("$.errors",
						hasItem("POST method is not supported for this request. Supported methods are GET ")));
	}

	@Test
	public void whenRequestRewardsPointBadParameter_sentBadRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getPrice")				
				.param("tripPricerApiKey", "1")
				.param("userId", "084291e4-c10b-479f-8c38-c50abaca9f89")
				.param("numberOfAdults", "1.8")
				.param("numberChildren", "8.2")
				.param("tripDuration", "4")
				.param("cumulatativeRewardPoints", "300"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.message", is(
						"Failed to convert value of type 'java.lang.String' to required type 'int'; nested exception is java.lang.NumberFormatException: For input string: \"1.8\"")))
				.andExpect(jsonPath("$.errors",
						hasItem("numberOfAdults should be of type int")));
	}

	
	
}
