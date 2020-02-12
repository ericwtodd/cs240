package server.fill;

import api.model.Event;
import api.model.Location;
import api.model.Person;
import api.model.User;
import api.request.FillRequest;
import api.request.UserLoginRequest;
import api.result.MessageResult;
import server.Generator;
import server.dataaccess.DatabaseManager;
import server.dataaccess.EventDao;
import server.dataaccess.PersonDao;
import server.dataaccess.UserDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;


/**
 * A service object that populates the server's database with generated data for the specified username
 */
public class FillService {

    private Generator generator;
    private DatabaseManager dbManager;
    private MessageResult messageResult;
    private PersonDao personDao;
    private EventDao eventDao;
    private UserDao userDao;
    private Connection connection;

    /**
     * Default Constructor
     * Used to create new FillService objects
     */
    public FillService() {
        generator = new Generator(true);
        dbManager = new DatabaseManager();
        messageResult = new MessageResult();
    }

    /**
     * Populates the server's database with generated data for the specified username
     * If there is any data in the database already associated with the given username, it is deleted.
     *
     * @param request A FillRequest has a username member which must correspond to
     *                a User that is already registered in the database.
     *                It has an optional generations parameter that can specify the number of
     *                generations of ancestors to be generated, and must be non-negative
     * @return MessageResult describing whether the fill was successful or not.
     */
    public MessageResult fill(FillRequest request) {
        dbManager.openConnection();
        try {
            //Check the Request input to make sure it's valid
            if (request.isInvalidRequest()) {
                throw new IllegalArgumentException("Request username or generations is invalid");
            }
            connection = dbManager.getConn();

            userDao = new UserDao(connection);
            personDao = new PersonDao(connection);
            eventDao = new EventDao(connection);

            //Check the User is registered in the User Database
            //Checks if the username exists in the database
            if (userDao.readUser(request.getUsername()) != null)//If the User is found in the database
            {
                //Clear the Persons and Events Associated with this user.
                eventDao.deleteFamilyEvents(request.getUsername());
                personDao.deleteFamily(request.getUsername());

                //Since the current User will always have a Person created for them, we will
                //Generate a Person and Events for them:
                String currentUserPersonID = userDao.readUser(request.getUsername()).getPersonID();
                String currentUserFirstName = userDao.readUser(request.getUsername()).getFirstName();
                String currentUserLastName = userDao.readUser(request.getUsername()).getLastName();
                String currentUserGender = userDao.readUser(request.getUsername()).getGender();
                //We are only adding the user to the tree, so we don't include his parents or spouse
                String fatherID = null;
                String motherID = null;
                String spouseID = null;
                //Create a new Person Object for the specified Username.
                Person currentUser = new Person(currentUserPersonID, request.getUsername(),
                        currentUserFirstName, currentUserLastName, currentUserGender,
                        fatherID, motherID, spouseID);
                //Add the user's Person to the Database
                personDao.createPerson(currentUser);

                //Generate Events for the current User
                ArrayList<Event> currentUserEvents = generateEvents(currentUser, 0);
                //Add them to the Events Table
                for (Event event : currentUserEvents) {
                    eventDao.createEvent(event);
                }

                //If more than just the user is requested to be populated, then we call our function.
                if (request.getGenerations() > 0) {
                    int generations = request.getGenerations();
                    generateParents(generations, request);
                }
            } else {
                throw new IllegalArgumentException("User Not Found");
            }
            //Set Success MessageResult
            messageResult.setMessage("Successfully added " +
                    personDao.getPersonsAdded() + " persons and " +
                    eventDao.getEventsAdded() + " events to the database.");
            dbManager.closeConnection(true);
        } catch (SQLException | IllegalArgumentException ex) {
            messageResult.setMessage(ex.getMessage());
//            ex.printStackTrace();
            dbManager.closeConnection(false);
        }
        return messageResult;
    }

