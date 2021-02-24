package src.main.java.gpsUtils;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GpsUtilsApplication {

	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US")); 	
		SpringApplication.run(GpsUtilsApplication.class, args);
	}

}
