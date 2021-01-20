package tourGuide.dto;

import gpsUtil.location.VisitedLocation;

public class NearbyAttractions {

    private double latitude;
    private double longitude;
    private VisitedLocation visitedLocation;
    private double distance;
    private int rewardPoints;

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
     * @return the visitedLocation
     */
    public VisitedLocation getVisitedLocation() {
	return visitedLocation;
    }

    /**
     * @param visitedLocation the visitedLocation to set
     */
    public void setVisitedLocation(VisitedLocation visitedLocation) {
	this.visitedLocation = visitedLocation;
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
