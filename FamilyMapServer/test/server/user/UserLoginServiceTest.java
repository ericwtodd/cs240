package server.user;

import api.model.User;
import api.request.UserLoginRequest;
import api.result.UserResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.dataaccess.AuthTokenDao;
import server.dataaccess.AuthTokenDaoTest;
import server.dataaccess.DatabaseManager;
import server.dataaccess.UserDao;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserLoginServiceTest {

    private DatabaseManager dbManager;
    private UserDao userDao;
    private UserResult loginResult;
    private UserLoginService loginService;
    private UserLoginRequest loginRequest;

    @Before
    public void setUp() throws Exception {
        loginRequest = new UserLoginRequest();
        dbManager = new DatabaseManager();
        dbManager.openConnection();
        loginResult = new UserResult();
        userDao = new UserDao(dbManager.getConn());
        userDao.clearTable();
        loginService = new UserLoginService();

    }

    @After
    public void tearDown() throws Exception {
        dbManager.openConnection();
        userDao = new UserDao(dbManager.getConn());
        userDao.clearTable();
        AuthTokenDao authDao = new AuthTokenDao(dbManager.getConn());
        authDao.clearTable();
        dbManager.closeConnection(true);
    }

    @Test
    public void testLogin() {

        //POSITIVE TEST
        try {
            String userName = "etodd";
            String password = "password15";
            loginRequest.setUsername(userName);
            loginRequest.setPassword(password);
            //Create the user that is logging in in the database.
            User user1 = new User(userName, password, "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            userDao.createUser(user1);

            //Commit changes so the user can be added
            dbManager.closeConnection(true);

            //Run the loginService, and output it to a loginResult
            loginResult = loginService.login(loginRequest);
            //Assert that the login was successful, and the personID & an Authtoken are returned.
            assertEquals(loginResult.getUsername(), userName);
            assertNotNull(loginResult.getAuthToken());
            assertNotNull(loginResult.getAuthToken());

            //Assert null to the error message
            assertNull(loginResult.getMessage());
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }

        //NEGATIVE TEST
        //Try logging in with bad request parameters
        try {
            String userName = "";
            String password = "password15";
            loginRequest.setUsername(userName);
            loginRequest.setPassword(password);

            loginResult = loginService.login(loginRequest);

            //The correct error message is contained in the result
            assertEquals(loginResult.getMessage(), "Request has missing or invalid values.");
            //The other values in the result are null
            assertNull(loginResult.getAuthToken());
            assertNull(loginResult.getUsername());
            assertNull(loginResult.getPersonID());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try logging in with the wrong password
        try {
            //Add a new user to the database:
            dbManager.openConnection();
            userDao = new UserDao(dbManager.getConn());
            User user1 = new User("etodd2", "password", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            userDao.createUser(user1);
            //Commit changes so the user can be added
            dbManager.closeConnection(true);

            String userName = "etodd2";
            String password = "wrongPassword";
            loginRequest.setUsername(userName);
            loginRequest.setPassword(password);

            loginResult = loginService.login(loginRequest);

            //The correct error message is contained in the result
            assertEquals(loginResult.getMessage(), "Incorrect Password");
            //The other values in the result are null
            assertNull(loginResult.getAuthToken());
            assertNull(loginResult.getUsername());
            assertNull(loginResult.getPersonID());
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }
        //Try logging in a user that is not in the database
        try {
            String userName = "JOSE";
            String password = "contrasena";
            loginRequest.setUsername(userName);
            loginRequest.setPassword(password);

            loginResult = loginService.login(loginRequest);

            //The correct error message is contained in the result
            assertEquals(loginResult.getMessage(), "User Not Found");
            //The other values in the result are null
            assertNull(loginResult.getAuthToken());
            assertNull(loginResult.getUsername());
            assertNull(loginResult.getPersonID());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}