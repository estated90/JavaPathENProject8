package tourGuide.model;

import java.util.Date;
import java.util.UUID;

import gpsUtil.location.Location;

public class VisitedLocation {

    private UUID uuid;
    private Location location;
    private Date dateVisited;
    /**
     * @param uuid
     * @param location
     * @param dateVisited
     */
    public VisitedLocation(UUID uuid, Location location, Date dateVisited) {
	super();
	this.uuid = uuid;
	this.location = location;
	this.dateVisited = dateVisited;
    }
    /**
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }
    /**
     * @param uuid the uuid to set
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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
     * @return the dateVisited
     */
    public Date getDateVisited() {
        return dateVisited;
    }
    /**
     * @param dateVisited the dateVisited to set
     */
    public void setDateVisited(Date dateVisited) {
        this.dateVisited = dateVisited;
    }
    
    
    
}
