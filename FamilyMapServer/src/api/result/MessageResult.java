package api.result;

/**
 * Simple data object used by the following API's:
 * /clear - Success and Failure
 * /fill/[username]/{generations} - Success and Failure
 */
public class MessageResult {

    /**
     * Default Constructor
     * Used to create a new MessageResult object
     */
    public MessageResult() {
        message = null;
    }

    /**
     * Initializes a new MessageResult with the message to be returned by the handler
     *
     * @param message the message to be contained in the result to be returned by the handler
     */
    public MessageResult(String message) {
        this.message = message;
    }

    /**
     * String containing information about either a success message, or a description of the error
     */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
