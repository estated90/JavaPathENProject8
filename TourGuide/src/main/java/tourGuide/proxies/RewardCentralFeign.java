package tourGuide.proxies;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(value = "microservice-rewardCentral", url = "http://localhost:8082")
public interface RewardCentralFeign {

	@RequestMapping(value = "/getAttractionRewardPoints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	int getAttractionRewardPoints(@RequestParam("attractionId") UUID attractionId, @RequestParam("userId") UUID userId);
}
