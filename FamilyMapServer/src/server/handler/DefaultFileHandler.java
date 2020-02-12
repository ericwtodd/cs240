package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.*;


public class DefaultFileHandler implements HttpHandler {

    //File path to web directory in the server project
    // C:\Users\Eric Todd\IdeaProjects\FamilyMapServer\web
    private final static String FILE_ROOT_DIR = "C:\\Users\\Eric Todd\\IdeaProjects\\FamilyMapServer\\web\\";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
//              1. Retrieve the request URL from the HttpExchange
//		        2. Translate the request URL path to a physical file path on your server
//		        3. Open the requested file, and return its contents in the HTTP
//			    response body
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                String urlPath = exchange.getRequestURI().getPath();
                String path = null;
//                System.out.println("URLPath: " + urlPath);
                //If they didn't specify a folder, or are starting, return the homepage
                if (urlPath.equals("/") || urlPath.equals("/index.html")) {
                    //Get the home page
                    path = FILE_ROOT_DIR + "index.html";
//                    System.out.println(path);
                } else {
                    path = FILE_ROOT_DIR + urlPath;
                }

                //Check that the path to the file actually exists
                //If it doesn't, serve the custom 404 page.
                if (!(new File(path).exists())) {
                    System.out.println("DNE");
                    //If the File isn't found, then we redirect to the Custom 404 page
                    Path filePath = FileSystems.getDefault().getPath("C:\\Users\\Eric Todd\\IdeaProjects\\FamilyMapServer\\web\\HTML\\404.html");
                    //Send 404 HTTP Headers
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                    Files.copy(filePath, exchange.getResponseBody());
                }

                OutputStream responseBody = exchange.getResponseBody();
                Path filePath = FileSystems.getDefault().getPath(path);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                Files.copy(filePath, responseBody);

                responseBody.close();
                success = true;
            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (Exception ex) {
            // Some kind of internal error has occurred inside the server (not the
            // client's fault), so we return an "internal server error" status code
            // to the client.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            // Since the server is unable to complete the request, the client will
            // not receive a file, so we close the response body output stream,
            // indicating that the response is complete.
            exchange.getResponseBody().close();
            // Display/log the stack trace
            ex.printStackTrace();
        } finally {
            //Flushing and closing writer/OutputStream
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
        }
    }
}
