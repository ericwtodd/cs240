package api.result;

import api.model.Person;

import java.util.ArrayList;

/**
 * A simple data object used by the following API's:
 * /person
 * /person/[personID]
 */
public class PersonResult {

    /**
     * Default Constructor
     * Used to create a new PersonResult object
     */
    public PersonResult() {
    }

    //API: Person - Success
    /**
     * An array of Person objects being returned
     * If the service fails, it will be set to null;
     */
    private ArrayList<Person> data;

    public ArrayList<Person> getPersons() {
        return data;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.data = persons;
    }

    //API: Person/[personID] - Success
    /**
     * Simple Person object being returned,
     * If the service fails, it will be set to null
     */
    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    //API: Person - Failure
    //API: Person/[personID] - Failure
    /**
     * A string containing a description of the error
     * If the service succeeds, it will be set to null
     */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
