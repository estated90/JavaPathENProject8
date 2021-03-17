package tourguide.configuration.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * @author Nicolas
 * 
 *         <p>
 *         Actuator service for health of APIs
 *         </p>
 * 
 */

@Component
public class DownstreamServiceHealthIndicator implements ReactiveHealthIndicator {

	@Override
	public Mono<Health> health() {
		return checkDownstreamServiceHealth().onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()));
	}

	/**
	 * @return information on Health
	 */
	private Mono<Health> checkDownstreamServiceHealth() {
		// we could use WebClient to check health reactively
		return Mono.just(new Health.Builder().up().build());
	}

}
