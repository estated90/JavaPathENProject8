package tourGuide.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tourGuide.exception.ApiErrorResponse;
import tourGuide.exception.LocalisationException;
import tourGuide.exception.RewardException;
import tourGuide.exception.UserNoTFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UserNoTFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
	    UserNoTFoundException ex) {
        ApiErrorResponse response = 
            new ApiErrorResponse("error-0001",
                "No User found with user name " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(RewardException.class)
    public ResponseEntity<ApiErrorResponse> handleApiRewardException(
	    RewardException ex) {
        ApiErrorResponse response = 
            new ApiErrorResponse("error-0002",
                "Reward were not returned" + ex.getMessage() +" error message : " + ex.getMessageError());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(LocalisationException.class)
    public ResponseEntity<ApiErrorResponse> handleApiLocalisationException(
	    RewardException ex) {
        ApiErrorResponse response = 
            new ApiErrorResponse("error-0003",
                "Localisation was not found" + ex.getMessage() +" error message : " + ex.getMessageError());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
}
