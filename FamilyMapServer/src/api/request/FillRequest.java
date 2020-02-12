package api.request;

import api.model.AuthToken;

/**
 * Simple data object used by the /fill/[username]/{generations} API
 */
public class FillRequest {

    //Username is required for a FillRequest object
    /**
     * Username is required and must be associated with a user already in the database
     * If there is any data in the database already associated with the given username, it is deleted
     */
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * generations is an optional parameter in the FillRequest object.
     * This lets the caller specify the number of generations of ancestors to be generated,
     * and must be a non-negative integer (the default is 4)
     */
    private int generations;

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }

    /**
     * Initializes a new FillRequest with a valid username, and default number of generations: 4
     *
     * @param username name associated with a User already registered in the database
     */
    public FillRequest(String username) {
        this.generations = 4; //default is 4
        this.username = username;
    }

    /**
     * Initializes a new FillRequest with a valid username and specified number of generations to fill
     *
     * @param username    name associated with a User already registered in the database
     * @param generations number of generations of ancestors to be generated (non-negative)
     */
    public FillRequest(String username, int generations) {
        this.username = username;
        this.generations = generations;
    }

    /**
     * Determines whether any of the members of the Request are invalid/don't meet the API specifications
     *
     * @return false if the Request has all valid member variables,
     * true if the Request has any invalid member variable
     */
    public boolean isInvalidRequest() {
        if (username == null || username.isEmpty()) {
            return true;
        }
        if (generations < 0) {
            return true;
        }
        return false;
    }
}
