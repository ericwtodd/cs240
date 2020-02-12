package server.fill;

import api.request.FillRequest;
import api.result.MessageResult;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;

public class FillHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        MessageResult messageResult = new MessageResult();
        Gson gson = new Gson();
        Writer writer = new OutputStreamWriter(exchange.getResponseBody());
        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                FillService fillService = new FillService();

                String urlPath = exchange.getRequestURI().getPath();
                System.out.println("URLPath: " + urlPath);
                String username;
                int generations = 4;

                //Extract the Username & Generations, and call the FillService
                if (urlPath.contains("/fill/")) {
                    FillRequest fillRequest;
                    urlPath = urlPath.replace("/fill/", "");
                    if (urlPath.contains("/")) {
                        String[] fillRequestParts = urlPath.split("/");
                        username = fillRequestParts[0];
                        if (fillRequestParts.length > 1) {
                            generations = Integer.valueOf(fillRequestParts[1]);
                        }
                        //Call the Service and store the message result.
                        fillRequest = new FillRequest(username, generations);
                    } else {
                        username = urlPath;
                        fillRequest = new FillRequest(username);
                    }
                    //Call the Service and store the message result.
                    messageResult = fillService.fill(fillRequest);

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    gson.toJson(messageResult, writer);
                    writer.close();
                    success = true;
                }
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
            messageResult.setMessage(ex.getMessage());
            gson.toJson(messageResult, writer);
            ex.printStackTrace();

        } finally {
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
            if (writer != null) {
                writer.close();
            }
        }
    }
}
