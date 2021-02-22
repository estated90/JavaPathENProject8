package tourGuide.proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import gpsUtil.location.Attraction;
import tourGuide.model.VisitedLocation;

@FeignClient(value = "externalapi-gpsutil", url = "http://localhost:8081/")
@Service
public interface GpsUtilFeign {

	@RequestMapping(method = RequestMethod.GET, value = "/getUserLocation")
	VisitedLocation getUserLocation(String userId);

	@RequestMapping(method = RequestMethod.GET, value = "/getAttractions")
	List<Attraction> getAttractions();

}
