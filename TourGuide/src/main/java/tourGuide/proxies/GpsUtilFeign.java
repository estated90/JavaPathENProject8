package tourGuide.proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tourGuide.model.Attraction;
import tourGuide.model.VisitedLocation;


@FeignClient(value = "microservice-gpsUtil", url = "http://localhost:8081")
public interface GpsUtilFeign {

	@RequestMapping(method = RequestMethod.GET, value = "/getUserLocation", produces = "application/json")
	VisitedLocation getUserLocation(@RequestParam("userId") String userId);

	@RequestMapping(method = RequestMethod.GET, value = "/getAttractions", produces = "application/json")
	List<Attraction> getAttractions();

}
