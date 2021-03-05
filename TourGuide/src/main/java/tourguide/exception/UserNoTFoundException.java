package tourguide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNoTFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;  

    private final String userName;

    public UserNoTFoundException(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return userName;
    }

}
