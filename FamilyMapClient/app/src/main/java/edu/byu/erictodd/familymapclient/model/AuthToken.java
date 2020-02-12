package edu.byu.erictodd.familymapclient.model;

/**
 * Simple AuthToken data object for Family Map
 */
public class AuthToken {

    /**
     * Default Constructor
     * Used to create a new AuthToken object
     */
    public AuthToken() {
    }

    /**
     * Initializes an AuthToken object with its three parameters:
     *
     * @param authToken unique String  as an authorization key
     * @param username  unique username associated with a User
     */
    public AuthToken(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    /**
     * Unique ID (UUID as a string) representing an authorization token/key for a login session
     */
    private String authToken;
    /**
     * Unique String representing the User's username associated with the authToken
     */
    private String username;

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

    /**
     * Determines whether any of the members of an AuthToken are invalid/don't meet the API specifications
     *
     * @return false if the AuthToken has all valid member variables,
     * true if the User has any invalid member variable
     */
    public boolean isInvalidToken() {
        if (authToken == null || authToken.isEmpty()) {
            return true;
        }
        if (username == null || username.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Determines how an object is equal to an authToken
     *
     * @param o Object being compared to this authToken
     * @return returns true if the object is equal, returns false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        AuthToken token = (AuthToken) o;

        if (token.getAuthToken().equals(this.getAuthToken()) && token.getUsername().equals(this.getUsername())) {
            return true;
        } else {
            return false;
        }
    }
}
