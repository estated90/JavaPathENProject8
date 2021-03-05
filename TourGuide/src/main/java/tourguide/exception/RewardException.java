package tourguide.exception;

public class RewardException extends Exception {

    private static final long serialVersionUID = 1L;  

    private final String message;
    private final String messageError;

    public RewardException(String message, String messageError) {
        this.message = message;
        this.messageError = messageError;
    }

    /**
     * @return the message
     */
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
