package edu.byu.erictodd.familymapclient.network;


import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.internal.MethodSorter;
import org.junit.runners.MethodSorters;

import java.util.UUID;

import edu.byu.erictodd.familymapclient.model.AuthToken;
import edu.byu.erictodd.familymapclient.model.EventResult;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.PersonResult;
import edu.byu.erictodd.familymapclient.model.UserLoginRequest;
import edu.byu.erictodd.familymapclient.model.UserRegisterRequest;
import edu.byu.erictodd.familymapclient.model.UserResult;
import edu.byu.erictodd.familymapclient.userinterface.EventRecyclerAdapter;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServerProxyTest {

    private UserLoginRequest mLoginRequest;
    private UserRegisterRequest mRegisterRequest;
    private UserResult mUserResult;
    private static ServerProxy mServerProxy;
    private static String loginUserName;
    private static String loginPassword;
    private static AuthToken currentUserToken;
    private PersonResult mPersonResult;
    private EventResult mEventResult;

    static {
        mServerProxy = ServerProxy.getInstance();
        //Replace with current IP Address
        mServerProxy.setHostname("10.24.197.169");
        mServerProxy.setPortNumber("8080");
        currentUserToken = new AuthToken();
    }

    @Before
    public void setUp() throws Exception {
        mLoginRequest = new UserLoginRequest();
        mRegisterRequest = new UserRegisterRequest();
        mUserResult = new UserResult();
    }

    @After
    public void tearDown() throws Exception {
    }

    //Test Register first so that login can be tested correctly
    @Test
    public void test1Register() {
        //POSITIVE TEST
        //Get Register Information
        //Use a UUID in order to run this indefinitely
        String userName = UUID.randomUUID().toString();
        String password = "password";
        String email = "eric.todd@gmail.com";
        String firstName = "Eric";
        String lastName = "Todd";
        String gender = "m";
        mRegisterRequest.setUsername(userName);
        mRegisterRequest.setPassword(password);
        mRegisterRequest.setEmail(email);
        mRegisterRequest.setFirstName(firstName);
        mRegisterRequest.setLastName(lastName);
        mRegisterRequest.setGender(gender);

        //Call the serverProxy Method
        mUserResult = mServerProxy.register(mRegisterRequest);

        //Store the first login & password to test login.
        loginUserName = userName;
        loginPassword = password;

        //Assert that the register was successful, and the personID & an AuthToken are returned.
        assertEquals(mUserResult.getUsername(), userName);
        assertNotNull(mUserResult.getAuthToken());
        assertNotNull(mUserResult.getAuthToken());

        //Assert null to the error message
        assertNull(mUserResult.getMessage());

        //NEGATIVE TESTS

        //Try Registering the the same user twice.
        mUserResult = ServerProxy.getInstance().register(mRegisterRequest);
        //The correct error message is contained in the result
        assertEquals(mUserResult.getMessage(), "Username already taken by another user");
        //The other values in the result are null
        assertNull(mUserResult.getAuthToken());
        assertNull(mUserResult.getUsername());
        assertNull(mUserResult.getPersonID());

        //Try Registering with invalid information:
        userName = UUID.randomUUID().toString();
        email = "";
        firstName = null;
        mRegisterRequest.setEmail(email);
        mRegisterRequest.setFirstName(firstName);

        //Run the ServerProxy Method
        mUserResult = ServerProxy.getInstance().register(mRegisterRequest);
        //The correct error message is contained in the result
        assertEquals(mUserResult.getMessage(), "Request has missing or invalid values.");
        //The other values in the result are null
        assertNull(mUserResult.getAuthToken());
        assertNull(mUserResult.getUsername());
        assertNull(mUserResult.getPersonID());
    }

    @Test
    public void test2Login() {
        //POSITIVE TEST
        String userName = loginUserName;
        String password = loginPassword;
        mLoginRequest.setUsername(userName);
        mLoginRequest.setPassword(password);
        mUserResult = ServerProxy.getInstance().login(mLoginRequest);

        //Assert that the login was successful, and the personID & an AuthToken are returned.
        assertEquals(mUserResult.getUsername(), userName);

        assertNotNull(mUserResult.getAuthToken());
        currentUserToken.setUsername(mUserResult.getUsername());
        currentUserToken.setAuthToken(mUserResult.getAuthToken());

        assertNotNull(mUserResult.getPersonID());
    }

    @Test
    public void test3LoginNegative() {
        //NEGATIVE TESTS

        //Try logging in with bad request parameters
        String userName = "";
        String password = "password15";
        mLoginRequest.setUsername(userName);
        mLoginRequest.setPassword(password);

        mUserResult = ServerProxy.getInstance().login(mLoginRequest);

        //The correct error message is contained in the result
        assertEquals(mUserResult.getMessage(), "Request has missing or invalid values.");
        //The other values in the result are null
        assertNull(mUserResult.getAuthToken());
        assertNull(mUserResult.getUsername());
        assertNull(mUserResult.getPersonID());

        //Try logging in with the wrong password
        String userName2 = loginUserName;
        String password2 = "wrongPassword";
        mLoginRequest.setUsername(userName2);
        mLoginRequest.setPassword(password2);

        mUserResult = ServerProxy.getInstance().login(mLoginRequest);

        //The correct error message is contained in the result
        assertEquals(mUserResult.getMessage(), "Incorrect Password");
        //The other values in the result are null
        assertNull(mUserResult.getAuthToken());
        assertNull(mUserResult.getUsername());
        assertNull(mUserResult.getPersonID());

        //Try logging in a user that is not in the database
        String userName3 = "JOSE";
        String password3 = "contrasena";
        mLoginRequest.setUsername(userName3);
        mLoginRequest.setPassword(password3);

        mUserResult = ServerProxy.getInstance().login(mLoginRequest);

        //The correct error message is contained in the result
        assertEquals(mUserResult.getMessage(), "User Not Found");
        //The other values in the result are null
        assertNull(mUserResult.getAuthToken());
        assertNull(mUserResult.getUsername());
    }

    @Test
    public void test4GetPersons() {
        //POSITIVE TEST
        //Get a personResult from calling the serverProxy method
        mPersonResult = mServerProxy.getPersons(currentUserToken);

        //Assert that the personResult is not null
        assertNotNull(mPersonResult.getPersons());
        //Assert that a single person is null
        assertNull(mPersonResult.getPerson());
        //Assert that the error message is null
        assertNull(mPersonResult.getMessage());

        //NEGATIVE TESTS
        //Try using an invalid AuthToken to request persons
        AuthToken personToken = new AuthToken(UUID.randomUUID().toString(), "");
        mPersonResult = mServerProxy.getPersons(personToken);
        assertEquals(mPersonResult.getMessage(), "ERROR: Unauthorized");
        assertNull(mPersonResult.getPerson());
        assertNull(mPersonResult.getPersons());

        //Try requesting persons for a user that doesn't exist
        personToken = new AuthToken(UUID.randomUUID().toString(), "Jose");
        mPersonResult = mServerProxy.getPersons(personToken);
        assertEquals(mPersonResult.getMessage(), "ERROR: Unauthorized");
        assertNull(mPersonResult.getPerson());
        assertNull(mPersonResult.getPersons());
    }

    @Test
    public void test5GetEvents() {
        //POSITIVE TEST
        AuthToken eventToken = currentUserToken;

        mEventResult = mServerProxy.getEvents(eventToken);

        assertNull(mEventResult.getMessage());
        assertNull(mEventResult.getEvent());
        assertNotNull(mEventResult.getEvents());

        //NEGATIVE TESTS

        //Try using an invalid AuthToken to request an event
        eventToken = new AuthToken(UUID.randomUUID().toString(), "");
        mEventResult = mServerProxy.getEvents(eventToken);
        assertEquals(mEventResult.getMessage(), "ERROR: Unauthorized");
        assertNull(mEventResult.getEvent());
        assertNull(mEventResult.getEvents());

        //Try requesting events for a user that doesn't exist
        eventToken = new AuthToken(UUID.randomUUID().toString(), "Jose");
        mEventResult = mServerProxy.getEvents(eventToken);
        assertEquals(mEventResult.getMessage(), "ERROR: Unauthorized");
        assertNull(mEventResult.getEvent());
        assertNull(mEventResult.getEvents());
    }
}