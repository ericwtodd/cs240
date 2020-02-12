package server.load;

import api.model.Event;
import api.model.Person;
import api.model.User;
import api.request.LoadRequest;
import api.result.MessageResult;
import server.clear.ClearService;
import server.dataaccess.DatabaseManager;
import server.dataaccess.EventDao;
import server.dataaccess.PersonDao;
import server.dataaccess.UserDao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A service object that clears all the data from the database and then
 * loads the posted user, person, and event data into the database
 */
public class LoadService {

    /**
     * Default Constructor
     * Used to create a new LoadService object
     */
    public LoadService() {
    }

    /**
     * Clears all data from the database, and then loads the posted user,
     * person, and event data into the database
     *
     * @param request the object containing the person, event, and user objects to be
     *                created and added into the database
     * @return MessageResult describing whether or not the load was successful
     */
    public MessageResult load(LoadRequest request) {
        DatabaseManager dbManager = new DatabaseManager();
        MessageResult loadResult = new MessageResult();
        //-> Call the Clear Service to Clear the Database
        ClearService clearService = new ClearService();
        MessageResult clearResult = clearService.clear();
        //If the clear didn't succeed, return whatever error occurred internally there
        if (!clearResult.getMessage().equals("Clear succeeded.")) {
            return clearResult;
        }
        dbManager.openConnection();
        //The Clear Succeeded, and the request values are all valid
        try {
            if (request.isInvalidRequest()) {
                throw new IllegalArgumentException("Invalid request data (missing/invalid values)");
            }
            Connection connection = dbManager.getConn();

            //Connect the necessary Dao's
            UserDao userDao = new UserDao(connection);
            EventDao eventDao = new EventDao(connection);
            PersonDao personDao = new PersonDao(connection);

            //Add all the users to the database
            for (User user : request.getUsers()) {
                userDao.createUser(user);
            }
            //Add all the events to the database
            for (Event event : request.getEvents()) {
                eventDao.createEvent(event);
            }
            //Add all the persons to the database
            for (Person person : request.getPersons()) {
                personDao.createPerson(person);
            }
            String successMessage = "Successfully added " +
                    userDao.getUsersAdded() + " users, " +
                    personDao.getPersonsAdded() + " persons, and " +
                    eventDao.getEventsAdded() + " events to the database.";
            loadResult.setMessage(successMessage);
            dbManager.closeConnection(true);
            return loadResult;
        } catch (SQLException | IllegalArgumentException ex) {
            loadResult.setMessage(ex.getMessage());
            dbManager.closeConnection(false);
            return loadResult;
        }
    }
}