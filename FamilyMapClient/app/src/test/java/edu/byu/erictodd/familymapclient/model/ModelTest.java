package edu.byu.erictodd.familymapclient.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.byu.erictodd.familymapclient.network.ServerProxy;

import static org.junit.Assert.*;

public class ModelTest {

    private UserRegisterRequest mRegisterRequest;
    private static Model sModel;
    private static AuthToken currentUserToken;
    private UserResult mUserResult;
    private static ServerProxy mServerProxy;


    static {
        sModel = Model.getInstance();
        mServerProxy = ServerProxy.getInstance();
        //Replace with current IP Address
        mServerProxy.setHostname("10.24.197.169");
        mServerProxy.setPortNumber("8080");
        currentUserToken = new AuthToken();
    }

    @Before
    public void setUp() throws Exception {
        initializeUser();
        //populate the model's person data
        PersonResult personResult = ServerProxy.getInstance().getPersons(currentUserToken);
        sModel.populatePersonData(personResult);
        //Populate the Model's event data
        EventResult eventResult = ServerProxy.getInstance().getEvents(currentUserToken);
        sModel.populateEventData(eventResult);
    }

    /**
     * Registered a user
     */
    private void initializeUser() {
        //Randomize the username so we can run this test without having to clear the database.
        String userName = UUID.randomUUID().toString();
        String password = "password";
        String email = "eric.todd@gmail.com";
        String firstName = "Eric";
        String lastName = "Todd";
        String gender = "m";

        //Log in or Register depending on the test.
        mRegisterRequest = new UserRegisterRequest();
        mRegisterRequest.setUsername(userName);
        mRegisterRequest.setPassword(password);
        mRegisterRequest.setEmail(email);
        mRegisterRequest.setFirstName(firstName);
        mRegisterRequest.setLastName(lastName);
        mRegisterRequest.setGender(gender);
        mUserResult = ServerProxy.getInstance().register(mRegisterRequest);

        currentUserToken = new AuthToken(mUserResult.getAuthToken(), mUserResult.getUsername());
        sModel.getUser().setPersonID(mUserResult.getPersonID());
    }

    @After
    public void tearDown() throws Exception {
    }

    //TEST CALCULATING FAMILY RELATIONSHIPS (SPOUSES, PARENTS, CHILDREN)
    @Test
    public void testPopulatePersonData() {
        //POSITIVE TEST
        //Assert that the various parts were created
        assertFalse(sModel.getPersons().isEmpty());
        assertFalse(sModel.getPersonEvents().isEmpty());
        assertFalse(sModel.getPersonRelatives().isEmpty());
        //Get the user's current relatives to show family relationships are correctly stored in this structure:
        Person user = sModel.getUser();
        List<Person> relatives = sModel.getPersonRelatives().get(user.getPersonID());
        //PARENTS
        //If the user has a mother, assert she's in the relatives list
        if (user.getMother() != null) {
            assertTrue(relatives.contains(sModel.findPerson(user.getMother())));
        } else {
            assertFalse(relatives.contains(sModel.findPerson(user.getMother())));
        }
        //If the user has a father, assert he's in the relatives list
        if (user.getFather() != null) {
            assertTrue(relatives.contains(sModel.findPerson(user.getFather())));
        } else {
            assertFalse(relatives.contains(sModel.findPerson(user.getFather())));
        }
        //SPOUSES
        //Assert that the mother and father of the person are relatives of each other
        List<Person> motherRelatives = sModel.getPersonRelatives().get(user.getMother());
        List<Person> fatherRelatives = sModel.getPersonRelatives().get(user.getFather());
        assertTrue(fatherRelatives.contains(sModel.findPerson(user.getMother())));
        assertTrue(motherRelatives.contains(sModel.findPerson(user.getFather())));
        assertEquals(user.getMother(), sModel.findPerson(user.getFather()).getSpouse());
        //CHILDREN
        //Show that the person is in the father's relatives as a child
        assertTrue(fatherRelatives.contains(user));
        assertTrue(motherRelatives.contains(user));


        //NEGATIVE TESTS

        //Test when someone has no spouse:
        //The user doesn't have a spouse, so this should not contain anything.
        assertFalse(relatives.contains(sModel.findPerson(user.getSpouse())));

        //When a person is there that has no relatives,
        Person newPerson = new Person();
        newPerson.setPersonID(UUID.randomUUID().toString());
        sModel.getPersons().put(newPerson.getPersonID(), newPerson);
        //assert that his relative list is empty
        List<Person> emptyRelatives = sModel.getPersonRelatives().get(newPerson.getPersonID());
        assertNull(emptyRelatives);

    }

