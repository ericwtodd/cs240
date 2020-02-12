package server.user;

import api.model.User;
import api.request.UserRegisterRequest;
import api.result.UserResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.clear.ClearService;
import server.dataaccess.DatabaseManager;
import server.dataaccess.EventDao;
import server.dataaccess.PersonDao;
import server.dataaccess.UserDao;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserRegisterServiceTest {

    private DatabaseManager dbManager;
    private UserDao userDao;
    private UserResult registerResult;
    private UserRegisterService registerService;
    private UserRegisterRequest registerRequest;

    @Before
    public void setUp() throws Exception {
        registerRequest = new UserRegisterRequest();
        registerService = new UserRegisterService();
        dbManager = new DatabaseManager();
    }

    @After
    public void tearDown() throws Exception {
        //Clear the Database after each Test
        ClearService clearService = new ClearService();
        clearService.clear();
    }

    @Test
    public void testRegister() {
        //POSITIVE TEST
        try {
            //Get Register Information
            String userName = "etodd";
            String password = "password15";
            String email = "eric.todd@gmail.com";
            String firstName = "Eric";
            String lastName = "Todd";
            String gender = "m";
            registerRequest.setUsername(userName);
            registerRequest.setPassword(password);
            registerRequest.setEmail(email);
            registerRequest.setFirstName(firstName);
            registerRequest.setLastName(lastName);
            registerRequest.setGender(gender);

            //Run the registerService, and output it to a registerResult
            registerResult = registerService.register(registerRequest);

            //Assert that the register was successful, and the personID & an Authtoken are returned.
            assertEquals(registerResult.getUsername(), userName);
            assertNotNull(registerResult.getAuthToken());
            assertNotNull(registerResult.getAuthToken());

            //Assert null to the error message
            assertNull(registerResult.getMessage());

            //Assert persons and events are not null for this user to indicate they were populated.
            dbManager.openConnection();
            PersonDao personDao = new PersonDao(dbManager.getConn());
            EventDao eventDao = new EventDao(dbManager.getConn());

            assertNotNull(personDao.getUserFamily(userName));
            assertNotNull(eventDao.getUserFamilyEvents(userName));

            dbManager.closeConnection(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }

        //NEGATIVE TESTS
        //Try to register with invalid information
        try {
            //Get Register Information
            String userName = "etodd";
            String password = "password15";
            String email = "";
            String firstName = null;
            String lastName = "Todd";
            String gender = "m";
            registerRequest.setUsername(userName);
            registerRequest.setPassword(password);
            registerRequest.setEmail(email);
            registerRequest.setFirstName(firstName);
            registerRequest.setLastName(lastName);
            registerRequest.setGender(gender);

            //Run the registerService, and output it to a registerResult
            registerResult = registerService.register(registerRequest);
            //The correct error message is contained in the result
            assertEquals(registerResult.getMessage(), "Request has missing or invalid values.");
            //The other values in the result are null
            assertNull(registerResult.getAuthToken());
            assertNull(registerResult.getUsername());
            assertNull(registerResult.getPersonID());

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try registering the same username twice
        try {
            String userName = "etodd";
            String password = "newPassword";
            String email = "newEmail";
            String firstName = "Eric";
            String lastName = "Todd";
            String gender = "m";
            registerRequest.setUsername(userName);
            registerRequest.setPassword(password);
            registerRequest.setEmail(email);
            registerRequest.setFirstName(firstName);
            registerRequest.setLastName(lastName);
            registerRequest.setGender(gender);

            //Run the registerService, and output it to a registerResult
            registerResult = registerService.register(registerRequest);
            //The correct error message is contained in the result
            assertEquals(registerResult.getMessage(), "Username already taken by another user");
            //The other values in the result are null
            assertNull(registerResult.getAuthToken());
            assertNull(registerResult.getUsername());
            assertNull(registerResult.getPersonID());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}