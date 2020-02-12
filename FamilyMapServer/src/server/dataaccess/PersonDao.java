package server.dataaccess;

import api.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A data access object that interacts with the Person table in the database
 */
public class PersonDao {

    /**
     * A connection to the database for the current transaction
     */
    private Connection connection;
    /**
     * Keeps track of how many persons were added in a transaction by this Dao.
     */
    private int personsAdded;

    public int getPersonsAdded() {
        return personsAdded;
    }

    /**
     * Initializes a new PersonDao object with a connection created within a service needing this Dao
     *
     * @param conn The connection opened by the service using this Dao.
     */
    public PersonDao(Connection conn) {
        personsAdded = 0;
        connection = conn;
    }

    /**
     * Inserts a person into the database
     *
     * @param person the Person to insert
     * @throws SQLException             if the person can't be created/table doesn't exist
     * @throws IllegalArgumentException if person is invalid/null
     */
    public void createPerson(Person person) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        if (person == null) {
            throw new IllegalArgumentException("Cannot add a null Person");
        } else if (person.isInvalidPerson()) {
            throw new IllegalArgumentException("Person with invalid information");
        }
        if (readPerson(person.getPersonID()) != null) {
            throw new IllegalArgumentException("A person with this PersonID already exists");
        }
        try {
            String sql = "INSERT INTO Persons " +
                    "(personID, descendant, first_name, last_name, gender, father, mother, spouse) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);

            statement.setString(1, person.getPersonID());
            statement.setString(2, person.getDescendant());
            statement.setString(3, person.getFirstName());
            statement.setString(4, person.getLastName());
            statement.setString(5, person.getGender());
            if (person.getFather() != null) {
                statement.setString(6, person.getFather());
            } else {
                statement.setString(6, null);
            }
            if (person.getMother() != null) {
                statement.setString(7, person.getMother());
            } else {
                statement.setString(7, null);
            }
            if (person.getSpouse() != null) {
                statement.setString(8, person.getSpouse());
            } else {
                statement.setString(8, null);
            }
            statement.executeUpdate();
            personsAdded += 1;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Returns a person from the database
     *
     * @param personID the Person to be returned's unique ID
     * @return Person the desired person from the database, null if not found
     * @throws SQLException             if table doesn't exist
     * @throws IllegalArgumentException if personID is invalid or null
     */
    public Person readPerson(String personID) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Person person;
        if (personID == null) {
            throw new IllegalArgumentException("Requested personID is null");
        }
        try {
            String sql = "select * from Persons where personID =  ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, personID);
            resultSet = statement.executeQuery();
            //The personID should be unique, so this should only happen once.
            if (resultSet.next()) {
                person = new Person(
                        resultSet.getString("personID"),
                        resultSet.getString("descendant"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("gender"),
                        resultSet.getString("father"),
                        resultSet.getString("mother"),
                        resultSet.getString("spouse")
                );
            } else {
                person = null;
            }
            statement.execute();
            return person;
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
     * Returns all the persons associated with the current user (that have as their descendant - username)
     *
     * @param username the current user that is the descendant of persons in the database.
     * @return persons associated with the current user, null if none found
     * @throws SQLException             if there aren't persons associated with the username, or another database error occurs
     * @throws IllegalArgumentException if the arguments provided don't match the requirements
     */
    public ArrayList<Person> getUserFamily(String username) throws SQLException, IllegalArgumentException {
        ArrayList<Person> family = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if (username == null) {
            throw new IllegalArgumentException("Requested username is null");
        }
        try {
            String sql = "select * from Persons where descendant =  ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            //Get all the people associated with the descendant (username)
            while (resultSet.next()) {
                Person person = new Person(
                        resultSet.getString("personID"),
                        resultSet.getString("descendant"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("gender"),
                        resultSet.getString("father"),
                        resultSet.getString("mother"),
                        resultSet.getString("spouse")
                );
                family.add(person);
            }
            if (family.isEmpty()) {
                family = null;
            }
            statement.execute();
            return family;
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
     * Removes a Person from the database
     *
     * @param personID the Person to be removed's unique ID
     * @throws SQLException             if the table doesn't exist/can't delete a person
     * @throws IllegalArgumentException if the personID is null/invalid
     */
    public void deletePerson(String personID) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        if (personID == null) {
            throw new IllegalArgumentException("Requested personID is null");
        }
        try {
            String sql = "DELETE FROM Persons WHERE personID = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, personID);
            statement.executeUpdate();
            //Since we're removing someone, we remove one from the total of personsAdded
            personsAdded -= 1;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Deletes all persons associated with the current user (chosen by descendant)
     *
     * @param username Current user for which all persons are being cleared
     * @throws SQLException             There are no persons related to the current user
     * @throws IllegalArgumentException The username passed is null
     */
    public void deleteFamily(String username) throws SQLException, IllegalArgumentException {
        PreparedStatement statement = null;
        if (username == null) {
            throw new IllegalArgumentException("Requested username is null");
        }
        try {
            String sql = "DELETE FROM Persons WHERE descendant = ?";
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
     * Creates the Person table in the database
     *
     * @throws SQLException if the table can't be created
     */
    public void createTable() throws SQLException {
        Statement statement = null;
        try {
            String sql = "CREATE TABLE If NOT EXISTS Persons (" +
                    "  personID   TEXT NOT NULL," +
                    "  descendant TEXT NOT NULL," +
                    "  first_name TEXT NOT NULL," +
                    "  last_name  TEXT NOT NULL," +
                    "  gender     TEXT NOT NULL," +
                    "  father     TEXT," +
                    "  mother     TEXT," +
                    "  spouse     TEXT," +
                    "  PRIMARY KEY (personID)," +
                    "  FOREIGN KEY (descendant) REFERENCES Users (username)" +
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
     * Deletes the Person table from the database
     *
     * @throws SQLException if the table can't be deleted
     */
    public void deleteTable() throws SQLException {
        Statement statement = null;
        try {
            // drop table if exists
            String sql = "DROP TABLE IF EXISTS Persons";
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Clears the Persons table, but keeps it in the database
     *
     * @throws SQLException if the table can't be cleared
     */
    public void clearTable() throws SQLException {
        Statement statement = null;
        try {
            //clear table if exists
            String sql = "DELETE FROM Persons";
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

//    /**
//     * Updates information about a person in the database
//     *
//     * @param person the Person to be updated
//     */
//    public void updatePerson(Person person) {
//    }
}