    @Test
    public void testGetRelation() {
        //POSITIVE TEST
        //Test that the relationship strings are displaying correctly
        //For father, mother, spouse, and child
        Person user = sModel.getUser();
        Person father = sModel.findPerson(user.getFather());
        Person mother = sModel.findPerson(user.getMother());
        assertEquals(sModel.getRelation(user, father), "Father");
        assertEquals(sModel.getRelation(user, mother), "Mother");
        assertEquals(sModel.getRelation(mother, father), "Spouse");
        assertEquals(sModel.getRelation(father, user), "Child");
        assertEquals(sModel.getRelation(mother, user), "Child");

        //NEGATIVE TESTS

        //Assert that two unrelated people return an empty string as specified in the function description
        assertEquals(sModel.getRelation(sModel.findPerson(father.getFather()),
                sModel.findPerson(mother.getMother())), "");
        //Assert that a null comparison also returns an empty string indicating no relation
        assertEquals(sModel.getRelation(null, father), "");
    }

    //TEST FILTERING EVENTS ACCORDING TO THE CURRENT FILTER SETTINGS:
    @Test
    public void testGetFilteredEvents_AND_GetFilteredPersonEvents() {
        //POSITIVE TEST
        //We pull the user's father's events since those are what we can show is getting filtered
        List<Event> fatherEvents = sModel.getPersonEvents().get(sModel.getUser().getFather());
        List<Event> motherEvents = sModel.getPersonEvents().get(sModel.getUser().getMother());

        Map<String, Event> filteredEvents = sModel.getFilteredEvents();

        //BY GENDER - Male and Female work the same
        //Test when gender filter is on that no events with that gender are in the filtered events
        //Assert that the filtered events contains the father's birth, a male event:
        assertTrue(filteredEvents.containsValue(fatherEvents.get(0)));
        //Disable male events and assert that the filtered events no longer contains the father's birth
        sModel.getFilter().setMaleEnabled(false);
        filteredEvents = sModel.getFilteredEvents();
        assertFalse(filteredEvents.containsValue(fatherEvents.get(0)));

        //Now we assert that the personEvents are also getting filtered similarly
        fatherEvents = sModel.getFilteredPersonEvents(sModel.getUser().getFather());
        assertTrue(fatherEvents.isEmpty());

        //And if we enable the male filter, then the father events is no longer empty
        sModel.getFilter().setMaleEnabled(true);
        fatherEvents = sModel.getFilteredPersonEvents(sModel.getUser().getFather());
        assertFalse(fatherEvents.isEmpty());
        filteredEvents = sModel.getFilteredEvents();
        assertTrue(filteredEvents.containsValue(fatherEvents.get(0)));

        //BY SIDE - Mother's and fathers work the same
        //Test when side filter is on that no events with that side are in the filtered events
        //Assert that the filtered events contains events from the mother's side:
        assertTrue(filteredEvents.containsValue(motherEvents.get(0)));
        //Disable the mother's side and assert that the filtered events no longer contain the mother's events
        // and that the mother's events are empty
        sModel.getFilter().setMotherSideEnabled(false);
        for (Event event : motherEvents) {
            assertFalse(sModel.getFilteredEvents().containsValue(event));
        }
        motherEvents = sModel.getFilteredPersonEvents(sModel.getUser().getMother());
        assertTrue(motherEvents.isEmpty());
        sModel.getFilter().setMotherSideEnabled(true);

        //BY EVENT TYPE - each event Type works the same
        //Test when an event type is filtered that no events with that event type are in the filtered events.
        //Get the events again & show how the marriage events of the mother and father
        filteredEvents = sModel.getEvents();
        fatherEvents = sModel.getPersonEvents().get(sModel.getUser().getFather());
        motherEvents = sModel.getPersonEvents().get(sModel.getUser().getMother());
        assertTrue(filteredEvents.containsValue(fatherEvents.get(1)));
        assertTrue(filteredEvents.containsValue(motherEvents.get(1)));

        //Then disable birth events and show that the fathers and mother's
        // birth events are no longer in their filtered person events, and no
        // marriage events are in the filtered events
        sModel.getFilter().getEventTypeEnabled().put("Marriage".toLowerCase(), false);
        filteredEvents = sModel.getFilteredEvents();
        assertFalse(filteredEvents.containsValue(fatherEvents.get(1)));
        assertFalse(filteredEvents.containsValue(motherEvents.get(1)));
        for (Event event : filteredEvents.values()) {
            assertTrue(!event.getEventType().equals("Marriage".toLowerCase()));
        }

        //NEGATIVE TESTS

        //Add an event w/out an eventType, and assert that it will be filtered since it has no type
        Event event0 = new Event(UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                "40.75", "-110.1167", "United States",
                "Salt Lake City", "", "1980");

        sModel.getEvents().put(event0.getEventID(), event0);
        assertFalse(sModel.getFilteredEvents().containsValue(event0));

        //Add an event w/out a person associated and assert it will be filtered out since
        //It's not associated with a specific person
        Event event1 = new Event(UUID.randomUUID().toString(), "etodd", null,
                "40.75", "-110.1167", "United States",
                "Salt Lake City", "Hamburger Contest", "1980");

        sModel.getEvents().put(event1.getEventID(), event1);
        assertFalse(sModel.getFilteredEvents().containsValue(event1));
    }

