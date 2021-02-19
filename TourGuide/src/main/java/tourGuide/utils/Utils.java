package tourGuide.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourGuide.exception.UserNoTFoundException;
import tourGuide.model.User;
import tourGuide.service.TourGuideService;

@Service
public class Utils {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);
    @Autowired
    private TourGuideService tourGuideService;

    public User getUser(String userName) throws UserNoTFoundException {
	logger.info("Getting user {}", userName);
	return tourGuideService.getUser(userName);
    }

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool, int minutes) {
	logger.info("Shuting down threads service");
	threadPool.shutdown();
	try {
	    if (!threadPool.awaitTermination(minutes, TimeUnit.MINUTES)) {
		logger.info("The service time out : {} minutes max", minutes);
		threadPool.shutdownNow();
	    }
	} catch (InterruptedException ex) {
	    logger.info("Unexpected error in the thread shutdown");
	    threadPool.shutdownNow();
	    Thread.currentThread().interrupt();
	}
    }

}