    private void generateParents(int generations, FillRequest request) throws SQLException, IllegalArgumentException {
        while (generations > 0) {
            //For each person in the User's family
            for (Person p : personDao.getUserFamily(request.getUsername())) {
                int childBirthYear = 0;
                for (Event event : eventDao.getUserFamilyEvents(p.getDescendant())) {
                    if (p.getPersonID().equals(event.getPersonID()) && event.getEventType().equals("birth")) {
                        childBirthYear = Integer.valueOf(event.getYear());
                    }
                }
                //If the person doesn't yet have parents, and there are
                //still generations to populate, we will create parents
                if (p.getFather() == null && p.getMother() == null) {
                    //Create Parents for p
                    String fatherID = UUID.randomUUID().toString();
                    String motherID = UUID.randomUUID().toString();

                    //HERE WOULD BE A CASE TO USE UPDATEPERSON FUNCTION
                    //Update the child's parents
                    p.setFather(fatherID);
                    p.setMother(motherID);

                    //Re-add the person to the database
                    personDao.deletePerson(p.getPersonID());
                    personDao.createPerson(p);

                    //Create the Father and Mother Persons
                    Person father = new Person(fatherID, p.getDescendant(),
                            generator.generateMaleName(), p.getLastName(), "m",
                            null, null, motherID);
                    //Give the mother her maiden name
                    Person mother = new Person(motherID, p.getDescendant(),
                            generator.generateFemaleName(), generator.generateLastName(), "f",
                            null, null, fatherID);
                    //Add the father and mother
                    personDao.createPerson(father);
                    personDao.createPerson(mother);

                    //Create Events for Parents of p
                    ArrayList<Event> fatherEvents = generateEvents(father, childBirthYear);
                    ArrayList<Event> motherEvents = generateEvents(mother, childBirthYear);
                    Event marriage = new Event();
                    for (Event event : fatherEvents) {
                        //Copy the father's marriage event data
                        if (event.getEventType().equals("marriage")) {
                            marriage = event;
                        }
                        eventDao.createEvent(event);
                    }
                    for (Event event : motherEvents) {
                        if (event.getEventType().equals("marriage")) {
                            //Make sure the mother's marriage event data
                            //Is all the same except the personID and eventID
                            marriage.setPersonID(event.getPersonID());
                            marriage.setEventID(event.getEventID());
                            event = marriage;
                        }
                        eventDao.createEvent(event);
                    }
                }
            }
            generations--;
        }
    }

    private ArrayList<Event> generateEvents(Person person, int baseYear) {

        ArrayList<Event> events = new ArrayList<>();

        int birthYear;
        int marriageYear;
        int deathYear;

        //We are populating events for the current User's Person
        if (baseYear == 0) {
            //Random number between 1950-2000
            int lowerBirthYear = 1950;
            int upperBirthYear = 2000;
            birthYear = (int) (Math.random() * (upperBirthYear - lowerBirthYear)) + lowerBirthYear;

            //marriage year is 20-35 years after birth year
//            int lowerMarriageYear = birthYear + 20;
//            int upperMarriageYear = birthYear + 35;
//            marriageYear = (int) (Math.random() * (upperMarriageYear - lowerMarriageYear)) + lowerMarriageYear;
            //can't pass 2018
//            if (marriageYear > 2018) {
                marriageYear = 0;
//            }
            //Current User isn't dead yet.
            deathYear = 0;
        } else {
            //Parents were married 1 to 18 years before their child was born
            int lowerMarriageYear = baseYear - 18;
            int upperMarriageYear = baseYear - 1;
            marriageYear = (int) (Math.random() * (upperMarriageYear - lowerMarriageYear)) + lowerMarriageYear;

            //Parents were born 19 to 35 years before they were married
            int lowerBirthYear = marriageYear - 35;
            int upperBirthYear = marriageYear - 19;
            birthYear = (int) (Math.random() * (upperBirthYear - lowerBirthYear)) + lowerBirthYear;

            //Parents die 35 to 65 years after their kids are born
            int lowerDeathYear = baseYear + 35;
            int upperDeathYear = baseYear + 65;
            deathYear = (int) (Math.random() * (upperDeathYear - lowerDeathYear)) + lowerDeathYear;
            //If the death year is after 2018, they haven't died, and we won't populate those events.
            if (deathYear > 2018) {
                deathYear = 0;
            }
        }
        //Create a birth event
        if (birthYear != 0) {
            Location birthLocation = generator.generateLocation();
            Event birth = new Event(UUID.randomUUID().toString(), person.getDescendant(), person.getPersonID(),
                    birthLocation.getLatitude(), birthLocation.getLongitude(), birthLocation.getCountry(),
                    birthLocation.getCity(), "birth", String.valueOf(birthYear));
            //add it to the list of events to be returned
            events.add(birth);
        }
        //Create a marriage event
        if (marriageYear != 0) {
            Location marriageLocation = generator.generateLocation();
            Event marriage = new Event(UUID.randomUUID().toString(), person.getDescendant(), person.getPersonID(),
                    marriageLocation.getLatitude(), marriageLocation.getLongitude(),
                    marriageLocation.getCountry(), marriageLocation.getCity(), "marriage", String.valueOf(marriageYear));
            //add it to the list of events to be returned
            events.add(marriage);
        }
        //Create a death event
        if (deathYear != 0) {
            Location deathLocation = generator.generateLocation();
            Event death = new Event(UUID.randomUUID().toString(), person.getDescendant(), person.getPersonID(),
                    deathLocation.getLatitude(), deathLocation.getLongitude(),
                    deathLocation.getCountry(), deathLocation.getCity(), "death", String.valueOf(deathYear));
            //add it to the list of events to be returned
            events.add(death);
        }
        return events;
    }
}