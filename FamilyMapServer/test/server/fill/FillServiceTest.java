package server.fill;

import api.model.User;
import api.request.FillRequest;
import api.result.MessageResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.dataaccess.DatabaseManager;
import server.dataaccess.EventDao;
import server.dataaccess.PersonDao;
import server.dataaccess.UserDao;

import javax.xml.crypto.Data;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class FillServiceTest {

    private FillRequest fillRequest;
    private FillRequest fillRequest2;
    private MessageResult fillResult;
    private MessageResult fillResult2;
    private int generations;
    private String username;
    private String username2;
    private FillService fillService;
    private DatabaseManager dbManager;
    private UserDao userDao;

    @Before
    public void setUp() throws Exception {
        dbManager = new DatabaseManager();
        dbManager.InitializeDatabase();
        dbManager.openConnection();
        userDao = new UserDao(dbManager.getConn());
        fillService = new FillService();
    }

    @After
    public void tearDown() throws Exception {
        dbManager.openConnection();
        userDao = new UserDao(dbManager.getConn());
        userDao.clearTable();
        dbManager.closeConnection(true);
    }

    @Test
    public void testFill() {
        //POSITIVE TESTS
        //Fill with just the username & default (4) generations
        try {
            username = "etodd";
            //Create the user that is logging in in the database.
            User user1 = new User(username, "password15", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            userDao.createUser(user1);
            //Commit changes so the user can be added
            dbManager.closeConnection(true);
            //Generate a fillRequest for a user
            fillRequest = new FillRequest(username);
            //Call the fill service
            fillResult = fillService.fill(fillRequest);

            //Assert that the user's persons and family events were populated.
            //Assert persons and events are not null for this user to indicate they were populated.
            dbManager.openConnection();
            PersonDao personDao = new PersonDao(dbManager.getConn());
            EventDao eventDao = new EventDao(dbManager.getConn());

            //Assert that the fillResult message details a successful addition of the right number of
            //persons and events
            String successMessage = "Successfully added " + personDao.getUserFamily(username).size()
                    + " persons and " + eventDao.getUserFamilyEvents(username).size() + " events to the database.";
            assertEquals(fillResult.getMessage(), successMessage);
            assertNotNull(personDao.getUserFamily(username));
            assertNotNull(eventDao.getUserFamilyEvents(username));

            dbManager.closeConnection(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Indicate that it works with generations specified (not the default) as well;
        try {
            username = "etodd";
            generations = 2;
            fillRequest = new FillRequest(username, generations);
            fillResult = fillService.fill(fillRequest);
            //Assert that the user's persons and family events were populated.
            //Assert persons and events are not null for this user to indicate they were populated.
            dbManager.openConnection();
            PersonDao personDao = new PersonDao(dbManager.getConn());
            EventDao eventDao = new EventDao(dbManager.getConn());

            //Assert that the fillResult message details a successful addition of the right number of
            //persons and events
            String successMessage = "Successfully added " + personDao.getUserFamily(username).size()
                    + " persons and " + eventDao.getUserFamilyEvents(username).size() + " events to the database.";
            assertEquals(fillResult.getMessage(), successMessage);
            assertNotNull(personDao.getUserFamily(username));
            assertNotNull(eventDao.getUserFamilyEvents(username));
            dbManager.closeConnection(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        //NEGATIVE TESTS
        //Try with an unregistered username
        try {
            username2 = "JOSE";
            generations = 2;
            fillRequest2 = new FillRequest(username2, generations);
            fillResult2 = fillService.fill(fillRequest2);
            assertEquals(fillResult2.getMessage(), "User Not Found");
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try with generations < 0
        try {
            username = "etodd";
            generations = -1;
            //Indicate that it works with generations specified as well;
            fillRequest = new FillRequest(username, generations);
            fillResult = fillService.fill(fillRequest);
            assertEquals(fillResult.getMessage(), "Request username or generations is invalid");
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}