package server.dataaccess;

import api.model.Event;
import api.model.Person;

import java.sql.*;
import java.util.*;

/**
 * Manages opening and closing connections to the database, as well as initializing the database
 * to create all the tables at the server's start
 */
public class DatabaseManager {

    //    Installs the correct Driver in order to be able to use a SQLite Database
    static {
        try {
            final String driver = "org.sqlite.JDBC";
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * A connection to the SQLite Database
     */
    private Connection connection;
    /**
     * Whether or not a transaction should be committed to or rolled back from the database
     */
    private boolean commit;

    public boolean getCommit() {
        return commit;
    }

    public void setCommit(boolean commit) {
        this.commit = commit;
    }

    public Connection getConn() {
        return connection;
    }

    /**
     * Opens a connection to the database
     */
    public void openConnection() {
        try {
            //Get Correct File Path to the Database:
            // C:\Users\Eric Todd\IdeaProjects\FamilyMapServer\src\server\database\database.db
            final String CONNECTION_URL = "jdbc:sqlite:src\\server\\database\\database.db";

            // Open a database connection
            connection = DriverManager.getConnection(CONNECTION_URL);

            // Start a transaction
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("openConnection failed");
        }
    }

    /**
     * Handles whether or not the changes to the database should be committed or not,
     * And closes the connection for the current Database Transaction
     *
     * @param commit whether or not the changes to the database should be committed or rolled back
     */
    public void closeConnection(boolean commit) {
        try {
            if (commit) {
                connection.commit();
            } else {
                connection.rollback();
            }
            connection.close();
            connection = null;
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("closeConnection failed");
        }
    }

    /**
     * Initializes all the tables in the database by creating them if they don't exist
     * This function is only called once in the Main Server Class to ensure the tables always exist.
     */
    public void InitializeDatabase() {
        openConnection();
        EventDao eventDao = new EventDao(getConn());
        UserDao userDao = new UserDao(getConn());
        PersonDao personDao = new PersonDao(getConn());
        AuthTokenDao authDao = new AuthTokenDao(getConn());

        try {
            eventDao.createTable();

            userDao.createTable();

            personDao.createTable();

            authDao.createTable();

            closeConnection(true);
        } catch (SQLException ex) {
            closeConnection(false);
//            ex.printStackTrace();
        }
    }
}


