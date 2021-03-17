package tourguide.proxies;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Nico
 * <p>Feign instance to connect to microservice RewardCentral</p>
 *
 */
@Service
@FeignClient(value = "microservice-rewardcentral", url = "http://localhost:8082")
public interface RewardCentralFeign {

	/**
	 * @param attractionId The attraction UUID
	 * @param userId The User UUID
	 * @return The int value of reward
	 */
	@GetMapping(value = "/getAttractionRewardPoints", produces = MediaType.APPLICATION_JSON_VALUE)
	int getAttractionRewardPoints(@RequestParam("attractionId") UUID attractionId, @RequestParam("userId") UUID userId);
}
