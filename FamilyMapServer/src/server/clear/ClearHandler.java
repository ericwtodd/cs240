package server.clear;

import api.result.MessageResult;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

/**
 * Handles a /clear request from the Server
 */
public class ClearHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //Create the corresponding Service and Result
        ClearService clearService = new ClearService();
        MessageResult result = new MessageResult();
        //Gson object to convert to and from JSON
        Gson gson = new Gson();
        //Writer used to write to the exchange response body
        Writer writer = new OutputStreamWriter(exchange.getResponseBody());
        boolean success = false;
        System.out.println("/clear");
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                //Call the Clear Service and store its messageResult
                result = clearService.clear();
                //Send OK headers, and the message to the exchange
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                //Convert object to JSON string
                gson.toJson(result, writer);
                writer.close();
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
            //Add error Message from the exception that was thrown
            gson.toJson(result, writer);
            ex.printStackTrace();
        } finally {
            //Flushing and closing writer/OutputStream
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
            if (writer != null) {
                writer.close();
            }
        }
    }
}
