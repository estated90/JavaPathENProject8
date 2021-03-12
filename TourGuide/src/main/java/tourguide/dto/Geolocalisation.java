package tourguide.dto;

/**
 * @author Nicolas
 *
 */
public class Geolocalisation {

	private double longitude;
	private double latitude;
	
	/**
	 * @param longitude the longitude
	 * @param latitude the latitude
	 */
	public Geolocalisation(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
