package tourguide.proxies;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import SharedObject.model.Attraction;
import SharedObject.model.VisitedLocation;

@Service
@FeignClient(value = "microservice-gpsUtil", url = "http://localhost:8081")
public interface GpsUtilFeign {

	@GetMapping(value = "/getUserLocation", produces = MediaType.APPLICATION_JSON_VALUE)
	VisitedLocation getUserLocation(@RequestParam("userId") UUID uuid);

	@GetMapping(value = "/getAttractions", produces = MediaType.APPLICATION_JSON_VALUE)
	List<Attraction> getAttractions();

}
