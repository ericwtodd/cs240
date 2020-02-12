package server.dataaccess;

import api.model.Person;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class PersonDaoTest {

    private PersonDao personDao;
    private DatabaseManager dbManager;

    @Before
    public void setUp() throws Exception {
        dbManager = new DatabaseManager();
        dbManager.openConnection();
        personDao = new PersonDao(dbManager.getConn());
        personDao.clearTable();
    }

    @After
    public void tearDown() throws Exception {
        personDao.clearTable();
        dbManager.closeConnection(dbManager.getCommit());
        personDao = null;
    }

    @Test
    public void testReadAndCreatePerson() {
        //POSITIVE TESTS
        try {
            //Check that a person is being added and read correctly
            Person person = new Person(
                    UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person);
            assertEquals(person.getPersonID(), personDao.readPerson(person.getPersonID()).getPersonID());
            assertEquals(person.getDescendant(), personDao.readPerson(person.getPersonID()).getDescendant());
            assertEquals(person.getFirstName(), personDao.readPerson(person.getPersonID()).getFirstName());
            assertEquals(person.getLastName(), personDao.readPerson(person.getPersonID()).getLastName());
            assertEquals(person.getFather(), personDao.readPerson(person.getPersonID()).getFather());
            assertEquals(person.getMother(), personDao.readPerson(person.getPersonID()).getMother());
            assertEquals(person.getSpouse(), personDao.readPerson(person.getPersonID()).getSpouse());

            //Check null is returned when a person isn't found
            String randomPersonID = UUID.randomUUID().toString();
            assertNull(personDao.readPerson(randomPersonID));
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS

        //Try adding a null Person
        try {
            Person person2 = null;
            personDao.createPerson(person2);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Cannot add a null Person");
            dbManager.setCommit(false);
        }
        //Try reading a null Person
        try {
            personDao.readPerson(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested personID is null");
            dbManager.setCommit(false);
        }
        //Try adding a person with invalid information
        try {
            Person person3 = new Person(
                    UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "NONE", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person3);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Person with invalid information");
            dbManager.setCommit(false);
        }
        //Try adding a person that has already been added into the database
        try {
            Person person = new Person(
                    UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            Person person2 = new Person(
                    person.getPersonID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person);
            personDao.createPerson(person2);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "A person with this PersonID already exists");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeletePerson() {
        //POSITIVE TESTS
        //Check a person is deleted correctly
        try {
            Person person = new Person(UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person);
            //Show the person was added by asserting not null
            assertNotNull(personDao.readPerson(person.getPersonID()));
            //Delete the person
            personDao.deletePerson(person.getPersonID());
            //Show the person was deleted by asserting null
            assertNull(personDao.readPerson(person.getPersonID()));
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS
        //Deleting a person that doesn't exist - Should do nothing
        try {
            personDao.deletePerson(UUID.randomUUID().toString());
            dbManager.setCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
        //Deleting a null Person
        try {
            personDao.deletePerson(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested personID is null");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeleteAndCreateTable() {
        //Delete the Table
        try {
            personDao.deleteTable();
            //Try to Read something from it, and get a SQL or missing database error
            personDao.readPerson(UUID.randomUUID().toString());
        } catch (SQLException ex) {
            assertEquals(ex.getMessage(), ("[SQLITE_ERROR] SQL error or missing database (no such table: Persons)"));
        }
        //Create the Table
        try {
            personDao.createTable();
            //Now read person should return null for a personID not in the database,
            //but showing the table exists
            assertNull(personDao.readPerson(UUID.randomUUID().toString()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testClearTable() {
        try {
            //Add a few persons to the database
            Person person = new Person(UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            Person person2 = new Person(UUID.randomUUID().toString(), "etodd", "David", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person);
            personDao.createPerson(person2);

            //Show they were added, asserting not null
            assertNotNull(personDao.readPerson(person.getPersonID()));
            assertNotNull(personDao.readPerson(person2.getPersonID()));
            //Clear the table
            personDao.clearTable();
            //Assert read is now null, showing they were cleared
            assertNull(personDao.readPerson(person.getPersonID()));
            assertNull(personDao.readPerson(person2.getPersonID()));
            dbManager.setCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeleteFamily() {
        //POSITIVE TESTS
        try {
            //Add multiple people for the same family, and another for a different one
            Person person = new Person(UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            Person person2 = new Person(UUID.randomUUID().toString(), "etodd", "David", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), person.getMother());
            Person person3 = new Person(UUID.randomUUID().toString(), "etodd", "Cindy", "Todd",
                    "f", UUID.randomUUID().toString(), UUID.randomUUID().toString(), person.getFather());
            Person person4 = new Person(UUID.randomUUID().toString(), "joseramirez", "Jose", "Ramirez",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person);
            personDao.createPerson(person2);
            personDao.createPerson(person3);
            personDao.createPerson(person4);

            //Show they were added, asserting not null
            assertNotNull(personDao.readPerson(person.getPersonID()));
            assertNotNull(personDao.readPerson(person2.getPersonID()));
            assertNotNull(personDao.readPerson(person3.getPersonID()));
            assertNotNull(personDao.readPerson(person4.getPersonID()));

            //Delete all persons associated with this user
            personDao.deleteFamily("etodd");

            //Assert they were deleted by getting null returned
            assertNull(personDao.readPerson(person.getPersonID()));
            assertNull(personDao.readPerson(person2.getPersonID()));
            assertNull(personDao.readPerson(person3.getPersonID()));
            //And notice that the other family remained untouched.
            assertNotNull(personDao.readPerson(person4.getPersonID()));

            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS
        //Try deleting persons for a username that doesn't exist in the database - should do nothing
        try {
            personDao.deleteFamily("DNE");
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
        //Try deleting persons for a null username
        try {
            personDao.deleteFamily(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested username is null");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testGetUserFamily() {
        //POSITIVE TEST
        //Add Persons for two families, and get the Persons of one family
        try {
            Person person = new Person(UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            Person person2 = new Person(UUID.randomUUID().toString(), "etodd", "David", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), person.getMother());
            Person person3 = new Person(UUID.randomUUID().toString(), "etodd", "Cindy", "Todd",
                    "f", UUID.randomUUID().toString(), UUID.randomUUID().toString(), person.getFather());
            Person person4 = new Person(UUID.randomUUID().toString(), "joseramirez", "Jose", "Ramirez",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person);
            personDao.createPerson(person2);
            personDao.createPerson(person3);
            personDao.createPerson(person4);

            //Get all persons that have "etodd" as their descendant
            ArrayList<Person> persons = personDao.getUserFamily("etodd");

            //Make sure people are getting extracted from the database (the array is non-empty)
            assertFalse(persons.isEmpty());

            //Make sure we're getting the right family, and not the wrong family.
            for (Person p : persons) {
                assertEquals(p.getDescendant(), "etodd");
                assertNotEquals(p.getDescendant(), "joseramirez");
            }

            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS
        //Try getting the persons for a username that doesn't exist in the database - should return null
        try {
            assertNull(personDao.getUserFamily("DNE"));
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
        //Try getting the persons for a null username
        try {
            personDao.getUserFamily(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested username is null");
            dbManager.setCommit(false);
        }
    }
}