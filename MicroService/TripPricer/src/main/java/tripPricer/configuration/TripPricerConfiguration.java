package tripPricer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tripPricer.TripPricer;

@Configuration
public class TripPricerConfiguration {

	@Bean
	public TripPricer getGpsUtil() {
		return new  TripPricer();
	}

}
