package tourguide.model;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;

/**
 * @author Nico
 *
 */
public class UserPreferences {

	private int attractionProximity = Integer.MAX_VALUE;
	private CurrencyUnit currency = Monetary.getCurrency(Locale.US);
	private Money lowerPricePoint = Money.of(0, currency);
	private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);
	private int tripDuration = 1;
	private int ticketQuantity = 1;
	private int numberOfAdults = 1;
	private int numberOfChildren = 0;

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
	 * @return the currency
	 */
	public CurrencyUnit getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(CurrencyUnit currency) {
		this.currency = currency;
	}

	/**
	 * @return the lowerPricePoint
	 */
	public Money getLowerPricePoint() {
		return lowerPricePoint;
	}

	/**
	 * @param lowerPricePoint the lowerPricePoint to set
	 */
	public void setLowerPricePoint(Money lowerPricePoint) {
		this.lowerPricePoint = lowerPricePoint;
	}

	/**
	 * @return the highPricePoint
	 */
	public Money getHighPricePoint() {
		return highPricePoint;
	}

	/**
	 * @param highPricePoint the highPricePoint to set
	 */
	public void setHighPricePoint(Money highPricePoint) {
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
