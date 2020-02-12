package server.person;

import api.model.AuthToken;
import api.model.Person;
import api.result.PersonResult;
import server.dataaccess.DatabaseManager;
import server.dataaccess.PersonDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A service object that either returns a single Person given a specified personID
 * or all People that are family members of the current user, (determined by authToken)
 */
public class PersonService {

    /**
     * Default Constructor
     * Used to create a new PersonService object
     */
    public PersonService() {
    }

    /**
     * Returns all family members of the current user.
     * The current User is determined from the provided auth token
     *
     * @param authToken the auth token provided by the current user.
     * @return an ArrayList of all persons that have User as a descendant
     */
    public PersonResult getFamily(AuthToken authToken) {
        PersonResult personResult = new PersonResult();
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.openConnection();

        try {
            if (authToken.isInvalidToken()) {
                throw new IllegalArgumentException("AuthToken is invalid");
            }
            Connection connection = dbManager.getConn();
            PersonDao personDao = new PersonDao(connection);

            if (personDao.getUserFamily(authToken.getUsername()) != null) {
                //Set the Array of Persons in the Person Result
                personResult.setPersons(personDao.getUserFamily(authToken.getUsername()));
                personResult.setPerson(null);
                personResult.setMessage(null);
                dbManager.closeConnection(true);
            } else {
                throw new IllegalArgumentException("No Associated Persons Found for this User");
            }
        } catch (SQLException | IllegalArgumentException ex) {
            personResult.setPersons(null);
            personResult.setPerson(null);
            personResult.setMessage(ex.getMessage());
            dbManager.closeConnection(false);
        }
        return personResult;
    }

    /**
     * Returns the single Person object with the specified ID
     *
     * @param personID the Person of interest
     * @return PersonResult contains a the desired Person object
     */
    public PersonResult getPerson(AuthToken authToken, String personID) {
        PersonResult personResult = new PersonResult();
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.openConnection();
        try {
            if (authToken.isInvalidToken()) {
                throw new IllegalArgumentException("AuthToken is invalid");
            }
            if (personID == null || personID.isEmpty()) {
                throw new IllegalArgumentException("Requested personID is invalid");
            }
            Connection connection = dbManager.getConn();
            PersonDao personDao = new PersonDao(connection);

            if (personDao.getUserFamily(authToken.getUsername()) != null) {
                if (personDao.readPerson(personID) == null) {
                    throw new IllegalArgumentException("This user does not have a person object");
                }
                if (!personDao.readPerson(personID).getDescendant().equals(authToken.getUsername())) {
                    throw new IllegalArgumentException("Requested Person does not belong to this User");
                }
                //Set the Person in the Person Result
                personResult.setPersons(null);
                personResult.setPerson(personDao.readPerson(personID));
                personResult.setMessage(null);
                dbManager.closeConnection(true);
            } else {
                throw new IllegalArgumentException("Requested Person Not Found");
            }
        } catch (SQLException | IllegalArgumentException ex) {
            personResult.setPersons(null);
            personResult.setPerson(null);
            personResult.setMessage(ex.getMessage());
            dbManager.closeConnection(false);
        }
        return personResult;
    }

}
