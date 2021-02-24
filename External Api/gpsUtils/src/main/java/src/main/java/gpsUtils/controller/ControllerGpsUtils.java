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

import com.jsoniter.output.JsonStream;

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
		return JsonStream.serialize(gpsUtil.getUserLocation(uuid));
	}

	@GetMapping("/getAttractions")
	public List<Attraction> getAttreactions() {
		List<Attraction> attractions = gpsUtil.getAttractions();
		return attractions;
	}

}
