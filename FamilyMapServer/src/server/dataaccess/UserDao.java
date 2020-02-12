package server.dataaccess;

import api.model.User;

import java.sql.*;
import java.util.UUID;

/**
 * A data access object that interacts with the User table in the database
 */
public class UserDao {

    /**
     * A connection to the database for the current transaction
     */
    private Connection connection;

    /**
     * Keeps track of the number of users added in a give transaction for this Dao.
     */
    private int usersAdded;

    public int getUsersAdded() {
        return usersAdded;
    }

    /**
     * Initializes a new UserDao object with a connection created within a service needing this Dao
     *
     * @param conn The connection opened by the service using this Dao.
     */
    public UserDao(Connection conn) {
        usersAdded = 0;
        connection = conn;
    }

    /**
     * Inserts a new User into the database
     *
     * @param user the user to insert
     * @throws SQLException             if the user cannot be added/already exists in the database
     * @throws IllegalArgumentException the user as invalid information
     */
    public void createUser(User user) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        if (user == null) {
            throw new IllegalArgumentException("Cannot add a null User");
        } else if (user.isInvalidUser()) {
            throw new IllegalArgumentException("User with invalid information");
        }
        if (readUser(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already taken by another user");
        }
        try {
            String sql = "INSERT INTO Users " +
                    "(username, password, email, first_name, last_name, gender, personID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setString(6, user.getGender());
            statement.setString(7, user.getPersonID());

            statement.executeUpdate();
            usersAdded += 1;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Returns a User from the database
     *
     * @param username the user's unique username
     * @return User the desired user from the database Users table, null if not found
     * @throws SQLException             if the table doesn't exist
     * @throws IllegalArgumentException if the username is invalid/null
     */
    public User readUser(String username) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        User user;
        if (username == null) {
            throw new IllegalArgumentException("Requested username is null");
        }
        try {
            String sql = "select * from Users where username =  ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            //The personID should be unique, so this should only happen once.
            if (resultSet.next()) {
                user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("gender"),
                        resultSet.getString("personID")
                );
            } else {
                user = null;
            }
            statement.execute();
            return user;
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
     * Removes a User from the database
     *
     * @param username the user's unique username
     * @throws SQLException             if the table doesn't exist
     * @throws IllegalArgumentException if the username is invalid/null
     */
    public void deleteUser(String username) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        if (username == null) {
            throw new IllegalArgumentException("Requested username is null");
        }
        try {
            String sql = "DELETE FROM Users WHERE username = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Creates the User table in the database
     *
     * @throws SQLException failed to create the table
     */
    public void createTable() throws SQLException {
        Statement statement = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Users (" +
                    "  username   TEXT NOT NULL," +
                    "  password   TEXT NOT NULL," +
                    "  email      TEXT NOT NULL," +
                    "  first_name TEXT NOT NULL," +
                    "  last_name  TEXT NOT NULL," +
                    "  gender     TEXT NOT NULL," +
                    "  personID   TEXT NOT NULL," +
                    "  PRIMARY KEY (username)," +
                    "  FOREIGN KEY (personID) REFERENCES Persons (personID)" +
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
     * Deletes the User table from the database
     *
     * @throws SQLException failed to delete the table
     */
    public void deleteTable() throws SQLException {
        Statement statement = null;
        try {
            // drop table if exists
            String sql = "DROP TABLE IF EXISTS Users";
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Clears the Users table, but keeps it in the database
     *
     * @throws SQLException failed to clear the table/the table doesn't exist
     */
    public void clearTable() throws SQLException {
        Statement statement = null;
        try {
            String sql = "DELETE FROM Users";
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

//    /**
//     * Updates a User's information in the database
//     *
//     * @param username the user's unique username
//     */
//    public void updateUser(String username) {
//    }
}
