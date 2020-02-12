package server.event;

import api.model.AuthToken;
import api.result.EventResult;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.dataaccess.AuthService;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;


public class EventHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EventResult eventResult = new EventResult();
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
                        AuthToken eventToken = new AuthToken(authToken, username);

                        String urlPath = exchange.getRequestURI().getPath();
                        System.out.println(urlPath);

                        EventService eventService = new EventService();
                        //Check if the User wants all events, or just a single event.
                        if (urlPath.equals("/event/")) {
                            //Call the Service
                            eventResult = eventService.getEvents(eventToken);
                            //Convert result into JSON and pass it to the Exchange
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            gson.toJson(eventResult, writer);
                            writer.close();
                            success = true;
                        }
                        if (urlPath.contains("/event/") && urlPath.length() > ("/event/").length()) {
                            //Extract the eventID from the urlPath
                            String eventID = urlPath.replace("/event/", "");
                            //Call the Service
                            eventResult = eventService.getEvent(eventToken, eventID);
                            //Convert result into JSON and pass it to the Exchange
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            gson.toJson(eventResult, writer);
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
            eventResult.setMessage(ex.getMessage());
            eventResult.setEvent(null);
            eventResult.setEvents(null);

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);
            //Add error Message from the exception that was thrown to the response body
            gson.toJson(eventResult, writer);
            writer.close();
        } catch (Exception ex) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            //Look at where the error came from
            ex.printStackTrace();
            //Flush & close the response body
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            writer.close();
        } finally {
            //Flushing and closing writer/OutputStream
            if (exchange != null) {
                exchange.getResponseBody().flush();
                exchange.getResponseBody().close();
                exchange.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}