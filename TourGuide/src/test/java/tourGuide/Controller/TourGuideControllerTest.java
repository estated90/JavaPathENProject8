/**
 * 
 */
package tourGuide.Controller;


import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import tourGuide.exception.ApiErrorResponse;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

/**
 * @author nicol
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TourGuideControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TourGuideService tourGuideService;

    /**
     * @throws java.lang.Exception
     */
    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    public void whenPostRequestToPreferencessAndValidPreferences_thenCorrectResponse() throws Exception {
	String preferences = "{\"attractionProximity\":\"1\",\"lowerPricePoint\":\"100\",\"highPricePoint\":\"1000\",\"tripDuration\":\"1\",\"ticketQuantity\":\"1\",\"numberOfAdults\":\"1\",\"numberOfChildren\":\"0\"}";
	mockMvc.perform(MockMvcRequestBuilders.post("/postPreferences").param("userName", ("internalUser80"))
		.content(preferences).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void whenPostRequestToPreferencessAndInialidPreferences_thenWrongResponse() throws Exception {
	String preferences = "{\"attractionProximity\":\"-1\",\"lowerPricePoint\":\"100000\",\"highPricePoint\":\"1000\",\"tripDuration\":\"0\",\"ticketQuantity\":\"0\",\"numberOfAdults\":\"0\",\"numberOfChildren\":\"-1\"}";
	mockMvc.perform(MockMvcRequestBuilders.post("/postPreferences").param("userName", ("internalUser1")).content(preferences)
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(MockMvcResultMatchers.status().isBadRequest())
		.andExpect(jsonPath("$.timestamp", is(notNullValue()))).andExpect(jsonPath("$.status", is(400)))
		.andExpect(jsonPath("$.errors").isArray()).andExpect(jsonPath("$.errors", hasSize(6)))
		.andExpect(jsonPath("$.errors", hasItem("The proximity cannot be negative")))
		.andExpect(jsonPath("$.errors", hasItem("The higher price must be superior to the lower")))
		.andExpect(jsonPath("$.errors", hasItem("Trip duration must be at least one day")))
		.andExpect(jsonPath("$.errors", hasItem("Ticket quantity must be at least one")))
		.andExpect(jsonPath("$.errors", hasItem("One adult must be participating")))
		.andExpect(jsonPath("$.errors", hasItem("Children cannot be negative")));
    }

    @Test
    public void whenGettingAllUsers_thenCorrectResponse() throws Exception {
	User user = tourGuideService.getUser("internalUser80");
	String id = user.getUserId().toString();
	mockMvc.perform(MockMvcRequestBuilders.get("/getAllCurrentLocations"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(jsonPath("$."+id).isMap())
		.andExpect(jsonPath("$."+id+".longitude").isNotEmpty())
		.andExpect(jsonPath("$."+id+".latitude").isNotEmpty());

    }
    
    @Test
    public void whenPostRequestToPreferencessAndInvalidUser_thenThrowErrorResponse() throws Exception {
	String preferences = "{\"attractionProximity\":\"1\",\"lowerPricePoint\":\"100\",\"highPricePoint\":\"1000\",\"tripDuration\":\"1\",\"ticketQuantity\":\"1\",\"numberOfAdults\":\"1\",\"numberOfChildren\":\"0\"}";
	mockMvc.perform(MockMvcRequestBuilders.post("/postPreferences").param("userName", ("jon"))
		.content(preferences).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(MockMvcResultMatchers.status().isNotFound())
		.andExpect(content().json(MAPPER.writeValueAsString(new ApiErrorResponse("error-0001", "No User found with user name jon"))));
    }

}
