package server.dataaccess;

import api.model.Event;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class EventDaoTest {

    private EventDao eventDao;
    private DatabaseManager dbManager;

    @Before
    public void setUp() throws Exception {
        dbManager = new DatabaseManager();
        dbManager.openConnection();
        eventDao = new EventDao(dbManager.getConn());
        eventDao.clearTable();

    }

    @After
    public void tearDown() throws Exception {
        eventDao.clearTable();
        dbManager.closeConnection(dbManager.getCommit());
        eventDao = null;
    }

    @Test
    public void testReadAndCreateEvent() {
        //POSITIVE TESTS
        try {
            //Check an Event is being added and read correctly:
            Event event1 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "United States",
                    "Salt Lake City", "birth", "1996");
            eventDao.createEvent(event1);

            //Check all of the member variables of the event to make sure it read in correctly
            assertEquals(event1.getEventID(), eventDao.readEvent(event1.getEventID()).getEventID());
            assertEquals(event1.getDescendant(), eventDao.readEvent(event1.getEventID()).getDescendant());
            assertEquals(event1.getPersonID(), eventDao.readEvent(event1.getEventID()).getPersonID());
            assertEquals(event1.getLatitude(), eventDao.readEvent(event1.getEventID()).getLatitude());
            assertEquals(event1.getLongitude(), eventDao.readEvent(event1.getEventID()).getLongitude());
            assertEquals(event1.getCountry(), eventDao.readEvent(event1.getEventID()).getCountry());
            assertEquals(event1.getCity(), eventDao.readEvent(event1.getEventID()).getCity());
            assertEquals(event1.getEventType(), eventDao.readEvent(event1.getEventID()).getEventType());
            assertEquals(event1.getYear(), eventDao.readEvent(event1.getEventID()).getYear());

            //Check null is returned when an eventID isn't found
            String randomEvent = UUID.randomUUID().toString();
            assertNull(eventDao.readEvent(randomEvent));
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS

        //Try adding a null event
        try {
            Event event2 = null;
            eventDao.createEvent(event2);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Cannot add a null Event");
            dbManager.setCommit(false);
        }
        //Try adding an event with invalid information
        try {
            Event event = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", null, "United States",
                    "Salt Lake City", "birth", "");
            eventDao.createEvent(event);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Event with invalid information");
            dbManager.setCommit(false);
        }
        //Try reading a null event
        try {
            eventDao.readEvent(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested eventID is null");
            dbManager.setCommit(false);
        }
        //Check what happens if I try to add an event with the same eventID as another event:
        try {
            Event event3 = new Event(
                    UUID.randomUUID().toString(), "jose", UUID.randomUUID().toString(),
                    "-80.10", "-110.1167", "Mexico",
                    "Mexico City", "birth", "1996");
            Event event4 = new Event(
                    event3.getEventID().toString(), "etodd", UUID.randomUUID().toString(),
                    "50.10", "-110.1167", "USA",
                    "Salt Lake City", "birth", "1996");
            eventDao.createEvent(event3);
            eventDao.createEvent(event4);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "An event with this EventID already exists");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeleteEvent() {
        //POSITIVE TESTS
        try {
            //Add and delete an event and assert it's no longer there.
            Event event1 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "United States",
                    "Salt Lake City", "birth", "1996");
            eventDao.createEvent(event1);
            //Show the event was added by asserting not null
            assertNotNull(eventDao.readEvent(event1.getEventID()));
            //Delete the event
            eventDao.deleteEvent(event1.getEventID());
            //Show the event was deleted by asserting null
            assertNull(eventDao.readEvent(event1.getEventID()));
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS
        //Deleting an Event that doesn't exist - Should do nothing
        try {
            eventDao.deleteEvent(UUID.randomUUID().toString());
            dbManager.setCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
        //Delete a null Event
        try {
            eventDao.deleteEvent(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested eventID is null");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeleteAndCreateTable() {
        //Delete the Table
        try {
            eventDao.deleteTable();
            //Try to Read something from it, and get a SQL or missing database error
            eventDao.readEvent(UUID.randomUUID().toString());
        } catch (SQLException ex) {
            assertEquals(ex.getMessage(), ("[SQLITE_ERROR] SQL error or missing database (no such table: Events)"));
        }
        //Create the Table
        try {
            eventDao.createTable();
            //Now read event should return null for a username not in the database,
            //but showing the table exists
            assertNull(eventDao.readEvent(UUID.randomUUID().toString()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testClearTable() {
        try {
            //Add a few events to the database
            Event event3 = new Event(
                    UUID.randomUUID().toString(), "jose", UUID.randomUUID().toString(),
                    "5000.10", "-110.1167", "Mexico",
                    "Mexico City", "birth", "1996");
            Event event4 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "5000.10", "-110.1167", "USA",
                    "Salt Lake City", "birth", "1996");

            eventDao.createEvent(event3);
            eventDao.createEvent(event4);
            //Show they were added, asserting not null
            assertNotNull(eventDao.readEvent(event3.getEventID()));
            assertNotNull(eventDao.readEvent(event4.getEventID()));
            //Clear the table
            eventDao.clearTable();
            //Assert read is null
            assertNull(eventDao.readEvent(event3.getEventID()));
            assertNull(eventDao.readEvent(event4.getEventID()));
            dbManager.setCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testDeleteFamilyEvents() {
        //POSITIVE TEST
        try {
            //Add multiple events for the same family
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

            //Show they were added, asserting not null
            assertNotNull(eventDao.readEvent(event1.getEventID()));
            assertNotNull(eventDao.readEvent(event2.getEventID()));
            assertNotNull(eventDao.readEvent(event3.getEventID()));
            //Delete all events associated with this user
            eventDao.deleteFamilyEvents("etodd");

            //Assert they were deleted by getting null returned
            assertNull(eventDao.readEvent(event1.getEventID()));
            assertNull(eventDao.readEvent(event2.getEventID()));
            assertNull(eventDao.readEvent(event3.getEventID()));

            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS

        //Try deleting events for a username that doesn't exist in the database - should do nothing
        try {
            eventDao.deleteFamilyEvents("DNE");
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
        //Try deleting events for a null username
        try {
            eventDao.deleteFamilyEvents(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested username is null");
            dbManager.setCommit(false);
        }
    }

    @Test
    public void testGetUserFamilyEvents() {
        //POSITIVE TEST
        //Add events for two families, and get the events of one family
        try {
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

            Event event4 = new Event(UUID.randomUUID().toString(), "Jose", UUID.randomUUID().toString(),
                    "50.10", "-110.1167", "Mexico",
                    "Mexico City", "birth", "1996");

            eventDao.createEvent(event1);
            eventDao.createEvent(event2);
            eventDao.createEvent(event3);
            eventDao.createEvent(event4);

            ArrayList<Event> events = eventDao.getUserFamilyEvents("etodd");

            for (Event event : events) {
                assertEquals(event.getDescendant(), "etodd");
                assertNotEquals(event.getDescendant(), "Jose");
            }
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }

        //NEGATIVE TESTS

        //Try getting the events for a username that doesn't exist in the database - should return null
        try {
            assertNull(eventDao.getUserFamilyEvents("DNE"));
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
            dbManager.setCommit(false);
        }
        //Try getting the events for a null username
        try {
            eventDao.getUserFamilyEvents(null);
            dbManager.setCommit(true);
        } catch (SQLException | IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Requested username is null");
            dbManager.setCommit(false);
        }
    }
}