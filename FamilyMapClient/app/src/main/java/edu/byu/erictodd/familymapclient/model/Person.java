package edu.byu.erictodd.familymapclient.model;

/**
 * Simple Person data object for Family Map
 */
public class Person {

    /**
     * Default Constructor
     * Used to create a new Person object
     */
    public Person() {
    }

    /**
     * Initializes a Person object with the following parameters:
     *
     * @param personID   Unique identifier for this person (non-empty string)
     * @param descendant User (username) to which this person belongs
     * @param firstName  Person's first name (non-empty string)
     * @param lastName   Person's last name (non-empty string)
     * @param gender     Person's gender (string: "f" or "m")
     * @param father     ID of person's father (possibly null)
     * @param mother     ID of person's mother (possibly null)
     * @param spouse     ID of persons' spouse (possibly null)
     */
    public Person(String personID, String descendant, String firstName, String lastName,
                  String gender, String father, String mother, String spouse) {
        this.personID = personID;
        this.descendant = descendant;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.father = father;
        this.mother = mother;
        this.spouse = spouse;
    }

    /**
     * Unique identifier for this person (non-empty string) (UUID as a String)
     */
    private String personID;
    /**
     * User(username) to which this person belongs
     */
    private String descendant;
    /**
     * Person's first name (non-empty string)
     */
    private String firstName;
    /**
     * Person's last name (non-empty string)
     */
    private String lastName;
    /**
     * Person's gender (string: "f" or "m")
     */
    private String gender;
    /**
     * ID of person's father (possibly null) (UUID as a String)
     */
    private String father;
    /**
     * ID of person's mother (possibly null) (UUID as a String)
     */
    private String mother;
    /**
     * ID of person's spouse (possibly null) (UUID as a String)
     */
    private String spouse;

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getDescendant() {
        return descendant;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
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

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getSpouse() {
        return spouse;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }


    /**
     * Determines whether any of the members of a Person are invalid/don't meet the API specifications
     *
     * @return false if the Person has all valid member variables,
     * true if the Person has any invalid member variable
     */
    public boolean isInvalidPerson() {
        if (personID == null || personID.isEmpty()) {
            return true;
        }
        if (descendant == null || descendant.isEmpty()) {
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
        //Father, Mother, and Spouse can be null, so we don't validate those here.
        return false;
    }

    /**
     * Determines how an object is equal to a person
     *
     * @param o Object being compared to this person
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
        Person person = (Person) o;

        if (person.getPersonID().equals(this.personID)
                && person.getDescendant().equals(this.getDescendant())
                && person.getFirstName().equals(this.getFirstName())
                && person.getLastName().equals(this.getLastName())
                && person.getGender().equals(this.getGender())
                && person.getFather().equals(this.getFather())
                && person.getMother().equals(this.getMother())
                && person.getSpouse().equals(this.getSpouse())) {
            return true;
        } else {
            return false;
        }
    }
}
