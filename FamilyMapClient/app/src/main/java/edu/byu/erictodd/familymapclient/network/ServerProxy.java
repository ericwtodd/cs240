package edu.byu.erictodd.familymapclient.network;


import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import edu.byu.erictodd.familymapclient.model.AuthToken;
import edu.byu.erictodd.familymapclient.model.EventResult;
import edu.byu.erictodd.familymapclient.model.Model;
import edu.byu.erictodd.familymapclient.model.PersonResult;
import edu.byu.erictodd.familymapclient.model.UserLoginRequest;
import edu.byu.erictodd.familymapclient.model.UserRegisterRequest;
import edu.byu.erictodd.familymapclient.model.UserResult;

/**
 * This class allows the Android client to call various web API operations
 * of the Family Map Server to access the database on the client side
 */
public class ServerProxy {

    //Singleton Accessor Methods, Member Variables

    /**
     * The hostname of the machine where the server is running (usually 10.0.2.2)
     */
    private static String hostname;
    /**
     * The portNumber that the server is accepting (usually 8080)
     */
    private static String portNumber;
    /**
     * The only ServerProxy object that will exist and let the client interact with the server
     */
    private static ServerProxy singleInstance;
    /**
     * The current user's auth token
     */
    private AuthToken currentUserToken;

    //Instantiates the ServerProxy Singleton
    static {
        singleInstance = new ServerProxy();
    }

    /**
     * Allows other classes to have access to the singleton ServerProxy class
     *
     * @return returns a pointer to the only instance of the ServerProxy class
     */
    public static ServerProxy getInstance() {
        return singleInstance;
    }

    /**
     * Required default constructor
     */
    private ServerProxy() {
    }

    //Getters and Setters

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        ServerProxy.hostname = hostname;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        ServerProxy.portNumber = portNumber;
    }

    public AuthToken getCurrentUserToken() {
        return currentUserToken;
    }

    public void setCurrentUserToken(AuthToken currentUserToken) {
        this.currentUserToken = currentUserToken;
    }


    //Functions calling Server API Methods

    /**
     * The login method calls the server's "/user/login" operation to retrieve
     * either confirmation of a login & the current User's auth token,
     * or an error message explaining why the login failed.
     *
     * @param loginRequest the Login Request from the client login Fragment
     * @return the result of the the login -
     * if successful the result contains the username, AuthToken & PersonID of the currentUser
     * if unsuccessful, the result contains an error message to be displayed in a Toast
     */
    public UserResult login(UserLoginRequest loginRequest) {
        UserResult userResult = new UserResult();
        Gson gson = new Gson();
        //Since we may be logging in a new user, we need to reset the local data cache
        Model.getInstance().reset();
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + hostname + ":" + portNumber + "/user/login");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Specify that we are sending an HTTP POST request
            http.setRequestMethod("POST");
            // Indicate that this request will contain an HTTP request body
            http.setDoOutput(true);    // There is a request body

            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");

            // Connect to the server and send the HTTP request
            http.connect();

            // This is the JSON string we will send in the HTTP request body
            String reqData = gson.toJson(loginRequest);

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();
            // Write the JSON data to the request body
            writeString(reqData, reqBody);
            // Close the request body output stream, indicating that the
            // request is complete
            reqBody.close();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // The HTTP response status code indicates success, so
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                Reader reader = new InputStreamReader(respBody);
                //Turn the JSON String into a UserResult object using GSON
                userResult = gson.fromJson(reader, UserResult.class);
                respBody.close();
                reader.close();
                //Save the User's AuthToken to the local cache:Model
                setCurrentUserToken(
                        new AuthToken(userResult.getAuthToken(), userResult.getUsername()));
                //Set the current User's personID to identify him later.
                Model.getInstance().getUser().setPersonID(userResult.getPersonID());
            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
