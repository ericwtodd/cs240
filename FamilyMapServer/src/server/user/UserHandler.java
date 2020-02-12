package server.user;

import api.request.UserLoginRequest;
import api.request.UserRegisterRequest;
import api.result.UserResult;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;

public class UserHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        UserResult userResult = new UserResult();
        Gson gson = new Gson();
        Writer writer = new OutputStreamWriter(exchange.getResponseBody());

        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                String urlPath = exchange.getRequestURI().getPath();
                System.out.println("URLPath: " + urlPath);

                if (urlPath.equals("/user/login")) {
                    //Create a new Request and put the request body into the request
                    Reader reader = new InputStreamReader(exchange.getRequestBody());
                    UserLoginRequest loginRequest = gson.fromJson(reader, UserLoginRequest.class);

                    //Call the service
                    UserLoginService loginService = new UserLoginService();
                    userResult = loginService.login(loginRequest);

                    //Convert result into JSON and pass it to the Exchange
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    gson.toJson(userResult, writer);

                    success = true;
                    reader.close();
                    writer.close();
                } else if (urlPath.equals("/user/register")) {
                    //Create a new RegisterRequest and put the request body into the request
                    Reader reader = new InputStreamReader(exchange.getRequestBody());
                    UserRegisterRequest registerRequest = gson.fromJson(reader, UserRegisterRequest.class);

                    //Call the registerService
                    UserRegisterService registerService = new UserRegisterService();
                    userResult = registerService.register(registerRequest);

                    //Convert result into JSON and pass it to the Exchange
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    gson.toJson(userResult, writer);
                    writer.close();
                    success = true;

                } else {
                    success = false;
                }
            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (Exception ex) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            //Add error Message from the exception that was thrown
            userResult.setMessage(ex.getMessage());
            userResult.setUsername(null);
            userResult.setPersonID(null);
            userResult.setAuthToken(null);

            //Send Result to the Response Body
            gson.toJson(userResult, writer);
            ex.printStackTrace();
            exchange.getResponseBody().close();

        } finally {
            //Close RequestBody
            exchange.getRequestBody().close();

            //Flushing and closing writer/OutputStream
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            exchange.close();
            writer.close();
        }
    }
}
