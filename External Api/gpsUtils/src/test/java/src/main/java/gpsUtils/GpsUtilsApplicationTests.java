package src.main.java.gpsUtils;

import static org.hamcrest.CoreMatchers.is;
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
		mockMvc.perform(
				MockMvcRequestBuilders.get("/getUserLocation").param("uuid", ("bea60f6d-aa4b-496d-87da-4db04b99f2e5")))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.userId", is("bea60f6d-aa4b-496d-87da-4db04b99f2e5")))
				.andExpect(jsonPath("$.location").isMap()).andExpect(jsonPath("$.location.longitude").isNotEmpty())
				.andExpect(jsonPath("$.location.latitude").isNotEmpty())
				.andExpect(jsonPath("$.timeVisited").isNotEmpty());
	}
	
	@Test
	public void whenRequestLocalization_errorParamNull() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/getUserLocation").param("uuid", ""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void whenRequestLocalization_errorParamString() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/getUserLocation").param("uuid", "jon"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void whenRequestAttraction_sentOk() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/getAttractions"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].longitude").isNotEmpty())
				.andExpect(jsonPath("$[0].latitude").isNotEmpty())
				.andExpect(jsonPath("$[0].attractionName").isNotEmpty())
				.andExpect(jsonPath("$[0].city").isNotEmpty())
				.andExpect(jsonPath("$[0].state").isNotEmpty())
				.andExpect(jsonPath("$[0].attractionId").isNotEmpty());
	}

}