    //TEST CHRONOLOGICALLY SORTING A PERSON'S INDIVIDUAL EVENTS:
    @Test
    public void testPersonSortEvents() {
        //In my project, I implemented Comparable on Event. This is how I sorted things,
        // so this is really just testing if the Event class implements comparable correctly
        // like the spec suggests, which implies they are sorted correctly:

        //POSITIVE TEST
        //Get the list of the user's events
        List<Event> userEvents = new ArrayList<>(sModel.getPersonEvents().get(sModel.getUser().getFather()));
        Collections.sort(userEvents);
        //Show that the events are now sorted by asserting that birth is first, marriage is second and death is third
        assertEquals(userEvents.get(0).getEventType(), "Birth".toLowerCase());
        assertEquals(userEvents.get(1).getEventType(), "Marriage".toLowerCase());


        //Add in some other events to this list with different event types to show that the sort still
        //works properly.
        Event event0 = new Event(UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                "40.75", "-110.1167", "United States",
                "Salt Lake City", "birth", "1980");
        Event event1 = new Event(
                UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                "40.75", "-110.1167", "United States",
                "Salt Lake City", "beforeMarriage", "1996");

        Event event2 = new Event(
                UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                "40.75", "-110.1167", "New Zealand",
                "Auckland", "death", "1996");
        Event event3 = new Event(
                UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                "40.75", "-110.1167", "United States",
                "Oakland", "marriage", "1996");

        userEvents.clear();
        userEvents.add(event0);
        userEvents.add(event2);
        userEvents.add(event3);
        userEvents.add(event1);

        Collections.sort(userEvents);
        //Assert that birth comes first, then show that it sorts by event type name
        //and then that death comes last.
        assertEquals(userEvents.get(0).getEventType(), "Birth".toLowerCase());
        assertEquals(userEvents.get(1).getEventType(), event1.getEventType());
        assertEquals(userEvents.get(2).getEventType(), event3.getEventType());
        assertEquals(userEvents.get(3).getEventType(), "Death".toLowerCase());

        //NEGATIVE TEST
        //Show an invalid event throws an exception
        try {
            Event event4 = new Event(
                    UUID.randomUUID().toString(), "etodd", UUID.randomUUID().toString(),
                    "40.75", "-110.1167", "United States",
                    "Oakland", null, "1996");
            userEvents.add(event4);
            Collections.sort(userEvents);
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Event is invalid");
        }
        //Show a null event also throws an invalid exception, and is not comparable, as desired.
        try {
            userEvents.add(null);
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Event is invalid");
        }

    }

