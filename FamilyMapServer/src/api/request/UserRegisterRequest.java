package api.request;

/**
 * Simple data object used by the user/register API
 */
public class UserRegisterRequest {

    /**
     * Default Constructor
     * Used to create new UserRegisterRequest objects
     */
    public UserRegisterRequest() {
    }

    /**
     * Username of a new user registering (non-empty string)
     */
    private String userName;
    /**
     * password of a new user registering (non-empty string)
     */
    private String password;
    /**
     * email of a new user registering (non-empty string)
     */
    private String email;
    /**
     * first name of a new user registering (non-empty string)
     */
    private String firstName;
    /**
     * last name of a new user registering (non-empty string)
     */
    private String lastName;
    /**
     * gender of the new user registering ("m" or "f")
     */
    private String gender;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Determines whether any of the members of the Request are invalid/don't meet the API specifications
     *
     * @return false if the UserRequest has all valid member variables,
     * true if the UserRequest has any invalid member variable
     */
    public boolean isInvalidRequest() {
        if (userName == null || userName.isEmpty()) {
            return true;
        }
        if (password == null || password.isEmpty()) {
            return true;
        }
        if (email == null || email.isEmpty()) {
            return true;
        }
        if (firstName == null || firstName.isEmpty()) {
            return true;
        }
        if (lastName == null || lastName.isEmpty()) {
            return true;
        }
        if (gender == null || gender.isEmpty() || (!gender.equals("m") && !gender.equals("f"))) {
            return true;
        }
        return false;
    }
}
