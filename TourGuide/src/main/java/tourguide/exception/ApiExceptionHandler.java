package tourguide.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Nicolas
 * <p>Exception handler for custom exception</p>
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * @param ex Error raised
     * @return the JSON answer
     */
    @ExceptionHandler(UserNoTFoundException.class)
    public ResponseEntity<Object> handleApiException(
	    UserNoTFoundException ex) {
    	final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), "User not Found");
    	return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
    
    /**
     * @param ex Error raised
     * @return the JSON answer
     */
    @ExceptionHandler(RewardException.class)
    public ResponseEntity<Object> handleApiRewardException(
	    RewardException ex) {
    	final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), "No rewards were returned");
    	return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
    
    /**
     * @param ex Error raised
     * @return the JSON answer
     */
    @ExceptionHandler(LocalisationException.class)
    public ResponseEntity<Object> handleApiLocalisationException(
	    RewardException ex) {
    	final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), "Localization didn't work");
    	return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
    
}
