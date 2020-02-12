package server.user;


import api.model.AuthToken;
import api.request.UserLoginRequest;
import api.result.UserResult;
import server.dataaccess.AuthTokenDao;
import server.dataaccess.DatabaseManager;
import server.dataaccess.PersonDao;
import server.dataaccess.UserDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/**
 * A service object that Logs the user in and returns an authToken
 */
public class UserLoginService {

    /**
     * Default Constructor
     * Used to create a new UserLoginService object
     */
    public UserLoginService() {
    }

    /**
     * Logs in the user and returns an auth token
     *
     * @param request contains the login information for a given user (username, password)
     * @return UserResult containing an auth token, username, and personID
     */
    UserResult login(UserLoginRequest request) {
        DatabaseManager dbManager = new DatabaseManager();
        UserResult userResult = new UserResult();

        try {
            dbManager.openConnection();
            Connection connection = dbManager.getConn();

            UserDao userDao = new UserDao(connection);
            AuthTokenDao authDao = new AuthTokenDao(connection);

            //Check the request input to make sure it's valid
            if (request.isInvalidRequest()) {
                throw new IllegalArgumentException("Request has missing or invalid values.");
            }

            //Check the User is registered in the User Database
            if (userDao.readUser(request.getUsername()) != null)//If the User is found in the database
            {
                //Check the password is valid in the user database
                if (request.getPassword().equals(userDao.readUser(request.getUsername()).getPassword())) {
                    //The user exists and the password is correct
                    //Create a new AuthToken and Add it to the database with the corresponding userName
                    String tokenValue = UUID.randomUUID().toString();
                    AuthToken token = new AuthToken(tokenValue, request.getUsername());
                    authDao.createAuthToken(token);

                    //Grab the username from the User Table
                    String username = userDao.readUser(request.getUsername()).getUsername();
                    //Grab the personID from the User Table
                    String personID = userDao.readUser(request.getUsername()).getPersonID();

                    //Update the userResult Object
                    userResult.setAuthToken(tokenValue);
                    userResult.setUsername(username);
                    userResult.setPersonID(personID);

                    //We set the error message to null since we succeeded logging in
                    userResult.setMessage(null);

                }
                //Password did not match the user's password in the database
                else {
                    throw new IllegalArgumentException("Incorrect Password");
                }
            } else {
                throw new IllegalArgumentException("User Not Found");
            }
            dbManager.closeConnection(true);

        } catch (SQLException | IllegalArgumentException ex) {
            dbManager.closeConnection(false);
            //MessageResult gets an Error message from the Exception
            userResult.setMessage(ex.getMessage());
            //Set the members to null since the login did not succeed
            userResult.setAuthToken(null);
            userResult.setPersonID(null);
            userResult.setUsername(null);
        }
        dbManager = null;
        return userResult;
    }
}