//                System.out.println("ERROR: " + http.getResponseMessage());
                //Return the message in the userResult
                userResult.setMessage("ERROR: " + http.getResponseMessage());
                userResult.setAuthToken(null);
                userResult.setPersonID(null);
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            userResult.setMessage(e.getMessage());
            userResult.setPersonID(null);
            userResult.setAuthToken(null);
            userResult.setUsername(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            userResult.setMessage(ex.getMessage());
            userResult.setPersonID(null);
            userResult.setAuthToken(null);
            userResult.setUsername(null);
        }
        return userResult;
    }

    /**
     * The register method calls the server's "/user/register" operation to retrieve
     * either confirmation of a successful registry & the current User's auth token
     * or an error message explaining why the registry failed.
     *
     * @param registerRequest the Register Request from the client login fragment
     * @return the result of registering -
     * if successful the result contains the username, AuthToken & PersonID of the currentUser
     * if unsuccessful, the result contains an error message to be displayed in a Toast
     */
    public UserResult register(UserRegisterRequest registerRequest) {
        UserResult userResult = new UserResult();
        Gson gson = new Gson();
        //Since we may be logging in a new user, we need to reset the local data cache
        Model.getInstance().reset();
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + hostname + ":" + portNumber + "/user/register");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Specify that we are sending an HTTP POST request
            http.setRequestMethod("POST");
            // Indicate that this request will contain an HTTP request body
            http.setDoOutput(true);    // There is a request body

            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");

            // Connect to the server and send the HTTP request
            http.connect();

            // This is the JSON string we will send in the HTTP request body
            String reqData = gson.toJson(registerRequest);

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();
            // Write the JSON data to the request body
            writeString(reqData, reqBody);
            // Close the request body output stream, indicating that the
            // request is complete
            reqBody.close();

            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // The HTTP response status code indicates success, so
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                Reader reader = new InputStreamReader(respBody);
                //Turn the JSON String into a PersonResult object using GSON
                userResult = gson.fromJson(reader, UserResult.class);
                respBody.close();
                reader.close();
                //Save the User's AuthToken to the local cache:Model
                setCurrentUserToken(
                        new AuthToken(userResult.getAuthToken(), userResult.getUsername()));
                //Set the current User's personID to identify them later.
                Model.getInstance().getUser().setPersonID(userResult.getPersonID());
            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
//                System.out.println("ERROR: " + http.getResponseMessage());
                userResult.setMessage("ERROR: " + http.getResponseMessage());
                userResult.setAuthToken(null);
                userResult.setPersonID(null);
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            userResult.setMessage(e.getMessage());
            userResult.setPersonID(null);
            userResult.setAuthToken(null);
            userResult.setUsername(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            userResult.setMessage(ex.getMessage());
            userResult.setPersonID(null);
            userResult.setAuthToken(null);
            userResult.setUsername(null);
        }
        return userResult;
    }

    /**
     * The getPersons method calls the server's "/person" operation to retrieve all the
     * Persons associated with the current User.
     *
     * @param currentUserToken the current User's auth token
     * @return if successful, a PersonResult containing a list of
     * all the Persons associated with the current User
     * if unsuccessful, a PersonResult containing an error message
     */
    public PersonResult getPersons(AuthToken currentUserToken) {
        Gson gson = new Gson();
        PersonResult personResult = new PersonResult();
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + hostname + ":" + portNumber + "/person/");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");
            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(false);

            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", currentUserToken.getAuthToken());

            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");

            // Connect to the server and send the HTTP request
            http.connect();
            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                Reader reader = new InputStreamReader(respBody);
                //Turn the JSON String into a PersonResult object using GSON
                personResult = gson.fromJson(reader, PersonResult.class);
                respBody.close();
                reader.close();
            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
//                System.out.println("ERROR: " + http.getResponseMessage());
                personResult.setMessage("ERROR: " + http.getResponseMessage());
                personResult.setPerson(null);
                personResult.setPersons(null);
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            personResult.setMessage(e.getMessage());
            personResult.setPerson(null);
            personResult.setPersons(null);
        } catch (Exception e) {
            e.printStackTrace();
            personResult.setMessage(e.getMessage());
            personResult.setPerson(null);
            personResult.setPersons(null);
        }
        return personResult;
    }

    /**
     * The getEvents method calls the server's "/event" operation to retrieve all the
     * Events associated with the current User & their family.
     *
     * @param currentUserToken the current User's auth token
     * @return if successful, an EventResult containing a list of
     * all the Events associated with the current User & their family
     * if unsuccessful, an EventResult containing an error message
     */
    public EventResult getEvents(AuthToken currentUserToken) {
        Gson gson = new Gson();
        EventResult eventResult = new EventResult();
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + hostname + ":" + portNumber + "/event/");

            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");
            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(false);

            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", currentUserToken.getAuthToken());

            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");

            // Connect to the server and send the HTTP request
            http.connect();
            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                InputStream respBody = http.getInputStream();
                // Extract JSON data from the HTTP response body
                Reader reader = new InputStreamReader(respBody);
                //Turn the JSON String into a PersonResult object using GSON
                eventResult = gson.fromJson(reader, EventResult.class);
                // Display the JSON data returned from the server
                respBody.close();
                reader.close();

            } else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
//                System.out.println("ERROR: " + http.getResponseMessage());
                eventResult.setMessage("ERROR: " + http.getResponseMessage());
                eventResult.setEvent(null);
                eventResult.setEvents(null);
            }
        } catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            eventResult.setMessage(e.getMessage());
            eventResult.setEvent(null);
            eventResult.setEvents(null);
        } catch (Exception e) {
            e.printStackTrace();
            eventResult.setMessage(e.getMessage());
            eventResult.setEvent(null);
            eventResult.setEvents(null);
        }
        return eventResult;
    }


    //JSON Helper Function

    /**
     * The writeString method writes a String to an OutputStream.
     */
    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}