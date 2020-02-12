package server.user;

import api.model.Person;
import api.model.User;
import api.request.FillRequest;
import api.request.UserLoginRequest;
import api.request.UserRegisterRequest;
import api.result.MessageResult;
import api.result.UserResult;
import server.dataaccess.*;
import server.fill.FillService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/**
 * A service object that creates a new user account,
 * generates 4 generations of ancestor data for the new user,
 * logs the user in, and returns an auth token
 */
public class UserRegisterService {

    /**
     * Default Constructor
     * Used to create a new UserRegisterService object
     */
    public UserRegisterService() {
    }

    /**
     * Creates a new user account, generated 4 generations of ancestor data for the new user,
     * logs the user in, and returns an auth token
     *
     * @param request Contains the registration information for a new user (username, password, etc.)
     * @return UserResult containing an auth token, username, and personID
     */
    UserResult register(UserRegisterRequest request) {

        DatabaseManager dbManager = new DatabaseManager();
        UserResult userResult = new UserResult();

        //Handler Registering the New User
        try {
            dbManager.openConnection();
            Connection connection = dbManager.getConn();

            //Check that the request has valid member variables
            if (request.isInvalidRequest()) {
                throw new IllegalArgumentException("Request has missing or invalid values.");
            }

            UserDao userDao = new UserDao(connection);

            //Check if the user to be registered already has an account
            if (userDao.readUser(request.getUsername()) != null) {
                //ERROR - "Username already taken by another user" - stop register process
                throw new IllegalArgumentException("Username already taken by another user");
            }
            //The username is not taken, and the User is not yet in the database
            else {
                //Generate a new PersonID
                String personID = UUID.randomUUID().toString();
                //Create a new User from the request
                User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail(),
                        request.getFirstName(), request.getLastName(), request.getGender(), personID);
                //Add the new User to the User table
                userDao.createUser(newUser);
                dbManager.closeConnection(true);
            }
        } catch (SQLException | IllegalArgumentException ex) {
            dbManager.closeConnection(false);
            //MessageResult gets an Error message from the Exception
            userResult.setMessage(ex.getMessage());
            return userResult;
        }

        //Handle Generating 4 generations of ancestors for the new User
        //Create a new MessageResult to receive the result of the fillService call
        MessageResult fillResult;

        //Create a new FillRequest for the new User
        FillRequest fillRequest = new FillRequest(request.getUsername());

        //Run the FillService and retrieve the fillResult
        FillService fillService = new FillService();
        fillResult = fillService.fill(fillRequest);

        //If the fill succeeded, proceed to logging in the User
        if (fillResult.getMessage().contains("Successfully")) {
            //Create a LoginRequest from the RegisterRequest
            UserLoginRequest loginRequest = new UserLoginRequest();
            loginRequest.setUsername(request.getUsername());
            loginRequest.setPassword(request.getPassword());

            //Run the LoginService and Update the UserResult accordingly
            UserLoginService loginService = new UserLoginService();
            userResult = loginService.login(loginRequest);
        }
        //If the fill failed, then we return the an Internal Error Message
        else {
            userResult.setMessage(fillResult.getMessage());
            return userResult;
        }
        dbManager = null;
        return userResult;
    }
}
