package src.main.java.gpsUtils.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

@RestController
public class ControllerGpsUtils {

    private Logger logger = LoggerFactory.getLogger(ControllerGpsUtils.class);
    @Autowired
    private GpsUtil gpsUtil;

    @GetMapping("/getUserLocation")
    public VisitedLocation getUserLocation(@RequestParam UUID uuid) {
	if (!(uuid == null)) {
	    logger.info("returning user location for user : {}", uuid);
	    return gpsUtil.getUserLocation(uuid);
	} else {
	    return null;
	}
    }
    
    @GetMapping("/getAttreactions")
    public List<Attraction> getAttreactions() {
	return gpsUtil.getAttractions();
    }

}
