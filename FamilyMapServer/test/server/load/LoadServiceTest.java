package server.load;

import api.model.Event;
import api.model.Person;
import api.model.User;
import api.request.LoadRequest;
import api.result.MessageResult;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.clear.ClearService;
import server.dataaccess.DatabaseManager;
import server.dataaccess.EventDao;
import server.dataaccess.PersonDao;
import server.dataaccess.UserDao;

import javax.xml.crypto.Data;

import java.io.*;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class LoadServiceTest {

    private DatabaseManager dbManager;
    private EventDao eventDao;
    private PersonDao personDao;
    private UserDao userDao;
    private Gson gson;
    private LoadRequest loadRequest;
    private LoadService loadService;
    private MessageResult loadResult;


    @Before
    public void setUp() throws Exception {
        dbManager = new DatabaseManager();
        gson = new Gson();
        loadService = new LoadService();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoad() {
        //POSITIVE TEST
        try {
            File loadFile = new File("C:\\Users\\Eric Todd\\IdeaProjects\\FamilyMapServer\\json\\example.json");
            Reader loadReader = new InputStreamReader(new FileInputStream(loadFile));
            loadRequest = gson.fromJson(loadReader, LoadRequest.class);
            //Call the Load Service
            loadResult = loadService.load(loadRequest);

            //Assert the Load added the 1 user, 3 persons, and 2 events found in the example file
            assertTrue(loadResult.getMessage().contains("Successfully added 1 users, 3 persons, and 2 events to the database."));

            //Assert that the user, persons, and events are in the database
            dbManager.openConnection();
            personDao = new PersonDao(dbManager.getConn());
            userDao = new UserDao(dbManager.getConn());
            eventDao = new EventDao(dbManager.getConn());

            assertNotNull(userDao.readUser("sheila"));
            assertNotNull(personDao.getUserFamily("sheila"));
            assertEquals(personDao.getUserFamily("sheila").size(), 3);
            assertNotNull(eventDao.getUserFamilyEvents("sheila"));
            assertEquals(eventDao.getUserFamilyEvents("sheila").size(), 2);

            dbManager.closeConnection(true);

        } catch (SQLException | IOException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }

        //NEGATIVE TEST
        //Try a load request with invalid values
        try {
            //Make some users, events, and persons with invalid values.
            User user = new User("etodd", "password15", "",
                    "Eric", "Todd", "m", UUID.randomUUID().toString());
            Event event = new Event(
                    UUID.randomUUID().toString(), "jose", UUID.randomUUID().toString(),
                    "5000.10", "-110.1167", "Mexico",
                    "Mexico City", "birth", "1996");
            Event event2 = new Event(
                    UUID.randomUUID().toString(), "etodd", null,
                    "5000.10", "-110.1167", "USA",
                    "Salt Lake City", "birth", "1996");
            Person person = new Person(UUID.randomUUID().toString(), "etodd", "Eric", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            Person person2 = new Person(UUID.randomUUID().toString(), null, "David", "Todd",
                    "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(), person.getMother());
            Person person3 = new Person(UUID.randomUUID().toString(), "etodd", "Cindy", "Todd",
                    "f", UUID.randomUUID().toString(), UUID.randomUUID().toString(), person.getFather());
            //Add these users, persons, and events to the database.
            loadRequest.getUsers().add(user);
            loadRequest.getPersons().add(person);
            loadRequest.getPersons().add(person2);
            loadRequest.getPersons().add(person3);
            loadRequest.getEvents().add(event);
            loadRequest.getEvents().add(event2);

            //Call the Load Service
            loadResult = loadService.load(loadRequest);
            assertEquals(loadResult.getMessage(), "Invalid request data (missing/invalid values)");
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}