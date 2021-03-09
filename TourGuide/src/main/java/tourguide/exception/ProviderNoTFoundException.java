package tourguide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProviderNoTFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String message;

	public ProviderNoTFoundException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
