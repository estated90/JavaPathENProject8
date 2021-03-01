package RewardsService.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rewardCentral.RewardCentral;

@RestController
public class ControllerReward {

	private Logger logger = LoggerFactory.getLogger(ControllerReward.class);
	@Autowired
	private RewardCentral rewardCentral;

	@RequestMapping(value = "/getAttractionRewardPoints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	private int getAttractionRewardPoints(@RequestParam() UUID attractionId, @RequestParam() UUID userId) {
		int rewards = 0;
		try {
			logger.info("calculating reward for attraction : {} and user : {}", attractionId, userId);
			rewards = rewardCentral.getAttractionRewardPoints(attractionId, userId);
		} catch (Exception ex) {
			logger.error("error while calculating the rewards");
			logger.error(ex.getMessage());
		}
		return rewards;
	}
}
