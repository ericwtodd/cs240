package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;

import com.sun.net.httpserver.*;
import server.clear.ClearHandler;
import server.dataaccess.*;
import server.event.EventHandler;
import server.fill.FillHandler;
import server.handler.DefaultFileHandler;
import server.load.LoadHandler;
import server.person.PersonHandler;
import server.user.UserHandler;

//Port Number - Have been Using 8080

public class Server {

    private static final int MAX_WAITING_CONNECTIONS = 12;
    private HttpServer server;

    private void run(String portNumber) {
        // Since the server has no "user interface", it should display "log"
        // messages containing information about its internal activities.
        // This allows a system administrator (or you) to know what is happening
        // inside the server, which can be useful for diagnosing problems
        // that may occur.

        //CREATE TABLES
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.InitializeDatabase();
        databaseManager = null;

        System.out.println("Initializing HTTP Server");

        try {
            // Create a new HttpServer object.
            // Rather than calling "new" directly, we instead create
            // the object by calling the HttpServer.create static factory method.
            // Just like "new", this method returns a reference to the new object.
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Indicate that we are using the default "executor".
        // This line is necessary, but its function is unimportant for our purposes.
        server.setExecutor(null);

        // Log message indicating that the server is creating and installing
        // its HTTP handlers.
        // The HttpServer class listens for incoming HTTP requests.  When one
        // is received, it looks at the URL path inside the HTTP request, and
        // forwards the request to the handler for that URL path.
        System.out.println("Creating contexts");

        // Create and install the HTTP handler for the "/clear" URL path.
        // When the HttpServer receives an HTTP request containing the
        // "/clear" URL path, it will forward the request to ClearHandler
        // for processing.
        server.createContext("/clear", new ClearHandler());

        // Create and install the HTTP handler for the "/fill" URL path.
        // When the HttpServer receives an HTTP request containing the
        // "/fill" URL path, it will forward the request to FillHandler
        // for processing.
        server.createContext("/fill", new FillHandler());

        // Create and install the HTTP handler for the "/load" URL path.
        // When the HttpServer receives an HTTP request containing the
        // "/load" URL path, it will forward the request to LoadHandler
        // for processing.
        server.createContext("/load", new LoadHandler());

        // Create and install the HTTP handler for the "/user" URL path.
        // When the HttpServer receives an HTTP request containing the
        // "/user" URL path, it will forward the request to UserHandler
        // for processing. - will handle user/register & user/login
        server.createContext("/user", new UserHandler());

        // Create and install the HTTP handler for the "/event" URL path.
        // When the HttpServer receives an HTTP request containing the
        // "/event" URL path, it will forward the request to EventHandler
        // for processing.
        server.createContext("/event", new EventHandler());

        // Create and install the HTTP handler for the "/person" URL path.
        // When the HttpServer receives an HTTP request containing the
        // "/person" URL path, it will forward the request to PersonHandler
        // for processing.
        server.createContext("/person", new PersonHandler());

        // Create and install the HTTP handler for all other URL paths.
        // When the HttpServer receives an HTTP request containing a URL path
        // not handled above, it will forward the request to DefaultFileHandler
        // for processing.
        server.createContext("/", new DefaultFileHandler());

        // Log message indicating that the HttpServer is about the start accepting
        // incoming client connections.
        System.out.println("Starting server");

        // Tells the HttpServer to start accepting incoming client connections.
        // This method call will return immediately, and the "main" method
        // for the program will also complete.
        // Even though the "main" method has completed, the program will continue
        // running because the HttpServer object we created is still running
        // in the background.
        server.start();

        // Log message indicating that the server has successfully started.
        System.out.println("Server started");
    }

    public static void main(String[] args) {
        String portNumber = args[0];
        new Server().run(portNumber);
    }
}
