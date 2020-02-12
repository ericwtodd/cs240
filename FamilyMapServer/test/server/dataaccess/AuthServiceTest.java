package server.dataaccess;

import api.model.AuthToken;
import api.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class AuthServiceTest {

    AuthService authorizer;
    private UserDao userDao;
    private AuthTokenDao authDao;
    private DatabaseManager dbManager;

    @Before
    public void setUp() throws Exception {
        authorizer = new AuthService();
        dbManager = new DatabaseManager();
    }

    @After
    public void tearDown() throws Exception {
        dbManager.openConnection();
        userDao = new UserDao(dbManager.getConn());
        authDao = new AuthTokenDao(dbManager.getConn());
        userDao.clearTable();
        authDao.clearTable();
        dbManager.closeConnection(true);
    }

    @Test
    public void testAuthorizeUser() {
        //POSITIVE
        //Add a User and his AuthToken to the database like he is registered,
        //and then test to see if the user's login authToken is valid
        try {
            dbManager.openConnection();
            userDao = new UserDao(dbManager.getConn());
            authDao = new AuthTokenDao(dbManager.getConn());
            User user1 = new User("etodd", "password1", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            userDao.createUser(user1);
            AuthToken token1 = new AuthToken(UUID.randomUUID().toString(), "etodd");
            authDao.createAuthToken(token1);
            dbManager.closeConnection(true);

            //Call the AuthorizeUser function
            String username = authorizer.authorizeUser(token1.getAuthToken());
            //Assert the user gave a valid token by returning the associated username
            assertEquals(username, token1.getUsername());
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }
    }

    @Test
    public void testAuthorizeUserNegative() {
        //NEGATIVE
        //Try using an AuthToken that is not stored in the database
        try {
            dbManager.openConnection();
            userDao = new UserDao(dbManager.getConn());
            authDao = new AuthTokenDao(dbManager.getConn());
            User user1 = new User("etodd", "password1", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            userDao.createUser(user1);
            AuthToken token1 = new AuthToken(UUID.randomUUID().toString(), "etodd");
            dbManager.closeConnection(true);

            //Assert that the AuthToken was invalid (didn't exist or not associated with the
            // current user) by returning null for the associated Username
            assertNull(authorizer.authorizeUser(token1.getAuthToken()));
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}