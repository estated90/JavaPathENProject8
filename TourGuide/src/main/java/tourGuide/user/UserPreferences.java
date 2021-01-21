package tourGuide.user;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.constraints.Min;

import org.javamoney.moneta.Money;

public class UserPreferences {

	@Min(value = 0, message = "The proximity cannot be negative")
	private int attractionProximity = Integer.MAX_VALUE;
	private CurrencyUnit currency = Monetary.getCurrency("USD");
	@Min(value = 0, message = "Negative price are impossible")
	private Money lowerPricePoint = Money.of(0, currency);
	@Min(value = 1, message = "The higher price must be higher then the lower")
	private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);
	@Min(value = 1, message = "Trip duration must be at least one day")
	private int tripDuration = 1;
	@Min(value = 1, message = "Ticket quantity must be at least one")
	private int ticketQuantity = 1;
	@Min(value = 1, message = "One adult must be participating")
	private int numberOfAdults = 1;
	@Min(value = 0, message = "Children cannot be negative")
	private int numberOfChildren = 0;

	public UserPreferences() {
	}

	public void setAttractionProximity(int attractionProximity) {
		this.attractionProximity = attractionProximity;
	}

	public int getAttractionProximity() {
		return attractionProximity;
	}

	public Money getLowerPricePoint() {
		return lowerPricePoint;
	}

	public void setLowerPricePoint(Money lowerPricePoint) {
		this.lowerPricePoint = lowerPricePoint;
	}

	public Money getHighPricePoint() {
		return highPricePoint;
	}

	public void setHighPricePoint(Money highPricePoint) {
		this.highPricePoint = highPricePoint;
	}

	public int getTripDuration() {
		return tripDuration;
	}

	public void setTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
	}

	public int getTicketQuantity() {
		return ticketQuantity;
	}

	public void setTicketQuantity(int ticketQuantity) {
		this.ticketQuantity = ticketQuantity;
	}

	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	public void setNumberOfAdults(int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

}
