package tourGuide.model;

import java.util.UUID;

public class Attraction extends Location {
	
	private String attractionName;
	private String city;
	private String state;
	public UUID attractionId;
	
	public Attraction() {
		super();
	}
	public Attraction(double longitude, double latitude) {
		super(longitude, latitude);
	}
	public Attraction(String attractionName, String city, String state, UUID attractionId) {
		super();
		this.attractionName = attractionName;
		this.city = city;
		this.state = state;
		this.attractionId = attractionId;
	}
	/**
	 * @return the attractionName
	 */
	public String getAttractionName() {
		return attractionName;
	}
	/**
	 * @param attractionName the attractionName to set
	 */
	public void setAttractionName(String attractionName) {
		this.attractionName = attractionName;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the attractionId
	 */
	public UUID getAttractionId() {
		return attractionId;
	}
	/**
	 * @param attractionId the attractionId to set
	 */
	public void setAttractionId(UUID attractionId) {
		this.attractionId = attractionId;
	}
	
}
