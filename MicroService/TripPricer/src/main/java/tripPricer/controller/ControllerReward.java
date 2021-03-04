package tripPricer.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tripPricer.Provider;
import tripPricer.TripPricer;

@RestController
public class ControllerReward {

	private Logger logger = LoggerFactory.getLogger(ControllerReward.class);
	@Autowired
	private TripPricer tripPricer;

	@RequestMapping(value = "/getPrice", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	private List<Provider> getPrice(@RequestParam String tripPricerApiKey, @RequestParam UUID userId,
			@RequestParam int numberOfAdults, @RequestParam int numberChildren, @RequestParam int tripDuration,
			@RequestParam int cumulatativeRewardPoints) {
		List<Provider> providers = null;
		try {
			logger.info("Getting list of price for providers using :");
			logger.info("API key : {}", tripPricerApiKey);
			logger.info("User : {}", userId);
			logger.info("Number of Adults : {}", numberOfAdults);
			logger.info("Number of children : {}", numberChildren);
			logger.info("Trip duration : {}", tripDuration);
			logger.info("And cumulated point : {}", cumulatativeRewardPoints);
			providers = tripPricer.getPrice(tripPricerApiKey, userId, numberOfAdults, numberChildren, tripDuration,
					cumulatativeRewardPoints);
		} catch (Exception ex) {
			logger.error("An error occured while retrieving the providers");
			logger.error(ex.getMessage());
		}
		return providers;
	}
}
