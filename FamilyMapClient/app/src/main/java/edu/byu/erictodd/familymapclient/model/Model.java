package edu.byu.erictodd.familymapclient.model;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Contains locally stached information about the Currently Logged-In User
 * such as family person info, family event info, and various specific data structures
 * to easily access that information as required by the user
 */
public class Model {

    //Singleton Accessor Methods and the Default Constructor

    /**
     * The only instance of the Model class
     */
    private static Model singleInstance;

    /**
     * Default constructor initializes all its data structures
     */
    private Model() {
        user = new Person();
        persons = new HashMap<>();
        events = new HashMap<>();
        personEvents = new HashMap<>();
        eventTypes = new ArrayList<>();
        eventTypeColors = new HashMap<>();
        paternalAncestors = new HashSet<>();
        maternalAncestors = new HashSet<>();
        personRelatives = new HashMap<>();
        loggedIn = false;
        mSettings = new Settings();
        mFilter = new Filter();
        mFilteredEvents = new HashMap<>();
        mapMarkers = new HashMap<>();
        mLifeStoryLines = new HashSet<>();
        mFamilyTreeLines = new HashSet<>();
    }

    /**
     * Returns a pointer to the single instance of the Model class
     *
     * @return the single instance of the model class
     */
    public static Model getInstance() {
        return singleInstance;
    }

    /**
     * Resets the Model when a new User is logged in.
     */
    public void reset() {
        singleInstance = new Model();
    }


    //Instantiates the Model Singleton
    static {
        singleInstance = new Model();
    }


    //Member variables and Data Structures

    /**
     * The Person/User currently logged into the App
     */
    private Person user;
    /**
     * A list of all persons associated with the current User (in the current User's family)
     * Key: personID, Value: Associated Person
     */
    private Map<String, Person> persons;
    /**
     * A list of all events associated with the current User's family
     * Key: eventID, Value: Associated Event
     */
    private Map<String, Event> events;
    /**
     * A list of all the events associated with a specific person
     * Key: personID, Value: list of events associated with that person
     */
    private Map<String, List<Event>> personEvents;
    /**
     * A list of all the unique eventTypes found in the database
     */
    private List<String> eventTypes;
    /**
     * Contains the relationship between eventType and the map Marker Color
     * Key: EventType, Value: Color (stored as a Float)
     */
    private Map<String, Float> eventTypeColors;
    /**
     * A set containing all the personID's of the paternal ancestors of the current User
     */
    private Set<String> paternalAncestors;
    /**
     * A set containing all the personID's of the maternal ancestors of the current User
     */
    private Set<String> maternalAncestors;
    /**
     * A list of all the people immediately associated with a specific person
     * Key: personID, Value: list of people in the immediate family of that person
     */
    private Map<String, List<Person>> personRelatives;
    /**
     * A flag determining whether or not a user has logged in/is currently logged in
     */
    private boolean loggedIn;
    /**
     * Used to determine whether loading the person data into the model was a success.
     */
    private boolean loadPersonSuccess;
    /**
     * Used to determine whether loading the event data into the model was a success
     */
    private boolean loadEventSuccess;
    /**
     * Various settings that can be changed in the Settings Activity
     * including line colors, map type, and whether or not lines are enabled.
     */
    private Settings mSettings;
    /**
     * Various filters that can be changed in the Filter Activity
     * including by gender, by mother's side, by father's side, and event type
     */
    private Filter mFilter;
    /**
     * A Map containing all the filtered events that correspond to the current filter settings
     * (It is a subset of the events data member)
     * Key: eventID, Value: Event
     */
    private Map<String, Event> mFilteredEvents;
    /**
     * A set of the current life story lines drawn on the map
     */
    private Set<Polyline> mLifeStoryLines;
    /**
     * A set of the current Family Tree Lines drawn on the map
     */
    private Set<Polyline> mFamilyTreeLines;
    /**
     * The current Spouse Line drawn on the map
     */
    private Polyline mSpouseLine;
    /**
     * A map of all the markers that are placed on the google map fragment.
     * Key: eventID, Value: map Marker
     */
    private Map<String, Marker> mapMarkers;


    //Getters and Setters

    public Set<Polyline> getLifeStoryLines() {
        return mLifeStoryLines;
    }

