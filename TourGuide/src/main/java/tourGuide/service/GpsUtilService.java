package tourGuide.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GpsUtilService {

    private Logger logger = LoggerFactory.getLogger(GpsUtilService.class);
    private static String propertyFile = System.getProperty("user.dir") + "/src/main/resources/config.properties";

    public HttpResponse<String> getUserLocation(UUID uuid) throws IOException, InterruptedException {
	logger.info("requesting visited location for user id {}", uuid);
	String uuidString = uuid.toString();
	String url = null;
	try (InputStream input = new FileInputStream(propertyFile)) {
	    Properties prop = new Properties();
	    prop.load(input);
	    url = prop.getProperty("gpsutil.host");
	} catch (FileNotFoundException ex) {
	    logger.error("The property file was not found", ex);
	    throw new IOException("The configuration file was not found");
	} catch (IOException ex) {
	    logger.error("Property was not read correctly", ex);
	    throw new IOException("Missing property in configuration file");
	}
	url = url + "/getUserLocation";
	HttpClient client = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
		.build();
	HttpRequest request = HttpRequest.newBuilder().GET().header("uuid", uuidString).uri(URI.create(url)).build();
	return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
