package api.request;

import api.model.Event;
import api.model.Person;
import api.model.User;
import api.result.EventResult;
import api.result.PersonResult;
import api.result.UserResult;

import java.util.ArrayList;

/**
 * Simple data object used by the /load API
 */
public class LoadRequest {

    /**
     * Default Constructor
     * Used to create a new LoadRequest object
     */
    public LoadRequest() {
    }

    //API: load - success
    /**
     * Array of User objects to be created
     */
    private ArrayList<User> users;
    /**
     * Array of Person objects to be created
     */
    private ArrayList<Person> persons;
    /**
     * Array of Event objects to be created
     */
    private ArrayList<Event> events;

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    //API: load - failure
    /**
     * Error message provided the LoadService fails
     * Possible Errors: Invalid request data (missing values, invalid values, etc.), Internal Server Error
     */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Determines whether any of the members of a LoadRequest are invalid/don't meet the API specifications
     *
     * @return false if the LoadRequest has all valid member variables,
     * true if the LoadRequest has any invalid member variable
     */
    public boolean isInvalidRequest() {
        //Check to make sure all the User objects are valid
        for (User user : users) {
            if (user.isInvalidUser()) {
                return true;
            }
        }
        //Check to make sure all the Person objects are valid
        for (Person person : persons) {
            if (person.isInvalidPerson()) {
                return true;
            }
        }
        //Check to make sure all the Event objects are valid
        for (Event event : events) {
            if (event.isInvalidEvent()) {
                return true;
            }
        }
        //Otherwise, all the objects' information is valid, and we return false;
        return false;
    }
}
