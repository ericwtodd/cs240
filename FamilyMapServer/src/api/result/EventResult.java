package api.result;

import api.model.Event;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Simple data object used by the following API's:
 * /event/[eventID]
 * /event
 */
public class EventResult {

    /**
     * Default Constructor
     * Used to create a new EventResult object
     */
    public EventResult() {
    }

    //API: event/[eventID] - Success
    /**
     * Simple data object being returned
     * If the service fails, this will be set to null
     */
    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    //API: event - Success
    /**
     * An array of Event objects
     * If the Service fails, this will be set to null
     */
    private ArrayList<Event> data;

    public ArrayList<Event> getEvents() {
        return data;
    }

    public void setEvents(ArrayList<Event> events) {
        this.data = events;
    }

    //API: event/[eventID] - Failure
    //API: event - Failure
    /**
     * A string either containing a description of the error if failure
     * Otherwise, this string will be set to null;
     */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
