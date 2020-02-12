package server.dataaccess;

import api.model.AuthToken;

import java.sql.*;
import java.util.UUID;


/**
 * A data access object that interacts with the AuthToken table in the database
 */
public class AuthTokenDao {

    /**
     * A connection to the database for the current transaction
     */
    private Connection connection;

    /**
     * Initializes a new AuthTokenDao object with a connection created within a service needing this Dao
     *
     * @param conn The connection opened by the service using this Dao.
     */
    public AuthTokenDao(Connection conn) {
        connection = conn;
    }

    /**
     * Inserts a new authToken into the database
     *
     * @param token the authToken to be inserted
     * @throws IllegalArgumentException If the token to be added is invalid
     * @throws SQLException             If the token already exists/can't be added
     */
    public void createAuthToken(AuthToken token) throws IllegalArgumentException, SQLException {
        PreparedStatement statement = null;
        if (token == null) {
            //ERROR!
            throw new IllegalArgumentException("Cannot add a null Token");
        } else if (token.isInvalidToken()) {
            throw new IllegalArgumentException("Token with invalid information");
        }
        try {
            String sql = "INSERT INTO AuthTokens (AuthToken, Username) VALUES (?, ?);";
            statement = connection.prepareStatement(sql);
            statement.setString(1, token.getAuthToken());
            statement.setString(2, token.getUsername());
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Returns an auth token from the database
     *
     * @param username the username associated with the current user
     * @return an auth token from the database
     * @throws SQLException             if there is an error accessing the user from the database
     * @throws IllegalArgumentException if the username is null
     */
    public AuthToken readAuthToken(String username) throws SQLException, IllegalArgumentException {
        if (username == null) {
            throw new IllegalArgumentException("Requested username is null");
        }
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        AuthToken token;
        try {
            String sql = "select * from AuthTokens where username =  ?;";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            String authTokenValue = null;
            String uName = null;

            //Get the most recent AuthToken for the current user
            while (resultSet.next()) {
                authTokenValue = resultSet.getString(1);
                uName = resultSet.getString(2);
            }
            //If there were actually AuthTokens associated with this User, create and return it.
            if (authTokenValue != null && uName != null) {
                token = new AuthToken(authTokenValue, uName);
            } else {
                token = null;
            }
            statement.execute();
            return token;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    /**
     * Checks the database for a user with the given AuthToken
     *
     * @param tokenValue the token received to be checked against the user/authToken database
     * @return the username that the token belongs to, or null if the token is not found in the database
     * @throws SQLException             the token is not found in the database
     * @throws IllegalArgumentException the token is null, or invalid
     */
    public String authorizeUser(String tokenValue) throws SQLException, IllegalArgumentException {
        if (tokenValue == null) {
            throw new IllegalArgumentException("Authorization Token is null");
        }
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String username;
        try {
            String sql = "select * from AuthTokens where AuthToken =  ?;";
            statement = connection.prepareStatement(sql);
            statement.setString(1, tokenValue);
            resultSet = statement.executeQuery();

            //Get the most recent AuthToken for the current user
            if (resultSet.next()) {
                username = resultSet.getString(2);
            } else {
                username = null;
            }
            statement.execute();
            return username;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    /**
     * Removes an authToken from the database if it exists
     *
     * @param tokenValue the authToken to be removed
     * @throws SQLException             the table doesn't exist
     * @throws IllegalArgumentException if the authToken passed in is null/invalid
     */
    public void deleteAuthToken(String tokenValue) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        if (tokenValue == null) {
            throw new IllegalArgumentException("Requested authToken is null");
        }
        try {
            String sql = "DELETE FROM AuthTokens WHERE AuthToken = ?;";
            statement = connection.prepareStatement(sql);
            statement.setString(1, tokenValue);
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Creates the authToken table in the database, if it doesn't already exist
     *
     * @throws SQLException failed to create the table
     */
    public void createTable() throws SQLException {
        Statement statement = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS AuthTokens (" +
                    "  AuthToken TEXT NOT NULL," +
                    "  Username  TEXT NOT NULL," +
                    "  PRIMARY KEY (authToken)," +
                    "  FOREIGN KEY (username) REFERENCES Users (username)" +
                    ");";
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Deletes the AuthToken table from the database if it exists
     *
     * @throws SQLException failed to delete the table/table didn't exist
     */
    public void deleteTable() throws SQLException {
        Statement statement = null;
        try {
            // drop table if exists
            String sql = "DROP TABLE IF EXISTS AuthTokens;";
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Clears the AuthToken table, but keeps it in the database
     *
     * @throws SQLException Failed to clear the table/the table doesn't exist.
     */
    public void clearTable() throws SQLException {
        Statement statement = null;
        try {
            //clear table if exists
            String sql = "DELETE FROM AuthTokens;";
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

//    /**
//     * Updates an auth token in the database
//     *
//     * @param username the username associated with the the current User;
//     */
//    public void updateAuthToken(String username) {
//
//    }

}

