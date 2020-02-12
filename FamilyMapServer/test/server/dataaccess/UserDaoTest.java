package server.dataaccess;

import api.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserDaoTest {

    private UserDao userDao;
    DatabaseManager dbManager = new DatabaseManager();

    @Before
    public void setUp() throws Exception {
        dbManager.openConnection();
        userDao = new UserDao(dbManager.getConn());
        userDao.createTable();
        userDao.clearTable();
    }

    @After
    public void tearDown() throws Exception {
        userDao.clearTable();
        dbManager.closeConnection(dbManager.getCommit());
        userDao = null;
    }

    @Test
    public void testReadAndCreateUser() {
        //POSITIVE TESTS
        try {
            //Check a user is being added and read correctly for each data member of the user.
            User user1 = new User("etodd", "password1", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            userDao.createUser(user1);

            assertEquals(user1.getUsername(), userDao.readUser("etodd").getUsername());
            assertEquals(user1.getPassword(), userDao.readUser("etodd").getPassword());
            assertEquals(user1.getEmail(), userDao.readUser("etodd").getEmail());
            assertEquals(user1.getFirstName(), userDao.readUser("etodd").getFirstName());
            assertEquals(user1.getLastName(), userDao.readUser("etodd").getLastName());
            assertEquals(user1.getGender(), userDao.readUser("etodd").getGender());
            assertEquals(user1.getPersonID(), userDao.readUser("etodd").getPersonID());

            assertEquals(user1, userDao.readUser(user1.getUsername()));

            //Check if null is returned if a username is queried that doesn't exist in the database
            assertNull(userDao.readUser("etodd2"));
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS
        //Try adding a null user
        try {
            User user2 = null;
            userDao.createUser(user2);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Cannot add a null User");
        }
        //Try adding a user with invalid information
        try {
            //Check an IllegalArugment is thrown if not all the fields match the specified types
            User user3 = new User(null, "12345", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "Q", null);
            userDao.createUser(user3);
            userDao.readUser(user3.getUsername());

        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "User with invalid information");
            dbManager.setCommit(false);
        }
        //Try reading a null user
        try {
            userDao.readUser(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested username is null");
            dbManager.setCommit(false);
        }
        //Check what happens if I try to add a user with the same username as someone else:
        try {
            //Check a user is being added and read correctly for each data member of the user.
            User user2 = new User("etodd", "password1", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            userDao.createUser(user2);
            dbManager.setCommit(true);

        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Username already taken by another user");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeleteUser() {
        //POSITIVE TEST
        try {
            //Add and delete a user and assert it's no longer there.
            User user1 = new User("etodd", "password1", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            //Add the user to the database
            userDao.createUser(user1);
            //Show that the user was added by asserting not Null
            assertNotNull(userDao.readUser(user1.getUsername()));
            //Delete the User
            userDao.deleteUser("etodd");
            //Show the user is deleted by asserting null
            assertNull(userDao.readUser(user1.getUsername()));
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS

        //Deleting a User that doesn't exist - Should do nothing
        try {
            userDao.deleteUser("DNE");
            dbManager.setCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
        //Delete a null user
        try {
            userDao.deleteUser(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested username is null");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeleteAndCreateTable() {
        //Delete the Table
        try {
            userDao.deleteTable();
            //Try to Read something from it, and get a SQL or missing database error
            userDao.readUser("etodd");
        } catch (SQLException ex) {
            assertEquals(ex.getMessage(), ("[SQLITE_ERROR] SQL error or missing database (no such table: Users)"));
        }
        //Create the Table
        try {
            userDao.createTable();
            //Now read user should return null for a username not in the database,
            //but showing the table exists
            assertNull(userDao.readUser("etodd"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testClearTable() {
        try {
            //Add a few users to the database
            User user1 = new User("etodd", "password1", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            User user2 = new User("jtodd", "password2", "Jeff-Todd@gmail.com",
                    "Jeff", "Todd", "m", UUID.randomUUID().toString());
            User user3 = new User("BTodd", "password3", "ToddB@gmail.com",
                    "B-Do", "Todd", "m", UUID.randomUUID().toString());

            userDao.createUser(user1);
            userDao.createUser(user2);
            userDao.createUser(user3);

            //Show they were added asserting not null
            assertNotNull(userDao.readUser("etodd"));
            assertNotNull(userDao.readUser("jtodd"));
            assertNotNull(userDao.readUser("BTodd"));
            //Clear the table
            userDao.clearTable();
            //Assert read is null
            assertNull(userDao.readUser("etodd"));
            assertNull(userDao.readUser("jtodd"));
            assertNull(userDao.readUser("BTodd"));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


}