package gpsUtils;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class GpsUtilsApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	public static void setUp() {
		Locale.setDefault(new Locale("en", "US"));
	}

	@Test
	public void whenRequestLocalization_sentOk() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getUserLocation").param("userId",
				("bea60f6d-aa4b-496d-87da-4db04b99f2e5"))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.userId", is("bea60f6d-aa4b-496d-87da-4db04b99f2e5")))
				.andExpect(jsonPath("$.location").isMap()).andExpect(jsonPath("$.location.longitude").isNotEmpty())
				.andExpect(jsonPath("$.location.latitude").isNotEmpty())
				.andExpect(jsonPath("$.timeVisited").isNotEmpty());
	}

	@Test
	public void whenRequestLocalization_errorWrongParam() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getUserLocation").param("uuid", ""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.message", is("Required UUID parameter 'userId' is not present")))
				.andExpect(jsonPath("$.errors", hasItem("userId parameter is missing")));
	}
	@Test
	
	public void whenRequestLocalization_errorEmptyParam() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getUserLocation").param("userId", ""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.message", is("Required UUID parameter 'userId' is not present")))
				.andExpect(jsonPath("$.errors", hasItem("userId parameter is missing")));
	}

	@Test
	public void whenRequestLocalization_errorParamString() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getUserLocation").param("userId", "jon"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.message", is(
						"Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; nested exception is java.lang.IllegalArgumentException: Invalid UUID string: jon")))
				.andExpect(jsonPath("$.errors",
						hasItem("userId should be of type java.util.UUID")));
	}

	@Test
	public void whenRequestAttraction_sentOk() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getAttractions")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$[0].longitude").isNotEmpty())
				.andExpect(jsonPath("$[0].latitude").isNotEmpty())
				.andExpect(jsonPath("$[0].attractionName").isNotEmpty()).andExpect(jsonPath("$[0].city").isNotEmpty())
				.andExpect(jsonPath("$[0].state").isNotEmpty()).andExpect(jsonPath("$[0].attractionId").isNotEmpty());
	}
	
	@Test
	public void whenRequestAttractionAsPost_sentBadRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/getAttractions"))
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
				.andExpect(jsonPath("$.status", is("METHOD_NOT_ALLOWED")))
				.andExpect(jsonPath("$.message", is("Request method 'POST' not supported")))
				.andExpect(jsonPath("$.errors",
						hasItem("POST method is not supported for this request. Supported methods are GET ")));
	}

}
