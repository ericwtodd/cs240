package edu.byu.erictodd.familymapclient.model;


import java.util.UUID;

/**
 * A simple data object used by the following API's:
 * /user/register
 * /user/login
 */
public class UserResult {

    /**
     * Default Constructor
     * Used to create a new UserResult Object
     */
    public UserResult() {
    }

    //API: user/login - Success
    //API: user/register - Success
    /**
     * An authToken of the current user (UUID as a String)
     */
    private String authToken;
    /**
     * The unique username of the current User
     */
    private String username;
    /**
     * The personID associated with the Person object of the current User (UUID as a String)
     */
    private String personID;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    //API: user/login - Failure
    //API: user/register - Failure
    /**
     * A string containing a description of the error,
     * If the service succeeds, it will be set to null.
     */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
