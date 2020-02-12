package server.clear;

import api.model.AuthToken;
import api.model.Event;
import api.model.Person;
import api.model.User;
import api.result.MessageResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.dataaccess.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class ClearServiceTest {

    private DatabaseManager dbManager;
    private ClearService clearService;
    private MessageResult clearResult;
    EventDao eventDao;
    UserDao userDao;
    PersonDao personDao;
    AuthTokenDao authDao;

    @Before
    public void setUp() throws Exception {
        dbManager = new DatabaseManager();
        dbManager.openConnection();
        eventDao = new EventDao(dbManager.getConn());
        userDao = new UserDao(dbManager.getConn());
        personDao = new PersonDao(dbManager.getConn());
        authDao = new AuthTokenDao(dbManager.getConn());
        clearService = new ClearService();
        clearResult = new MessageResult();
    }

    @After
    public void tearDown() throws Exception {
        dbManager.closeConnection(dbManager.getCommit());
        eventDao = null;
        userDao = null;
        personDao = null;
        authDao = null;
    }

    @Test
    public void testClear() {
        //POSITIVE TEST
        try {
            //Populate the tables with the data
            Event event1 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "United States",
                    "Salt Lake City", "birth", "1996");
            eventDao.createEvent(event1);

            AuthToken token1 = new AuthToken(UUID.randomUUID().toString(), "etodd");
            authDao.createAuthToken(token1);

            Person person1 = new Person(
                    UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            personDao.createPerson(person1);

            User user1 = new User("etodd", "password1", "eric.w.todd@gmail.com",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            userDao.createUser(user1);

            //Show the tables are not empty by asserting not null
            assertNotNull(eventDao.readEvent(event1.getEventID()));
            assertNotNull(personDao.readPerson(person1.getPersonID()));
            assertNotNull(authDao.readAuthToken(token1.getUsername()));
            assertNotNull(userDao.readUser(user1.getUsername()));
            //Close the connection so the ClearService can interact with the database
            dbManager.closeConnection(true);

            //Run the Clear Service
            clearResult = clearService.clear();

            //Reopen the Connection
            dbManager.openConnection();
            eventDao = new EventDao(dbManager.getConn());
            userDao = new UserDao(dbManager.getConn());
            personDao = new PersonDao(dbManager.getConn());
            authDao = new AuthTokenDao(dbManager.getConn());
            //Show each table was cleared by asserting null for each of them.
            assertNull(eventDao.readEvent(event1.getEventID()));
            assertNull(personDao.readPerson(person1.getPersonID()));
            assertNull(authDao.readAuthToken(token1.getAuthToken()));
            assertNull(userDao.readUser(user1.getUsername()));
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testNegativeClear() {
        //NEGATIVE TEST
        try {
            Event event1 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "United States",
                    "Salt Lake City", "birth", "1996");
            eventDao.createEvent(event1);
            clearResult = clearService.clear();
            dbManager.setCommit(true);
            //The result shows the error when a clear happens in the wrong situation
            //In this case, the connection to the SQLite database was still open when clear was called,
            //And SQLite cannot have more than one connection to itself at the same time, so this is the error that is
            //Communicated to the User
            assertEquals(clearResult.getMessage(), "[SQLITE_BUSY]  The database file is locked (database is locked)");
            dbManager.setCommit(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
    }
}