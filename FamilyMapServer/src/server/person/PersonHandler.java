package server.person;

import api.model.AuthToken;
import api.result.PersonResult;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.dataaccess.AuthService;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;


public class PersonHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        PersonResult personResult = new PersonResult();
        Gson gson = new Gson();
        Writer writer = new OutputStreamWriter(exchange.getResponseBody());
        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                // Get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();
                // Check to see if an "Authorization" header is present
                if (reqHeaders.containsKey("Authorization")) {
                    // Extract the auth token from the "Authorization" header
                    String authToken = reqHeaders.getFirst("Authorization");
                    if (authToken.isEmpty()) {
                        throw new IllegalArgumentException("Invalid Authorization Token");
                    }
                    AuthService authorizer = new AuthService();
                    if (authorizer.authorizeUser(authToken) != null) //The token exists.
                    {
                        String username = authorizer.authorizeUser(authToken);
                        AuthToken personToken = new AuthToken(authToken, username);

                        String urlPath = exchange.getRequestURI().getPath();
                        System.out.println(urlPath);

                        PersonService personService = new PersonService();

                        //Check if the User wants all associated persons, or just a single person.
                        if (urlPath.equals("/person/")) {
                            //Call the Service
                            personResult = personService.getFamily(personToken);

                            //Convert result into JSON and pass it to the Exchange
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            gson.toJson(personResult, writer);
                            writer.close();
                            success = true;
                        } else if (urlPath.contains("/person/") && urlPath.length() > ("/person/").length()) {
                            //Extract the personID from the urlPath
                            String personID = urlPath.replace("/person/", "");
                            //Call the Service
                            personResult = personService.getPerson(personToken, personID);

                            //Convert result into JSON and pass it to the Exchange
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            gson.toJson(personResult, writer);
                            writer.close();
                            success = true;
                        } else {
                            success = false;
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid Authorization Token");
                    }
                }
            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (IllegalArgumentException ex) {
            //Set the PersonResult to accept the message we want
            personResult.setMessage(ex.getMessage());
            personResult.setPerson(null);
            personResult.setPersons(null);

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);
            //Add error Message from the exception that was thrown to the response body
            gson.toJson(personResult, writer);
            writer.close();
        } catch (Exception ex) {
            // Some kind of internal error has occurred inside the server (not the
            // client's fault), so we return an "internal server error" status code
            // to the client.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            //Add error Message from the exception that was thrown
            gson.toJson(personResult, writer);
            ex.printStackTrace();

        } finally {
            //Flushing and closing writer/OutputStream
//            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
            if (writer != null) {
                writer.close();
            }
        }
    }
}
