package tourGuide.model;

import java.util.Date;
import java.util.UUID;

public class VisitedLocation {

	private UUID userId;
	private Location location;
	private Date timeVisited;
	
	public VisitedLocation() {
		super();
	}

	public VisitedLocation(UUID userId, Location location, Date timeVisited) {
		super();
		this.userId = userId;
		this.location = location;
		this.timeVisited = timeVisited;
	}

	/**
	 * @return the userId
	 */
	public UUID getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the timeVisited
	 */
	public Date getTimeVisited() {
		return timeVisited;
	}

	/**
	 * @param timeVisited the timeVisited to set
	 */
	public void setTimeVisited(Date timeVisited) {
		this.timeVisited = timeVisited;
	}
	
	
	
}
