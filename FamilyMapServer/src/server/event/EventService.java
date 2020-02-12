package server.event;

import api.model.AuthToken;
import api.model.Event;
import api.result.EventResult;
import server.dataaccess.DatabaseManager;
import server.dataaccess.EventDao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A service object that either returns a single event given a specified eventID
 * or all events for the family of the current user
 */
public class EventService {

    /**
     * Default Constructor
     * Used to create a new EventService object
     */
    public EventService() {
    }

    /**
     * Returns all events for all family members of the current user.
     * The current user is determined from the provided auth token.
     *
     * @param authToken the auth token provided by the current user.
     * @return EventResult contains list of all events for all family members of the current user.
     */
    public EventResult getEvents(AuthToken authToken) {
        EventResult eventResult = new EventResult();
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.openConnection();
        try {
            if (authToken == null || authToken.isInvalidToken()) {
                throw new IllegalArgumentException("AuthToken is invalid");
            }
            Connection connection = dbManager.getConn();
            EventDao eventDao = new EventDao(connection);

            if (eventDao.getUserFamilyEvents(authToken.getUsername()) != null) {
                //Set the Array of Events in the Event Result
                eventResult.setEvents(eventDao.getUserFamilyEvents(authToken.getUsername()));
                eventResult.setEvent(null);
                eventResult.setMessage(null);
                dbManager.closeConnection(true);
            } else {
                throw new IllegalArgumentException("No Associated Events Found for this User");
            }
        } catch (SQLException | IllegalArgumentException ex) {
            eventResult.setEvents(null);
            eventResult.setEvent(null);
            eventResult.setMessage(ex.getMessage());
            dbManager.closeConnection(false);
        }
        return eventResult;
    }

    /**
     * Returns the single Event object with the specified ID
     *
     * @param eventID    the Event of interest
     * @param eventToken the AuthToken of the user requesting this event
     * @return EventResult contains information about a single event matching the specified ID
     */
    public EventResult getEvent(AuthToken eventToken, String eventID) {

        EventResult eventResult = new EventResult();
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.openConnection();

        try {
            if (eventToken.isInvalidToken()) {
                throw new IllegalArgumentException("AuthToken is invalid");
            }
            if (eventID == null || eventID.isEmpty()) {
                throw new IllegalArgumentException("Requested eventID is invalid");
            }

            Connection connection = dbManager.getConn();
            EventDao eventDao = new EventDao(connection);

            if (eventDao.readEvent(eventID) != null) {
                Event event = eventDao.readEvent(eventID);
                if (!event.getDescendant().equals(eventToken.getUsername())) {
                    throw new IllegalArgumentException("Requested event does not belong to this User");
                }
                eventResult.setEvent(eventDao.readEvent(eventID));
                eventResult.setEvents(null);
                eventResult.setMessage(null);
                dbManager.closeConnection(true);
            } else {
                throw new IllegalArgumentException("Requested Event Not Found");
            }
        } catch (SQLException | IllegalArgumentException ex) {
            eventResult.setEvents(null);
            eventResult.setEvent(null);
            eventResult.setMessage(ex.getMessage());
            dbManager.closeConnection(false);
        }
        return eventResult;
    }
}
