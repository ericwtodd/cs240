package edu.byu.erictodd.familymapclient.model;

import android.support.annotation.NonNull;

/**
 * Simple Event data object for Family Map
 */
public class Event implements Comparable<Event> {

    /**
     * Default Constructor
     * Used to create a new Event object
     */
    public Event() {
    }

    /**
     * Initializes an Event with the following parameters
     *
     * @param eventID    Unique identifier for this event (non-empty string)
     * @param descendant User(username) to which this person belongs
     * @param personID   ID of person to which this event belongs
     * @param latitude   latitude of event's location
     * @param longitude  longitude of event's location
     * @param country    country in which event occurred
     * @param city       city in which event occurred
     * @param eventType  Type of Event (birth, baptism, christening, marriage, death, etc.)
     * @param year       year in which event occurred
     */
    public Event(String eventID, String descendant, String personID, String latitude,
                 String longitude, String country, String city, String eventType, String year) {
        this.eventID = eventID;
        this.descendant = descendant;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    /**
     * Unique identifier for this event (non-empty string) (UUID as a string)
     */
    private String eventID;
    /**
     * User(username) to which this event belongs
     */
    private String descendant;
    /**
     * ID of person to which this event belongs (UUID as a String)
     */
    private String personID;
    /**
     * Latitude of event's location
     */
    private String latitude;
    /**
     * Longitude of event's location
     */
    private String longitude;
    /**
     * Country in which event occurred
     */
    private String country;
    /**
     * City in which event occurred
     */
    private String city;
    /**
     * Type of Event (birth, baptism, christening, marriage, death, etc.)
     */
    private String eventType;
    /**
     * Year in which event occurred (integer formatted as a string)
     */
    private String year;

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getDescendant() {
        return descendant;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Determines whether any of the members of an Event are invalid/don't meet the API specifications
     *
     * @return false if the Event has all valid member variables,
     * true if the Event has any invalid member variable
     */
    public boolean isInvalidEvent() {
        if (eventID == null || eventID.isEmpty()) {
            return true;
        }
        if (descendant == null || descendant.isEmpty()) {
            return true;
        }
        if (personID == null || personID.isEmpty()) {
            return true;
        }
        if (latitude == null || latitude.isEmpty()) {
            return true;
        }
        if (longitude == null || longitude.isEmpty()) {
            return true;
        }
        if (country == null || country.isEmpty()) {
            return true;
        }
        if (city == null || city.isEmpty()) {
            return true;
        }
        if (eventType == null || eventType.isEmpty()) {
            return true;
        }
        if (year == null || year.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Determines how an object is equal to an event
     *
     * @param o Object being compared to this event
     * @return returns true if the object is equal, returns false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }

        Event event = (Event) o;

        if (event.getEventID().equals(this.getEventID())
                && event.getDescendant().equals(this.getDescendant())
                && event.getPersonID().equals(this.getPersonID())
                && event.getLatitude().equals(this.getLatitude())
                && event.getLongitude().equals(this.getLongitude())
                && event.getCountry().equals(this.getCountry())
                && event.getCity().equals(this.getCity())
                && event.getEventType().equals(this.getEventType())
                && event.getYear().equals(this.getYear())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Compares two events to determine which comes before the other
     *
     * @param event the event being compared
     * @return returns -1 if this event < that event
     * returns 0 if this event is == that event
     * returns 1 if this > event
     */
    @Override
    public int compareTo(@NonNull Event event) {
        if (this.isInvalidEvent() || event.isInvalidEvent())
        {
            throw new IllegalArgumentException("Event is invalid");
        }
        //If they are equal
        if (this == event) {
            return 0;
        }
        //If this eventType is birth & the other eventType is not
        if (this.getEventType().toLowerCase().equals("birth") && !event.getEventType().toLowerCase().equals("birth")) {
            return -1;
        }
        if (!this.getEventType().toLowerCase().equals("birth") && event.getEventType().toLowerCase().equals("birth")) {
            return 1;
        }
        //If this year comes before the other event year
        if (Integer.valueOf(this.getYear()) < Integer.valueOf(event.getYear())) {
            return -1;
        }
        //If this eventType is death and the other eventType is not
        if (this.getEventType().toLowerCase().equals("death") && !event.getEventType().toLowerCase().equals("death")) {
            return 1;
        }
        if (!this.getEventType().toLowerCase().equals("death") && event.getEventType().toLowerCase().equals("death")) {
            return -1;
        }
        //If this event year is after the other event year
        if (Integer.valueOf(this.getYear()) > Integer.valueOf(event.getYear())) {
            return 1;
        }
        //If the years are the Same
        if (this.getYear().equals(event.getYear())) {
            //This event's eventType string comes before the other event's eventType string
            if (this.getEventType().compareTo(event.getEventType()) < 0) {
                return -1;
            }
            //This event's eventType string comes after the other event's eventType string
            else if (this.getEventType().compareTo(event.getEventType()) > 0) {
                return 1;
            }
            //This event's eventType is the same as the other event's eventType string
            else {
                return 0;
            }
        }
        return 0;
    }
}
