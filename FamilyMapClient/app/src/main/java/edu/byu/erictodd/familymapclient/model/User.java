package edu.byu.erictodd.familymapclient.model;

/**
 * Simple User data object for Family Map
 */
public class User {

    /**
     * Default Constructor
     * Used to create a new User object
     */
    public User() {
    }

    /**
     * Initializes a User object with the following parameters:
     *
     * @param userName  A unique username (non-empty string)
     * @param password  User's password (non-empty string)
     * @param email     User's email address (non-empty string)
     * @param firstName User's first name (non-empty string)
     * @param lastName  User's last name (non-empty string)
     * @param gender    User's gender (string: "f" or "m")
     * @param personID  Unique Person ID assigned to this user's generated Person object
     */
    public User(String userName, String password, String email,
                String firstName, String lastName, String gender, String personID) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    /**
     * Unique user name (non-empty string)
     */
    private String userName;
    /**
     * User's password (non-empty string)
     */
    private String password;
    /**
     * User's email address (non-empty string)
     */
    private String email;
    /**
     * User's first name (non-empty string)
     */
    private String firstName;
    /**
     * User's last name (non-empty string)
     */
    private String lastName;
    /**
     * User's gender (String: "f" or "m")
     */
    private String gender;
    /**
     * Unique Person ID assigned to this User's generated Person Object
     */
    private String personID;


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

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * Determines whether any of the members of a user are invalid/don't meet the API specifications
     *
     * @return false if the User has all valid member variables, true if the User has any invalid member variable
     */
    public boolean isInvalidUser() {
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
        if (personID == null || personID.toString().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Determines how an object is equal to a user
     *
     * @param o Object being compared to this user
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
        User u = (User) o;

        if (u.getUsername().equals(this.getUsername())
                && u.getPassword().equals(this.getPassword())
                && u.getEmail().equals(this.getEmail())
                && u.getFirstName().equals(this.getFirstName())
                && u.getLastName().equals(this.getLastName())
                && u.getGender().equals(this.getGender())
                && u.getPersonID().equals(this.getPersonID())) {
            return true;
        } else {
            return false;
        }
    }
}
