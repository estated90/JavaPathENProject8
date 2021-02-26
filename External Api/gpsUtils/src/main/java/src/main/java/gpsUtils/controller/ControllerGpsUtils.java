package src.main.java.gpsUtils.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;

@RestController
public class ControllerGpsUtils {

	private Logger logger = LoggerFactory.getLogger(ControllerGpsUtils.class);
	@Autowired
	private GpsUtil gpsUtil;

	@RequestMapping(value = "/getUserLocation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getUserLocation(@RequestParam String userId) {
		UUID uuid = UUID.fromString(userId);
		logger.info("returning user location for user : {}", uuid);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(gpsUtil.getUserLocation(uuid));
			logger.info("Data were successfully retrieved");
			return json;
		} catch (JsonProcessingException e) {
			logger.error("System was enable to convert object to String");
			return null;
		}

	}

	@GetMapping("/getAttractions")
	public List<Attraction> getAttreactions() {
		logger.info("returning all attractions");
		List<Attraction> attractions = null;
		try {
			attractions = gpsUtil.getAttractions();
		} catch (Exception ex) {
			logger.error("error while retrieving attractions");
			logger.error(ex.getMessage());
		}
		return attractions;
	}

}
