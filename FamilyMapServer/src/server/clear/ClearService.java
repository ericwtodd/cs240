package server.clear;


import api.result.MessageResult;
import server.dataaccess.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A service object that clears the database of all its data from all its tables
 */
public class ClearService {

    /**
     * Default Constructor
     * Used to create a new ClearService object
     */
    public ClearService() {
    }

    /**
     * Deletes all data from the data base, including user accounts, auth tokens, and generated
     * person and event data
     *
     * @return MessageResult describing whether the clear was successful or not
     */
    public MessageResult clear() {
        DatabaseManager dbManager = new DatabaseManager();
        MessageResult result = new MessageResult();
        try {
            //Open a connection to the database
            dbManager.openConnection();
            Connection connection = dbManager.getConn();

            //Create the Dao's interacting with the Database
            EventDao eventDao = new EventDao(connection);
            UserDao userDao = new UserDao(connection);
            PersonDao personDao = new PersonDao(connection);
            AuthTokenDao authDao = new AuthTokenDao(connection);

            //Clear all the tables in the database
            eventDao.clearTable();
            userDao.clearTable();
            personDao.clearTable();
            authDao.clearTable();

            //Close the Connection, Set the message to succeeded
            dbManager.closeConnection(true);
            result.setMessage("Clear succeeded.");
        } catch (SQLException | IllegalArgumentException ex) {
            dbManager.closeConnection(false);
            //MessageResult gets an Error message from the Exception

            result.setMessage(ex.getMessage());
        }
        dbManager = null;
        return result;
    }
}
