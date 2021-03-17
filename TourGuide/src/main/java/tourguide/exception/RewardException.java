package tourguide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Nico
 * <p>Exception raised when a reward for the user was not found correctly</p>
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RewardException extends Exception {

    private static final long serialVersionUID = 1L;  

    private final String message;
    private final String messageError;

    /**
     * @param message for user
     * @param messageError Error message
     */
    public RewardException(String message, String messageError) {
        this.message = message;
        this.messageError = messageError;
    }

    /**
     * @return the message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * @return the messageError
     */
    public String getMessageError() {
        return messageError;
    } 
    
}
