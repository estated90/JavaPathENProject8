package RewardsService.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import rewardCentral.RewardCentral;

@RestController
public class ControllerReward {

	private Logger logger = LoggerFactory.getLogger(ControllerReward.class);
	@Autowired
	private RewardCentral rewardCentral;
	
	@RequestMapping(value = "/getAttractionRewardPoints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	private int getAttractionRewardPoints(UUID attractionId, UUID userId) {
		logger.info("calculating reward for attraction : {} and user : {}", attractionId, userId);
		return rewardCentral.getAttractionRewardPoints(attractionId, userId);
	}
}