    public void setLifeStoryLines(Set<Polyline> lifeStoryLines) {
        mLifeStoryLines = lifeStoryLines;
    }

    public Set<Polyline> getFamilyTreeLines() {
        return mFamilyTreeLines;
    }

    public void setFamilyTreeLines(Set<Polyline> familyTreeLines) {
        mFamilyTreeLines = familyTreeLines;
    }

    public Polyline getSpouseLine() {
        return mSpouseLine;
    }

    public void setSpouseLine(Polyline spouseLine) {
        mSpouseLine = spouseLine;
    }

    public Map<String, Marker> getMapMarkers() {
        return mapMarkers;
    }

    public void setMapMarkers(Map<String, Marker> mapMarkers) {
        this.mapMarkers = mapMarkers;
    }

    public boolean isLoadPersonSuccess() {
        return loadPersonSuccess;
    }

    public void setLoadPersonSuccess(boolean loadPersonSuccess) {
        this.loadPersonSuccess = loadPersonSuccess;
    }

    public boolean isLoadEventSuccess() {
        return loadEventSuccess;
    }

    public void setLoadEventSuccess(boolean loadEventSuccess) {
        this.loadEventSuccess = loadEventSuccess;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public Map<String, Person> getPersons() {
        return persons;
    }

    public void setPersons(Map<String, Person> persons) {
        this.persons = persons;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Event> events) {
        this.events = events;
    }

    public Map<String, List<Event>> getPersonEvents() {
        return personEvents;
    }

    public void setPersonEvents(Map<String, List<Event>> personEvents) {
        this.personEvents = personEvents;
    }

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public Map<String, Float> getEventTypeColors() {
        return eventTypeColors;
    }

    public void setEventTypeColors(Map<String, Float> eventTypeColors) {
        this.eventTypeColors = eventTypeColors;
    }

    public Set<String> getPaternalAncestors() {
        return paternalAncestors;
    }

    public void setPaternalAncestors(Set<String> paternalAncestors) {
        this.paternalAncestors = paternalAncestors;
    }

    public Set<String> getMaternalAncestors() {
        return maternalAncestors;
    }

    public void setMaternalAncestors(Set<String> maternalAncestors) {
        this.maternalAncestors = maternalAncestors;
    }

    public Map<String, List<Person>> getPersonRelatives() {
        return personRelatives;
    }

    public void setPersonRelatives(Map<String, List<Person>> personRelatives) {
        this.personRelatives = personRelatives;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Settings getSettings() {
        return mSettings;
    }

    public void setSettings(Settings settings) {
        mSettings = settings;
    }

    public Filter getFilter() {
        return mFilter;
    }

    public void setFilter(Filter filter) {
        mFilter = filter;
    }


    //Functions for retrieving, filtering, and sorting the data

    /**
     * Populates person-related Data Structures with data retrieved from the Database
     *
     * @param personResult the personResult containing all the person data associated with the current User
     */
    public void populatePersonData(PersonResult personResult) {
        //Add all the people to the local cache
        for (Person person : personResult.getPersons()) {
            getPersons().put(person.getPersonID(), person);
            //If the Person is the current User, we store that locally
            if (person.getPersonID().equals(getUser().getPersonID())) {
                setUser(person);
            }
            //Populate the personEvents map with personID's
            getPersonEvents().put(person.getPersonID(), new ArrayList<Event>());
        }
        addPaternalAncestors(findPerson(getUser().getFather()));
        addMaternalAncestors(findPerson(getUser().getMother()));
        //Populate list of immediate family members
        addImmediateFamilyMembers();
    }

    /**
     * Determines the current user's paternal ancestors recursively
     * and adds them into the Model's corresponding data structure
     *
     * @param person the person being evaluated
     */
    private void addPaternalAncestors(Person person) {
        //If the person has no ancestors, only add them and then stop
        if (person.getFather() == null && person.getMother() == null) {
            getPaternalAncestors().add(person.getPersonID());
        } else {
            getPaternalAncestors().add(person.getPersonID());
            if (person.getFather() != null) {
                addPaternalAncestors(findPerson(person.getFather()));
            }
            if (person.getMother() != null) {
                addPaternalAncestors(findPerson(person.getMother()));
            }
        }
    }

    /**
     * Determines the current user's maternal ancestors recursively
     * and adds them to the Model's corresponding data structure
     *
     * @param person the person being evaluated
     */
    private void addMaternalAncestors(Person person) {
        if (person.getFather() == null && person.getMother() == null) {
            getMaternalAncestors().add(person.getPersonID());
        } else {
            getMaternalAncestors().add(person.getPersonID());
            if (person.getFather() != null) {
                addMaternalAncestors(findPerson(person.getFather()));
            }
            if (person.getMother() != null) {
                addMaternalAncestors(findPerson(person.getMother()));
            }
        }
    }

    /**
     * Determines immediate family relationships for all people in the Model, and
     * adds it into the corresponding data structure.
     */
    private void addImmediateFamilyMembers() {
        for (Person person : getPersons().values()) {
            //Initialize Lists for newly discovered people
            if (getPersonRelatives().get(person.getPersonID()) == null) {
                getPersonRelatives().put(person.getPersonID(), new ArrayList<Person>());
            }
            if (getPersonRelatives().get(person.getFather()) == null) {
                getPersonRelatives().put(person.getFather(), new ArrayList<Person>());
            }
            if (getPersonRelatives().get(person.getMother()) == null) {
                getPersonRelatives().put(person.getMother(), new ArrayList<Person>());
            }

            //Get Person's Parents & spouse
            Person mother = findPerson(person.getMother());
            Person father = findPerson(person.getFather());
            Person spouse = findPerson(person.getSpouse());

            //If they have a father
            if (father != null) {
                //Add the father to the child's list
                getPersonRelatives().get(person.getPersonID()).add(father);
                //Add the child to the father's list
                getPersonRelatives().get(person.getFather()).add(person);
            }
            //If they have a mother
            if (mother != null) {
                //Add the mother to the child's list
                getPersonRelatives().get(person.getPersonID()).add(mother);
                //Add the child to the mother's list
                getPersonRelatives().get(person.getMother()).add(person);
            }
            //If they have a spouse
            if (spouse != null) {
                //Add the spouse to the person's list
                getPersonRelatives().get(person.getPersonID()).add(spouse);
            }
            //If the person doesn't have a valid personID
            if (person.getPersonID() == null) {
                //We shouldn't get here
                System.out.print("We have a null PersonID: " + person);
            }
        }
    }

    /**
     * Populates Event-related data structures with data retrieved from the server/database
     *
     * @param eventResult the eventResult containing all the event data associated with the family of the current user
     */
    public void populateEventData(EventResult eventResult) {
        for (Event event : eventResult.getEvents()) {
            //Populate Events (Map<EventID, Event>)
            getEvents().put(event.getEventID(), event);
            //Populate the EventTypes Set with all unique Event Types
            if (!getEventTypes().contains(event.getEventType().toLowerCase())) {
                getEventTypes().add(event.getEventType().toLowerCase());
            }
            //Populate personEvents (Map<personID, List<Event>>)
            if (getPersonEvents().get(event.getPersonID()) == null) {
                //If the Person doesn't exist, give them a new ArrayList for their events
                //this shouldn't happen much
                getPersonEvents().put(event.getPersonID(), new ArrayList<Event>());
            }
            //Populate the personEvents map
            getPersonEvents().get(event.getPersonID()).add(event);
        }
        //Generate unique colors for each event type
        populateEventTypeColors();
        //Generates filter settings for each unique event type
        populateDefaultFilters();
        mFilteredEvents = new HashMap<>(events);
    }

    /**
     * Generates unique colors for each event type found in the Model's event data
     */
    private void populateEventTypeColors() {
        //Generate colors dynamically for each event type
        for (int i = 0; i < getEventTypes().size(); i++) {
            //Generate the first color that hasn't been used. If they've all been used, use a random color
            if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_RED)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_RED);
            } else if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_ORANGE)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_ORANGE);
            } else if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_YELLOW)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_YELLOW);
            } else if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_GREEN)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_GREEN);
            } else if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_CYAN)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_CYAN);
            } else if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_AZURE)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_AZURE);
            } else if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_BLUE)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_BLUE);
            } else if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_VIOLET)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_VIOLET);
            } else if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_MAGENTA)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_MAGENTA);
            } else if (!eventTypeColors.values().contains(BitmapDescriptorFactory.HUE_ROSE)) {
                eventTypeColors.put(eventTypes.get(i), BitmapDescriptorFactory.HUE_ROSE);
            } else {
                eventTypeColors.put(eventTypes.get(i), (float) new Random().nextFloat() * 360);
            }
        }
    }

    /**
     * Sets the default Filters for each eventType to enabled
     */
    private void populateDefaultFilters() {
        for (String eventType : getEventTypes()) {
            mFilter.getEventTypeEnabled().put(eventType, true);
        }
    }

    /**
     * Returns the relationship between two people, used to display this relationship in a personActivity
     *
     * @param basePerson          the person whose relatives are being displayed
     * @param personBeingCompared the relative being displayed
     * @return the relationship of the two persons
     */
    public String getRelation(Person basePerson, Person personBeingCompared) {
        //For each Relative of the base Person,
        if (basePerson != null & personBeingCompared != null) {
            if (basePerson.getFather() != null && basePerson.getFather().equals(personBeingCompared.getPersonID())) {
                return "Father";
            } else if (basePerson.getMother() != null && basePerson.getMother().equals(personBeingCompared.getPersonID())) {
                return "Mother";
            } else if (basePerson.getSpouse() != null && basePerson.getSpouse().equals(personBeingCompared.getPersonID())) {
                return "Spouse";
            } else if (basePerson.getPersonID().equals(personBeingCompared.getFather())
                    || basePerson.getPersonID().equals(personBeingCompared.getMother())) {
                return "Child";
            } else {
                return "";
            }
        } else {
            //This function shouldn't be called when two people aren't related, but if so, return the empty string.
            return "";
        }
    }

    /**
     * Returns a person from the persons map
     *
     * @param personID id of the person to be returned
     * @return person to which the id belongs
     */
    public Person findPerson(String personID) {
        if (personID != null && persons.get(personID) != null) {
            return persons.get(personID);
        } else {
            return null;
        }
    }

    /**
     * Returns a person from the events map
     *
     * @param eventID id of the event to be returned
     * @return event to which the id belongs
     */
    public Event findEvent(String eventID) {
        if (events.get(eventID) != null) {
            return events.get(eventID);
        } else {
            return null;
        }
    }

    /**
     * Returns a map of filtered events by using the current filter preferences selected by the user
     *
     * @return map of filtered events
     */
    public Map<String, Event> getFilteredEvents() {
        mFilteredEvents = new HashMap<>(getEvents());
        mFilteredEvents = filterEventByEventType(mFilteredEvents);
        mFilteredEvents = filterEventByGender(mFilteredEvents);
        mFilteredEvents = filterEventBySide(mFilteredEvents);
        return mFilteredEvents;
    }

    /**
     * Filters events by EventType and returns a map containing the remaining events.
     *
     * @param eventsToBeFiltered the Events to be filtered
     * @return the filtered map of Events
     */
    private Map<String, Event> filterEventByEventType(Map<String, Event> eventsToBeFiltered) {
        Map<String, Event> eventsToFilter = new HashMap<>(eventsToBeFiltered);
        //For each eventType
        for (String eventType : mFilter.getEventTypeEnabled().keySet()) {
            //For each event in the eventsToFilter
            for (Event event : eventsToFilter.values()) {
                //If the eventType is filtered/not enabled and the eventType matches the event
                if (event.getEventType().toLowerCase().equals(eventType.toLowerCase())
                        && !mFilter.getEventTypeEnabled().get(eventType.toLowerCase())) {
                    //Remove that event from the map
                    eventsToBeFiltered.remove(event.getEventID());
                }
                //If it's an invalid event, we filter it out.
                if (event.isInvalidEvent()) {
                    eventsToBeFiltered.remove(event.getEventID());
                }
            }
        }
        return eventsToBeFiltered;
    }

    /**
     * Filters events by Gender and returns a map containing the remaining events
     *
     * @param eventsToBeFiltered the Events to be filtered
     * @return the filtered map of Events
     */
    private Map<String, Event> filterEventByGender(Map<String, Event> eventsToBeFiltered) {
        Map<String, Event> eventsToFilter = new HashMap<>(eventsToBeFiltered);

        for (Event event : eventsToFilter.values()) {
            //Remove events tied to males if Male is not enabled
            if (!mFilter.isMaleEnabled() && findPerson(event.getPersonID()).getGender().equals("m")) {
                eventsToBeFiltered.remove(event.getEventID());
            }
            //Remove events tied to females if Female is not enabled
            if (!mFilter.isFemaleEnabled() && findPerson(event.getPersonID()).getGender().equals("f")) {
                eventsToBeFiltered.remove(event.getEventID());
            }
        }
        return eventsToBeFiltered;
    }

    /**
     * Filters events by Side and returns a map containing the remaining events
     *
     * @param eventsToBeFiltered the Events to be filtered
     * @return the filtered map of Events
     */
    private Map<String, Event> filterEventBySide(Map<String, Event> eventsToBeFiltered) {
        Map<String, Event> eventsToFilter = new HashMap<>(eventsToBeFiltered);

        for (Event event : eventsToFilter.values()) {
            //Remove events tied to the Father's Side if not enabled
            if (!mFilter.isFatherSideEnabled() && paternalAncestors.contains(event.getPersonID())) {
                eventsToBeFiltered.remove(event.getEventID());
            }
            //Remove events of the Mother's Side if not enabled
            if (!mFilter.isMotherSideEnabled() && maternalAncestors.contains(event.getPersonID())) {
                eventsToBeFiltered.remove(event.getEventID());
            }
        }
        return eventsToBeFiltered;

    }

    /**
     * Returns a list of filtered Person Events, using the current filter settings
     *
     * @param personID the id of the person to whom the events are associated
     * @return the filtered list of events
     */
    public List<Event> getFilteredPersonEvents(String personID) {
        List<Event> filteredEventList = new ArrayList<>(getPersonEvents().get(personID));
        //For each event in person events, if that event is not in the filtered list of events, remove it.
        for (Event event : getPersonEvents().get(personID)) {
            if (!getFilteredEvents().values().contains(event)) {
                filteredEventList.remove(event);
            }
        }
        return filteredEventList;
    }

    /**
     * Returns a filtered list of Events that have the search query string in them
     * based on the current filter settings
     *
     * @param queryText the search query string
     * @return the filtered list of events
     */
    public List<Event> getFilteredEventSearchResults(String queryText) {
        if (queryText == null || queryText.equals("")) {
            return null;
        }
        Map<String, Event> filteredEventSearchResults = new HashMap<>();
        queryText = queryText.toLowerCase();
        //If the queryText string is in the event information, we add it to the result set
        for (Event event : events.values()) {
            if (event.getCountry().toLowerCase().contains(queryText)
                    || event.getCity().toLowerCase().contains(queryText)
                    || event.getEventType().toLowerCase().contains(queryText)
                    || event.getYear().toLowerCase().contains(queryText)) {
                filteredEventSearchResults.put(event.getEventID(), event);
            }
        }
        //Filter the Events using the current Filter Settings
        filteredEventSearchResults = filterEventByEventType(filteredEventSearchResults);
        filteredEventSearchResults = filterEventByGender(filteredEventSearchResults);
        filteredEventSearchResults = filterEventBySide(filteredEventSearchResults);

        return new ArrayList<>(filteredEventSearchResults.values());
    }

    /**
     * Returns a filtered list of Persons that have the search string in their name
     * based on the current filter Settings
     *
     * @param queryText the search string
     * @return the filtered list of Persons
     */
    public List<Person> getPersonSearchResults(String queryText) {
        if (queryText == null || queryText.equals("")) {
            return null;
        } else {
            Map<String, Person> personSearchResults = new HashMap<>();
            //If the queryText is in the person's name, we add it to the result set
            for (Person person : persons.values()) {
                if (person != null && person.getFirstName() != null && person.getLastName() != null
                        && (person.getFirstName().toLowerCase().contains(queryText)
                        || person.getLastName().toLowerCase().contains(queryText))) {
                    personSearchResults.put(person.getPersonID(), person);
                }
            }
            return new ArrayList<>(personSearchResults.values());
        }
    }
}