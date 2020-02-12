package server.dataaccess;

import api.model.AuthToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests the AuthTokenDao Class
 */
public class AuthTokenDaoTest {

    AuthTokenDao authDao;
    DatabaseManager dbManager = new DatabaseManager();

    @Before
    public void setUp() throws Exception {
        dbManager.openConnection();
        authDao = new AuthTokenDao(dbManager.getConn());
        authDao.createTable();
        authDao.clearTable();
    }

    @After
    public void tearDown() throws Exception {
        authDao.clearTable();
        dbManager.closeConnection(dbManager.getCommit());
        authDao = null;
    }

    @Test
    public void testCreateAndReadAuthToken() {
        //POSITIVE TESTS
        try {
            //Check authToken is being added and read correctly
            AuthToken token1 = new AuthToken(UUID.randomUUID().toString(), "etodd");
            authDao.createAuthToken(token1);
            AuthToken tokenRead1 = authDao.readAuthToken("etodd");
            assertEquals(token1.getAuthToken(), tokenRead1.getAuthToken());
            assertEquals(token1.getUsername(), tokenRead1.getUsername());

            //Check return of null values when the user isn't found
            AuthToken tokenRead2 = authDao.readAuthToken("etodd3");
            assertNull(tokenRead2);

            //Check if a second token is added for the same user, if it returns the most recent authToken:
            AuthToken token3 = new AuthToken(UUID.randomUUID().toString(), "etodd");
            authDao.createAuthToken(token3);
            assertEquals(token3.getAuthToken(), authDao.readAuthToken("etodd").getAuthToken());
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS
        //Try adding a null token
        try {
            AuthToken token5 = null;
            authDao.createAuthToken(token5);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Cannot add a null Token");
            dbManager.setCommit(false);
        }
        //Try adding a token with invalid values:
        try {
            AuthToken token6 = new AuthToken(UUID.randomUUID().toString(), "");
            authDao.createAuthToken(token6);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Token with invalid information");
            dbManager.setCommit(false);
        }
        //Try reading a null token
        try {
            authDao.readAuthToken(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested username is null");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeleteAuthToken() {
        //POSITIVE TESTS
        //Add a token, remove it and make sure that authToken was deleted from the database.
        try {
            AuthToken token1 = new AuthToken(UUID.randomUUID().toString(), "etodd");
            authDao.createAuthToken(token1);
            //Show that the token was added by asserting not Null
            assertNotNull(authDao.readAuthToken("etodd"));
            //Delete the token
            authDao.deleteAuthToken(token1.getAuthToken());
            //Show that the token was removed from the database by asserting Null
            assertNull(authDao.readAuthToken("etodd"));
            dbManager.setCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS
        //Deleting a Token that doesn't exist - Should do nothing
        try {
            String randomToken = UUID.randomUUID().toString();
            authDao.deleteAuthToken(randomToken);
            dbManager.setCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
        //Delete a null token
        try {
            authDao.deleteAuthToken(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested authToken is null");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeleteAndCreateTable() {
        //Delete the Table
        try {
            authDao.deleteTable();
            //Try to Read something from it, and get a SQL or missing database error
            authDao.readAuthToken("etodd");
        } catch (SQLException ex) {
            assertEquals(ex.getMessage(), ("[SQLITE_ERROR] SQL error or missing database (no such table: AuthTokens)"));
        }
        //Create the Table
        try {
            authDao.createTable();
            //Now read token should return null for a username not in the database
            assertNull(authDao.readAuthToken("etodd"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testClearTable() {
        try {
            //Add a few authTokens to the database
            AuthToken token1 = new AuthToken(UUID.randomUUID().toString(), "etodd");
            AuthToken token2 = new AuthToken(UUID.randomUUID().toString(), "etodd");
            AuthToken token3 = new AuthToken(UUID.randomUUID().toString(), "etodd2");

            //Clear the table
            authDao.clearTable();
            //Assert read is null
            assertNull(authDao.readAuthToken("etodd"));
            assertNull(authDao.readAuthToken("etodd2"));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testAuthorizeUser() {
        //POSITIVE TESTS
        //Create a token, add it to the database, and assert that authToken exists, and returns
        //the corresponding username associated with it.
        try {
            AuthToken token1 = new AuthToken(UUID.randomUUID().toString(), "etodd");
            authDao.createAuthToken(token1);
            assertEquals(authDao.authorizeUser(token1.getAuthToken()), token1.getUsername());
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        //NEGATIVE TESTS
        //Create a random UUID and assert that the token doesn't exist/there is no User
        //Associated with the passed token
        try {
            String token = UUID.randomUUID().toString();
            assertNull(authDao.authorizeUser(token));
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        //Try authorizing a user with a null token, expect an Illegal argument exception
        //saying that the authorization token is null
        try {
            authDao.authorizeUser(null);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Authorization Token is null");
        }

    }

}