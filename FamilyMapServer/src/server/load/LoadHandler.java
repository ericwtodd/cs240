package server.load;

import api.request.LoadRequest;
import api.result.MessageResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;

import com.google.gson.*;
import server.clear.ClearService;

public class LoadHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LoadService loadService = new LoadService();
        MessageResult messageResult = new MessageResult();
        Gson gson = new Gson();
        Writer writer = new OutputStreamWriter(exchange.getResponseBody());

        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                //Get the request body JSON and convert it into a request object
                Reader reader = new InputStreamReader(exchange.getRequestBody());
                LoadRequest loadRequest = gson.fromJson(reader, LoadRequest.class);

                //Call the LoadService with the loadRequest object
                messageResult = loadService.load(loadRequest);

                //Then convert the LoadService's result object into JSON and
                //give it to the exchange responseBody
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                gson.toJson(messageResult, writer);

                success = true;
                reader.close();
                writer.close();
            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (Exception ex) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            //Add error Message from the exception that was thrown
            messageResult.setMessage(ex.getMessage());

            //Send Result to the Response Body
            gson.toJson(messageResult, writer);
            ex.printStackTrace();
            exchange.getResponseBody().close();
        } finally {
            //Close the Request, Response Bodies and the writer.
            exchange.getRequestBody().close();
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            writer.close();
        }
    }
}
