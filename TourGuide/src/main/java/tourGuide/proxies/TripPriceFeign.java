package tourGuide.proxies;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import SharedObject.model.Provider;

@Service
@FeignClient(value = "microservice-trippricer", url = "http://localhost:8083")
public interface TripPriceFeign {

	@RequestMapping(value = "/getPrice", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	List<Provider> getPrice(@RequestParam("tripPricerApiKey") String tripPricerApiKey, @RequestParam("userId") UUID userId,
			@RequestParam("numberOfAdults") int numberOfAdults, @RequestParam("numberChildren") int numberChildren, @RequestParam("tripDuration") int tripDuration,
			@RequestParam("cumulatativeRewardPoints") int cumulatativeRewardPoints);
}
