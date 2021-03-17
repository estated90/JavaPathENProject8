package tourguide.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import SharedObject.model.Provider;
import SharedObject.model.VisitedLocation;

/**
 * @author Nico
 *
 */
public class User {
	private final UUID userId;
	private final String userName;
	private String phoneNumber;
	private String emailAddress;
	private Date latestLocationTimestamp;
	private List<VisitedLocation> visitedLocations = new ArrayList<>();
	private List<UserReward> userRewards = new ArrayList<>();
	private UserPreferences userPreferences = new UserPreferences();
	private List<Provider> tripDeals = new ArrayList<>();

	/**
	 * @param userId       UUID of the user
	 * @param userName     Unique user name
	 * @param phoneNumber  Phone number of user
	 * @param emailAddress Email address of the user
	 */
	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the userId
	 */
	public UUID getUserId() {
		return userId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the latestLocationTimestamp
	 */
	public Date getLatestLocationTimestamp() {
		return latestLocationTimestamp;
	}

	/**
	 * @param latestLocationTimestamp the latestLocationTimestamp to set
	 */
	public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
		this.latestLocationTimestamp = latestLocationTimestamp;
	}

	/**
	 * @param visitedLocation the visitedLocation to add
	 */
	public void addToVisitedLocations(VisitedLocation visitedLocation) {
		visitedLocations.add(visitedLocation);
	}

	/**
	 * @return the visitedLocation
	 */
	public List<VisitedLocation> getVisitedLocations() {
		return visitedLocations;
	}

	public void clearVisitedLocations() {
		visitedLocations.clear();
	}

	/**
	 * @param userReward The userReward to add
	 */
	public void addUserReward(UserReward userReward) {
		if (this.userRewards.stream()
				.noneMatch(r -> r.attraction.getAttractionName().equals(userReward.attraction.getAttractionName()))) {
			this.userRewards.add(userReward);
		}
	}

	/**
	 * @return the userReward
	 */
	public List<UserReward> getUserRewards() {
		return userRewards;
	}

	/**
	 * @return the UserPreferences
	 */
	public UserPreferences getUserPreferences() {
		return userPreferences;
	}

	
	/**
	 * @param userPreferences the userPreferences to set
	 */
	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	/**
	 * @return the VisitedLocation
	 */
	public VisitedLocation getLastVisitedLocation() {
		List<VisitedLocation> visitedLocation = getVisitedLocations();
		Comparator<VisitedLocation> byDate = new Comparator<VisitedLocation>() {
			public int compare(VisitedLocation c1, VisitedLocation c2) {
				return Long.valueOf(c1.getTimeVisited().getTime()).compareTo(c2.getTimeVisited().getTime());
			}
		};
		Collections.sort(visitedLocation, byDate.reversed());
		return visitedLocations.get(0);
	}

	/**
	 * @return the tripDeals
	 */
	public List<Provider> getTripDeals() {
		return tripDeals;
	}

	/**
	 * @param tripDeals the tripDeals to set
	 */
	public void setTripDeals(List<Provider> tripDeals) {
		this.tripDeals = tripDeals;
	}



}