package tourGuide.exception;

public class RewardException extends Exception {

    private static final long serialVersionUID = 1L;  

    private String message;
    private String messageError;

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
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the messageError
     */
    public String getMessageError() {
        return messageError;
    }

    /**
     * @param messageError the messageError to set
     */
    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }

    
    
}
