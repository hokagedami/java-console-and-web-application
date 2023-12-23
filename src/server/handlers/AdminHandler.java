package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static server.helpers.HtmlHelper.getBytesFromInputStream;

/**
 * This class is responsible for handling the admin endpoint.
 * It handles the login and logout operations.
 * It also serves the login page.
 * It implements the HttpHandler interface.
 */
public class AdminHandler implements HttpHandler {
    String fileName;
    User admin;

    /**
     * Constructor
     */
    public AdminHandler() {
        this.fileName = "static/login.html";
        this.admin = new User("admin", "admin");
    }

    /**
     * This method handles the admin endpoint.
     * It is an override of the handle method in the HttpHandler interface.
     * It handles the login and logout operations.
     * It handles GET and POST requests.
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Handle post request
        if ("POST".equals(exchange.getRequestMethod())) {

            // Get POST request body
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Get username and password from body
            String[] split = body.split("&");
            String username = split[0].split("=")[1];
            String password = split[1].split("=")[1];

            // Verify username and password
            if (username.equals(admin.username) && password.equals(admin.password)) {
                // Add token to header
                var token = username + ":" + password;
                var encodedToken = Base64.getEncoder().encodeToString(token.getBytes());
                exchange.getResponseHeaders().add("Set-Cookie", "token=" + encodedToken);
                exchange.getResponseHeaders().add("Set-Cookie", "sessionId=; Max-Age=0");
                exchange.getResponseHeaders().add("Location", "/products");
                exchange.sendResponseHeaders(302, 0);
            } else {
                exchange.getResponseHeaders().add("Location", "/admin");
                exchange.sendResponseHeaders(302, 0);
            }
        } else if ("GET".equals(exchange.getRequestMethod())) {
            try (var in = getClass().getResourceAsStream("/" + fileName)) {
                if (in == null) {
                    System.out.println("File not found");
                    exchange.sendResponseHeaders(404, 0);
                } else {
                    System.out.println("Login page html file found");
                    // get endpoint from request
                    String endpoint = exchange.getRequestURI().getPath();
                    // clear cookie if logout
                    if (endpoint.equals("/logout")) {
                        exchange.getResponseHeaders().add("Set-Cookie", "token=; Max-Age=0");
                        exchange.getResponseHeaders().add("Set-Cookie", "sessionId=; Max-Age=0");
                    }
                    byte[] responseBytes = getBytesFromInputStream(in);
                    // byte array to string
                    String response = new String(responseBytes);
                    responseBytes = response.getBytes();
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(responseBytes);
                    output.close();
                }
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}
