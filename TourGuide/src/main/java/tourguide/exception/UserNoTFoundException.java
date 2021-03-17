package tourguide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Nico
 * <p>Exception raised when a user was not found correctly</p>
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNoTFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String userName;

	/**
	 * @param userName Usernane received
	 */
	public UserNoTFoundException(String userName) {
		this.userName = userName;
	}

	@Override
	public String getMessage() {
		return userName;
	}

}