    //TEST SEARCHING FOR PEOPLE AND EVENTS:
    @Test
    public void testFindPerson() {
        //This is a simple retrieval of a person from the model class, and use/testing of this function is
        // found elsewhere in other tests.
        //POSITIVE TEST
        Person user = sModel.getUser();
        //Show that the person is in the Model
        assertNotNull(sModel.findPerson(user.getPersonID()));
        assertEquals(user, sModel.findPerson(user.getPersonID()));

        //NEGATIVE TESTS
        //Assert that a random ID is not found in persons
        assertNull(sModel.findPerson(UUID.randomUUID().toString()));
    }

    @Test
    public void testFindEvent_AND_PopulateEventData() {
        //POSITIVE TEST
        //Make sure the event data is not empty after being read into the model
        //We read it in in the @Before task of this test.
        assertFalse(sModel.getEvents().isEmpty());

        //Assert that the user's first event is in the list of events
        Event userFirstEvent = sModel.getPersonEvents().get(sModel.getUser().getPersonID()).get(0);
        assertNotNull(sModel.findEvent(userFirstEvent.getEventID()));
        assertEquals(userFirstEvent, sModel.findEvent(userFirstEvent.getEventID()));

        //NEGATIVE TESTS
        //Assert that a random ID is not found in the Events
        assertNull(sModel.findEvent(UUID.randomUUID().toString()));
        //Assert that a null ID is not found in Events
        assertNull(sModel.findEvent(null));
    }

    @Test
    public void testGetFilteredEventSearchResults() {
        //POSITIVE TEST
        String queryText;
        //Test searching for events with 1 character, which implies more than one character works as well
        queryText = "a";
        List<Event> searchEvents = sModel.getFilteredEventSearchResults(queryText);
        assertFalse(searchEvents.isEmpty());

        //Now filter and show that the filtered search results don't include those previously included results.
        //Disable the searchEvent's first eventType
        String eventTypeToFilter = searchEvents.get(0).getEventType();
        sModel.getFilter().getEventTypeEnabled().put(eventTypeToFilter, false);
        searchEvents = sModel.getFilteredEventSearchResults(queryText);
        //And assert that no events of that type are in the search results anymore.
        for (Event event : searchEvents) {
            assertFalse(event.getEventType().toLowerCase().equals(eventTypeToFilter.toLowerCase()));
        }

        //Test searching for events with all filters on
        //Assert that if the male and female event filters are disabled,
        //that the list of search events will be empty
        sModel.getFilter().setMaleEnabled(false);
        sModel.getFilter().setFemaleEnabled(false);
        searchEvents = sModel.getFilteredEventSearchResults(queryText);
        assertTrue(searchEvents.isEmpty());

        //Reset the filters to enabled
        sModel.getFilter().getEventTypeEnabled().put(eventTypeToFilter, true);
        sModel.getFilter().setMaleEnabled(true);
        sModel.getFilter().setFemaleEnabled(true);


        //NEGATIVE TESTS
        //Test searching with an empty string and assert null since the query can't return any events
        queryText = "";
        searchEvents = sModel.getFilteredEventSearchResults(queryText);
        assertNull(searchEvents);

        //Test searching with a null string & assert null since the query can't return any events
        queryText = null;
        searchEvents = sModel.getFilteredEventSearchResults(queryText);
        assertNull(searchEvents);
    }

    @Test
    public void testGetPersonSearchResults() {
        //POSITIVE TEST
        //Since persons can't be disabled from searches
        String queryText;
        //Test searching for persons using 1 character
        queryText = "a";
        List<Person> searchPersons = sModel.getPersonSearchResults(queryText);
        assertFalse(searchPersons.isEmpty());
        //Test searching for persons using more than 1 character
        queryText = "at";
        List<Person> searchPersons2 = sModel.getPersonSearchResults(queryText);
        assertFalse(searchPersons2.isEmpty());
        assertTrue(searchPersons.size() >= searchPersons2.size());


        //NEGATIVE TESTS
        //Test searching with an empty string and assert null since the query can't return any persons
        queryText = "";
        searchPersons = sModel.getPersonSearchResults(queryText);
        assertNull(searchPersons);

        //Test searching with a null string & assert null since the query can't return any persons
        queryText = null;
        searchPersons = sModel.getPersonSearchResults(queryText);
        assertNull(searchPersons);
    }
}