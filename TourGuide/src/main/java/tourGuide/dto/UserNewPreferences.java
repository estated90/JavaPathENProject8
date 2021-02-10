package tourGuide.dto;


import javax.validation.constraints.Min;

import tourGuide.validators.FieldMatch;

@FieldMatch
public class UserNewPreferences {

	@Min(value = 0, message = "The proximity cannot be negative")
	private int attractionProximity = Integer.MAX_VALUE;
	@Min(value = 0, message = "Negative price are impossible")
	private double lowerPricePoint = 0;
	@Min(value = 0, message = "Negative price are impossible")
	private double highPricePoint = 0;
	@Min(value = 1, message = "Trip duration must be at least one day")
	private int tripDuration = 1;
	@Min(value = 1, message = "Ticket quantity must be at least one")
	private int ticketQuantity = 1;
	@Min(value = 1, message = "One adult must be participating")
	private int numberOfAdults = 1;
	@Min(value = 0, message = "Children cannot be negative")
	private int numberOfChildren = 0;

	public UserNewPreferences() {
	}

	/**
	 * @return the attractionProximity
	 */
	public int getAttractionProximity() {
		return attractionProximity;
	}

	/**
	 * @param attractionProximity the attractionProximity to set
	 */
	public void setAttractionProximity(int attractionProximity) {
		this.attractionProximity = attractionProximity;
	}

	/**
	 * @return the lowerPricePoint
	 */
	public double getLowerPricePoint() {
		return lowerPricePoint;
	}

	/**
	 * @param lowerPricePoint the lowerPricePoint to set
	 */
	public void setLowerPricePoint(double lowerPricePoint) {
		this.lowerPricePoint = lowerPricePoint;
	}

	/**
	 * @return the highPricePoint
	 */
	public double getHighPricePoint() {
		return highPricePoint;
	}

	/**
	 * @param highPricePoint the highPricePoint to set
	 */
	public void setHighPricePoint(double highPricePoint) {
		this.highPricePoint = highPricePoint;
	}

	/**
	 * @return the tripDuration
	 */
	public int getTripDuration() {
		return tripDuration;
	}

	/**
	 * @param tripDuration the tripDuration to set
	 */
	public void setTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
	}

	/**
	 * @return the ticketQuantity
	 */
	public int getTicketQuantity() {
		return ticketQuantity;
	}

	/**
	 * @param ticketQuantity the ticketQuantity to set
	 */
	public void setTicketQuantity(int ticketQuantity) {
		this.ticketQuantity = ticketQuantity;
	}

	/**
	 * @return the numberOfAdults
	 */
	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	/**
	 * @param numberOfAdults the numberOfAdults to set
	 */
	public void setNumberOfAdults(int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	/**
	 * @return the numberOfChildren
	 */
	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	/**
	 * @param numberOfChildren the numberOfChildren to set
	 */
	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

}
