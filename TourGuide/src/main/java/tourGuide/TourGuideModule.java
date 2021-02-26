package tourGuide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.service.RewardsService;

@Configuration
public class TourGuideModule {

    @Bean
    public ExecutorService getExecutorService() {
	return Executors.newFixedThreadPool(1000);
    }

    @Bean
    public GpsUtil getGpsUtil() {
	return new GpsUtil();
    }

    @Bean
    public RewardsService getRewardsService() {
	return new RewardsService(getRewardCentral());
    }

    @Bean
    public RewardCentral getRewardCentral() {
	return new RewardCentral();
    }

}
