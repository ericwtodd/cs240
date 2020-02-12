package server.event;

import api.model.AuthToken;
import api.model.Event;
import api.result.EventResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteErrorCode;
import server.dataaccess.DatabaseManager;
import server.dataaccess.EventDao;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.*;

public class EventServiceTest {
    EventService eventService;
    DatabaseManager dbManager;
    EventResult eventResult;
    EventDao eventDao;

    @Before
    public void setUp() throws Exception {
        dbManager = new DatabaseManager();
        dbManager.openConnection();
        eventService = new EventService();
        eventResult = new EventResult();
        eventDao = new EventDao(dbManager.getConn());
    }

    @After
    public void tearDown() throws Exception {
        dbManager.openConnection();
        eventDao = new EventDao(dbManager.getConn());
        eventDao.clearTable();
        dbManager.closeConnection(true);
    }

    @Test
    public void getEvents() {
        //POSITIVE TESTS
        try {
            //Get an authToken from a user
            AuthToken eventToken = new AuthToken(UUID.randomUUID().toString(), "etodd");
            //Make sure that this user has some events already in the database:
            Event event1 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "United States",
                    "Salt Lake City", "birth", "1996");
            Event event2 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "New Zealand",
                    "Auckland", "death", "2080");
            Event event3 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "United States",
                    "Oakland", "marriage", "2019");
            eventDao.createEvent(event1);
            eventDao.createEvent(event2);
            eventDao.createEvent(event3);
            //Close the database connection so the service can connect
            dbManager.closeConnection(true);
            //Call the Event Service
            eventResult = eventService.getEvents(eventToken);

            assertNull(eventResult.getMessage());
            assertNull(eventResult.getEvent());
            assertNotNull(eventResult.getEvents());
            for (Event event : eventResult.getEvents()) {
                //Should have the same descendant for all events in the returned eventResult
                assertEquals("etodd", event.getDescendant());
            }
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }

        //NEGATIVE TESTS
        //Try using an invalid AuthToken to request an event
        try {
            AuthToken eventToken = new AuthToken(UUID.randomUUID().toString(), "");
            eventResult = eventService.getEvents(eventToken);
            assertEquals(eventResult.getMessage(), "AuthToken is invalid");
            assertNull(eventResult.getEvent());
            assertNull(eventResult.getEvents());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try requesting events for a user that doesn't have any
        try {
            AuthToken eventToken = new AuthToken(UUID.randomUUID().toString(), "Jose");
            eventResult = eventService.getEvents(eventToken);
            assertEquals(eventResult.getMessage(), "No Associated Events Found for this User");
            assertNull(eventResult.getEvent());
            assertNull(eventResult.getEvents());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void getEvent() {
        //POSITIVE TEST
        try {
            //Create an AuthToken like if a User were logging in
            AuthToken eventToken = new AuthToken(UUID.randomUUID().toString(), "etodd");
            Event event1 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "United States",
                    "Salt Lake City", "birth", "1996");
            eventDao.createEvent(event1);
            dbManager.closeConnection(true);

            //Run the EventService and get a result
            eventResult = eventService.getEvent(eventToken, event1.getEventID());

            //Assert that the returned event is the one added earlier, and the other members
            //Of the eventResult are null so that the single event is returned.
            dbManager.openConnection();
            eventDao = new EventDao(dbManager.getConn());
            assertEquals(eventResult.getEvent(), event1);
            assertNull(eventResult.getEvents());
            assertNull(eventResult.getMessage());

            dbManager.closeConnection(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.closeConnection(false);
        }

        //NEGATIVE TESTS
        //Try using an invalid AuthToken to request an event
        try {
            AuthToken eventToken = new AuthToken(UUID.randomUUID().toString(), "");

            eventResult = eventService.getEvent(eventToken, UUID.randomUUID().toString());
            assertEquals(eventResult.getMessage(), "AuthToken is invalid");
            assertNull(eventResult.getEvent());
            assertNull(eventResult.getEvents());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try using an invalid eventID to request an event
        try {
            AuthToken eventToken = new AuthToken(UUID.randomUUID().toString(), "etodd");

            eventResult = eventService.getEvent(eventToken, "");
            assertEquals(eventResult.getMessage(), "Requested eventID is invalid");
            assertNull(eventResult.getEvent());
            assertNull(eventResult.getEvents());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try requesting an event that doesn't belong to the current
        try {
            dbManager.openConnection();
            eventDao = new EventDao(dbManager.getConn());
            //Create an AuthToken like if a User were logging in
            AuthToken eventToken = new AuthToken(UUID.randomUUID().toString(), "JOSE");
            Event event1 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "United States",
                    "Salt Lake City", "birth", "1996");
            eventDao.createEvent(event1);
            dbManager.closeConnection(true);

            //Run the EventService and get a result
            eventResult = eventService.getEvent(eventToken, event1.getEventID());

            //The correct error message appears:
            assertEquals(eventResult.getMessage(), "Requested event does not belong to this User");
            assertNull(eventResult.getEvents());
            assertNull(eventResult.getEvent());
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        //Try requesting an event that doesn't exist
        try {
            AuthToken eventToken = new AuthToken(UUID.randomUUID().toString(), "etodd");
            eventResult = eventService.getEvent(eventToken, UUID.randomUUID().toString());
            //The correct error message appears:
            assertEquals(eventResult.getMessage(), "Requested Event Not Found");
            assertNull(eventResult.getEvents());
            assertNull(eventResult.getEvent());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}