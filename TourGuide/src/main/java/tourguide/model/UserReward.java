package tourguide.model;

import SharedObject.model.Attraction;
import SharedObject.model.VisitedLocation;

/**
 * @author Nico
 *
 */
public class UserReward {

	public final VisitedLocation visitedLocation;
	public final Attraction attraction;
	private int rewardPoints;

	/**
	 * @param visitedLocation The visitedLocation
	 * @param attraction The attraction
	 * @param rewardPoints The rewardPoints
	 */
	public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
		super();
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
	}

	/**
	 * @param visitedLocation The visitedLocation
	 * @param attraction The attraction
	 */
	public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		super();
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
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
