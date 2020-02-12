package api.request;

import java.util.UUID;

/**
 * Simple data object used by the user/login API
 */
public class UserLoginRequest {

    /**
     * Default Constructor
     * Used to create new UserLoginRequest objects
     */
    public UserLoginRequest() {
    }

    /**
     * Username of the user logging in (non-empty string)
     */
    private String userName;
    /**
     * Password of the user logging in (non-empty string)
     */
    private String password;

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Determines whether any of the members of a LoginRequest are invalid/don't meet the API specifications
     *
     * @return false if the LoginRequest has all valid member variables,
     * true if the LoginRequest has any invalid member variable
     */
    public boolean isInvalidRequest() {
        if (userName == null || userName.isEmpty()) {
            return true;
        }
        if (password == null || password.isEmpty()) {
            return true;
        }
        return false;
    }
}
