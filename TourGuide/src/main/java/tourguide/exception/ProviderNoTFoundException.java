package tourguide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Nico
 * <p>Exception raised when a provider for the user was not found correctly</p>
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProviderNoTFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String message;

	/**
	 * @param message to return
	 */
	public ProviderNoTFoundException(String message) {
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
