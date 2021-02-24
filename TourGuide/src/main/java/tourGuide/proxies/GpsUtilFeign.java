package tourGuide.proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import gpsUtil.location.Attraction;


@FeignClient(value = "microservice-gpsUtil", url = "http://localhost:8081")
public interface GpsUtilFeign {

	@RequestMapping(method = RequestMethod.GET, value = "/getUserLocation", produces = "application/json")
	String getUserLocation(@RequestParam("userId") String userId);

	@RequestMapping(method = RequestMethod.GET, value = "/getAttractions", produces = "application/json")
	List<Attraction> getAttractions();

}
