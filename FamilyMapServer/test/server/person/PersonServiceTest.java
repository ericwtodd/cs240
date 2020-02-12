package server.person;

import api.model.AuthToken;
import api.model.Person;
import api.result.PersonResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.dataaccess.DatabaseManager;
import server.dataaccess.PersonDao;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class PersonServiceTest {

    private DatabaseManager dbManager;
    private PersonDao personDao;
    private PersonResult personResult;
    private PersonService personService;

    @Before
    public void setUp() throws Exception {
        dbManager = new DatabaseManager();
        dbManager.openConnection();
        personService = new PersonService();
        personResult = new PersonResult();
        personDao = new PersonDao(dbManager.getConn());
    }

    @After
    public void tearDown() throws Exception {
        dbManager.openConnection();
        personDao = new PersonDao(dbManager.getConn());
        personDao.clearTable();
        dbManager.closeConnection(true);
    }

    @Test
    public void getFamily() {
        //POSITIVE TESTS
        try {
            //Get an authToken from a user
            AuthToken personToken = new AuthToken(UUID.randomUUID().toString(), "etodd");
            //Make sure that this user has some persons already in the database:
            Person person1 = new Person(UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            Person person2 = new Person(UUID.randomUUID().toString(), "etodd", "David", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), person1.getMother());
            Person person3 = new Person(UUID.randomUUID().toString(), "etodd", "Cindy", "Todd",
                    "f", UUID.randomUUID().toString(), UUID.randomUUID().toString(), person1.getFather());
            personDao.createPerson(person1);
            personDao.createPerson(person2);
            personDao.createPerson(person3);

            //Close the database connection so the service can connect
            dbManager.closeConnection(true);
            //Call the Person Service
            personResult = personService.getFamily(personToken);

            assertNull(personResult.getMessage());
            assertNull(personResult.getPerson());
            assertNotNull(personResult.getPersons());
            for (Person person : personResult.getPersons()) {
                //Should have the same descendant for all persons in the returned personResult
                assertEquals("etodd", person.getDescendant());
            }
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }

        //NEGATIVE TESTS
        //Try using an invalid AuthToken to request a person
        try {
            AuthToken personToken = new AuthToken(UUID.randomUUID().toString(), "");
            personResult = personService.getFamily(personToken);
            assertEquals(personResult.getMessage(), "AuthToken is invalid");
            assertNull(personResult.getPerson());
            assertNull(personResult.getPersons());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try requesting persons for a user that doesn't have any
        try {
            AuthToken personToken = new AuthToken(UUID.randomUUID().toString(), "Jose");
            personResult = personService.getFamily(personToken);
            assertEquals(personResult.getMessage(), "No Associated Persons Found for this User");
            assertNull(personResult.getPerson());
            assertNull(personResult.getPersons());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void getPerson() {
        //POSITIVE TEST
        try {
            //Create an AuthToken like if a User were logging in
            AuthToken personToken = new AuthToken(UUID.randomUUID().toString(), "etodd");
            Person person1 = new Person(UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person1);
            dbManager.closeConnection(true);

            //Run the personService and get a result
            personResult = personService.getPerson(personToken, person1.getPersonID());

            //Assert that the returned person is the one added earlier, and the other members
            //Of the personResult are null so that the single person is returned.
            dbManager.openConnection();
            personDao = new PersonDao(dbManager.getConn());
            assertEquals(personResult.getPerson(), person1);
            assertNull(personResult.getPersons());
            assertNull(personResult.getMessage());

            dbManager.closeConnection(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }

        //NEGATIVE TESTS
        //Try using an invalid AuthToken to request a person
        try {
            AuthToken personToken = new AuthToken(UUID.randomUUID().toString(), "");

            personResult = personService.getPerson(personToken, UUID.randomUUID().toString());
            assertEquals(personResult.getMessage(), "AuthToken is invalid");
            assertNull(personResult.getPerson());
            assertNull(personResult.getPersons());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try using an invalid personID to request a person
        try {
            AuthToken personToken = new AuthToken(UUID.randomUUID().toString(), "etodd");

            personResult = personService.getPerson(personToken, "");
            assertEquals(personResult.getMessage(), "Requested personID is invalid");
            assertNull(personResult.getPerson());
            assertNull(personResult.getPersons());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try requesting a person that doesn't belong to the current user
        try {
            dbManager.openConnection();
            personDao = new PersonDao(dbManager.getConn());
            //Create an AuthToken like if a User were logging in
            AuthToken personToken = new AuthToken(UUID.randomUUID().toString(), "etodd");
            Person person1 = new Person(UUID.randomUUID().toString(), "JOSE", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person1);
            dbManager.closeConnection(true);

            //Run the personService and get a result
            personResult = personService.getPerson(personToken, person1.getPersonID());

            //The correct error message appears:
            assertEquals(personResult.getMessage(), "Requested Person does not belong to this User");
            assertNull(personResult.getPersons());
            assertNull(personResult.getPerson());
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }
        //Try requesting a person that doesn't exist
        try {
            AuthToken personToken = new AuthToken(UUID.randomUUID().toString(), "etodd");
            personResult = personService.getPerson(personToken, UUID.randomUUID().toString());
            //The correct error message appears:
            assertEquals(personResult.getMessage(), "This user does not have a person object");
            assertNull(personResult.getPersons());
            assertNull(personResult.getPerson());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}
