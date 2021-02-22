package tourGuide;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("tourGuide")
public class Application {

    public static void main(String[] args) {
    	Locale.setDefault(new Locale("en", "US")); 	
        SpringApplication.run(Application.class, args);
    }

}
