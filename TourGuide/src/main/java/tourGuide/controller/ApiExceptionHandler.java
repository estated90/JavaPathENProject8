package tourGuide.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tourGuide.exception.ApiErrorResponse;
import tourGuide.exception.UserNoTFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UserNoTFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
	    UserNoTFoundException ex) {
        ApiErrorResponse response = 
            new ApiErrorResponse("error-0001",
                "No User found with user name " + ex.getId());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
}
