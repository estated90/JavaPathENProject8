package tourguide;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Nico
 *
 */
@SpringBootApplication
@EnableFeignClients("tourGuide")
@EnableSwagger2
public class Application {

    public static void main(String[] args) {
    	Locale.setDefault(new Locale("en", "US")); 	
        SpringApplication.run(Application.class, args);
    }
}
