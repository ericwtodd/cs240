package server.dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Determines whether a given token is associated with a registered User
 */
public class AuthService {

    /**
     * Default Constructor to create an AuthService object
     */
    public AuthService() {
    }

    /**
     * Determines the current User from the given AuthToken.
     * Returns the username of the associated current User if it's valid.
     * Otherwise, it returns null.
     *
     * @param token token that is to be checked to see if it's connected to a User
     * @return returns the username of the current user if the authToken is valid
     * returns null if the authToken is not valid.
     */
    public String authorizeUser(String token) {
        DatabaseManager dbManager = new DatabaseManager();
        String userName;
        try {
            //Open a connection to the Database to see if the AuthToken is valid
            dbManager.openConnection();
            Connection connection = dbManager.getConn();
            AuthTokenDao authDao = new AuthTokenDao(connection);

            //If the token is valid
            if (authDao.authorizeUser(token) != null) {
                userName = authDao.authorizeUser(token);
                dbManager.closeConnection(true);
            }
            //The token is invalid
            else {
                throw new IllegalArgumentException("Invalid Authorization Token");
            }
        } catch (SQLException | IllegalArgumentException ex) {
            dbManager.closeConnection(false);
            //The AuthToken doesn't exist, or isn't valid.
            userName = null;
        }
        dbManager = null;
        return userName;
    }
}
