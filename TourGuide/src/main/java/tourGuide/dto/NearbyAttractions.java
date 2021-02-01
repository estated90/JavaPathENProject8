package tourGuide.dto;

public class NearbyAttractions {

	private String attractionName;
	private double attractionLatitude;
	private double attractionLongitude;
	private double userLatitude;
	private double userLongitude;
	private double distance;
	private int rewardPoints;
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
	 * @return the attractionLatitude
	 */
	public double getAttractionLatitude() {
		return attractionLatitude;
	}
	/**
	 * @param attractionLatitude the attractionLatitude to set
	 */
	public void setAttractionLatitude(double attractionLatitude) {
		this.attractionLatitude = attractionLatitude;
	}
	/**
	 * @return the attractionLongitude
	 */
	public double getAttractionLongitude() {
		return attractionLongitude;
	}
	/**
	 * @param attractionLongitude the attractionLongitude to set
	 */
	public void setAttractionLongitude(double attractionLongitude) {
		this.attractionLongitude = attractionLongitude;
	}
	/**
	 * @return the userLatitude
	 */
	public double getUserLatitude() {
		return userLatitude;
	}
	/**
	 * @param userLatitude the userLatitude to set
	 */
	public void setUserLatitude(double userLatitude) {
		this.userLatitude = userLatitude;
	}
	/**
	 * @return the userLongitude
	 */
	public double getUserLongitude() {
		return userLongitude;
	}
	/**
	 * @param userLongitude the userLongitude to set
	 */
	public void setUserLongitude(double userLongitude) {
		this.userLongitude = userLongitude;
	}
	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
	/**
	 * @return the rewardPoints
	 */
	public int getRewardPoints() {
		return rewardPoints;
	}
	/**
	 * @param rewardPoints the rewardPoints to set
	 */
	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	
}
