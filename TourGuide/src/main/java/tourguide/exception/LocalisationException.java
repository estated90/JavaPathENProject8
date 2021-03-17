package tourguide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Nico
 * <p>Exception raised when a localization of the user was not done correctly</p>
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LocalisationException extends Exception {

	private final String message;

	private static final long serialVersionUID = 1L;

	/**
	 * @param message to return
	 */
	public LocalisationException(String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return message;
	}

}
