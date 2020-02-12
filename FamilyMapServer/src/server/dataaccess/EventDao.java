package server.dataaccess;

import api.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A data access object that interacts with the Event table in the database
 */
public class EventDao {

    /**
     * Keeps track of how many events were added in a transaction by this Dao.
     */
    private int eventsAdded;
    /**
     * A connection to the database for the current transaction
     */
    private Connection connection;

    /**
     * Initializes a new EventDao object with a connection created within a service needing this Dao
     *
     * @param conn The connection opened by the service using this Dao.
     */
    public EventDao(Connection conn) {
        eventsAdded = 0;
        connection = conn;
    }

    public int getEventsAdded() {
        return eventsAdded;
    }

    /**
     * Inserts a new event into the database
     *
     * @param event the Event to insert
     * @throws SQLException             if the event can't be added into the database
     * @throws IllegalArgumentException if the event is invalid/null
     */
    public void createEvent(Event event) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        if (event == null) {
            //ERROR!
            throw new IllegalArgumentException("Cannot add a null Event");
        } else if (event.isInvalidEvent()) {
            throw new IllegalArgumentException("Event with invalid information");
        }
        if (readEvent(event.getEventID()) != null) {
            throw new IllegalArgumentException("An event with this EventID already exists");
        }
        try {
            String sql = "INSERT INTO Events " +
                    "(eventID, descendant, personID, latitude, longitude, country, city, eventType, year) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            statement = connection.prepareStatement(sql);

            statement.setString(1, event.getEventID());
            statement.setString(2, event.getDescendant());
            statement.setString(3, event.getPersonID());
            statement.setString(4, event.getLatitude());
            statement.setString(5, event.getLongitude());
            statement.setString(6, event.getCountry());
            statement.setString(7, event.getCity());
            statement.setString(8, event.getEventType());
            statement.setString(9, event.getYear());

            statement.executeUpdate();
            eventsAdded += 1;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Returns an event from the database
     *
     * @param eventID the Event's unique ID
     * @return returns desired event Event
     * @throws SQLException             if the table doesn't exist
     * @throws IllegalArgumentException if the eventID is invalid/null
     */
    public Event readEvent(String eventID) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if (eventID == null) {
            throw new IllegalArgumentException("Requested eventID is null");
        }
        Event event;
        try {
            String sql = "select * from Events where eventID =  ?;";
            statement = connection.prepareStatement(sql);
            statement.setString(1, eventID);
            resultSet = statement.executeQuery();
            //The Event ID should be unique, so this should only happen once.
            if (resultSet.next()) {
                event = new Event(
                        resultSet.getString("eventID"),
                        resultSet.getString("descendant"),
                        resultSet.getString("personID"),
                        resultSet.getString("latitude"),
                        resultSet.getString("longitude"),
                        resultSet.getString("country"),
                        resultSet.getString("city"),
                        resultSet.getString("eventType"),
                        resultSet.getString("year"));
            } else {
                event = null;
            }
            statement.execute();
            return event;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    /**
     * @param username the current user that is the descendant of events in the database
     * @return All the events for all family members of the current user
     * @throws SQLException             there are no events associated with the current user
     * @throws IllegalArgumentException username passed in is invalid
     */
    public ArrayList<Event> getUserFamilyEvents(String username) throws SQLException, IllegalArgumentException {
        ArrayList<Event> familyEvents = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if (username == null) {
            throw new IllegalArgumentException("Requested username is null");
        }
        try {
            String sql = "select * from Events where descendant =  ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            //Get all the events associated with all family members of the descendant (username)
            while (resultSet.next()) {
                Event event = new Event(
                        resultSet.getString("eventID"),
                        resultSet.getString("descendant"),
                        resultSet.getString("personID"),
                        resultSet.getString("latitude"),
                        resultSet.getString("longitude"),
                        resultSet.getString("country"),
                        resultSet.getString("city"),
                        resultSet.getString("eventType"),
                        resultSet.getString("year"));
                familyEvents.add(event);
            }
            if (familyEvents.isEmpty()) {
                familyEvents = null;
            }
            statement.execute();
            return familyEvents;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    /**
     * Removes an event from the database
     *
     * @param eventID the Event's unique ID
     * @throws SQLException             if the table doesn't exist
     * @throws IllegalArgumentException if the eventID is invalid/null
     */
    public void deleteEvent(String eventID) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        if (eventID == null) {
            throw new IllegalArgumentException("Requested eventID is null");
        }
        try {
            String sql = "DELETE FROM Events WHERE eventID = ?;";
            statement = connection.prepareStatement(sql);
            statement.setString(1, eventID);

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Removes all events from the database associated with the current user (username)
     *
     * @param username the username that is the descendant of the events associated with his/her family
     * @throws SQLException             there are no events associated with the current user
     * @throws IllegalArgumentException the username passed is null
     */
    public void deleteFamilyEvents(String username) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        if (username == null) {
            throw new IllegalArgumentException("Requested username is null");
        }
        try {
            String sql = "DELETE FROM Events WHERE descendant = ?;";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Creates the Event table in the database
     *
     * @throws SQLException if the table can't be created
     */
    public void createTable() throws SQLException {
        Statement statement = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Events (" +
                    "  eventID    TEXT NOT NULL," +
                    "  descendant TEXT NOT NULL," +
                    "  personID   TEXT NOT NULL," +
                    "  latitude   REAL NOT NULL," +
                    "  longitude  REAL NOT NULL," +
                    "  country    TEXT NOT NULL," +
                    "  city       TEXT NOT NULL," +
                    "  eventType  TEXT NOT NULL," +
                    "  year       TEXT NOT NULL," +
                    "  PRIMARY KEY (eventID)," +
                    "  FOREIGN KEY (personID) REFERENCES Persons (personID)," +
                    "  FOREIGN KEY (descendant) REFERENCES Users (username)" +
                    ");";
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Deletes the Event table from the database
     *
     * @throws SQLException if the table can't be deleted
     */
    public void deleteTable() throws SQLException {
        Statement statement = null;
        try {
            // drop table if exists
            String sql = "DROP TABLE IF EXISTS Events;";
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Clears the Events table, but keeps it in the database
     *
     * @throws SQLException if the table can't be cleared
     */
    public void clearTable() throws SQLException {
        Statement statement = null;
        try {
            //clear table if exists
            String sql = "DELETE FROM Events;";
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                statement.close();

            }
        }
    }

//    /**
//     * Updates an event in the database
//     *
//     * @param eventID the Event's unique ID
//     */
//    public void updateEvent(UUID eventID) {
//
//    }
}
