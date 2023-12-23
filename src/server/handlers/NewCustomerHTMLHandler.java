package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;
import static server.helpers.HtmlHelper.getBytesFromInputStream;

/**
 * This class is responsible for handling requests to the /new-customer endpoint.
 * It returns the page for adding a new customer to the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class NewCustomerHTMLHandler implements HttpHandler {
    String fileName;

    public NewCustomerHTMLHandler() {
        this.fileName = "static/new-customer.html";
    }

    /**
     * This overridden method handles requests (GET) to the /new-customer endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (var in = getClass().getResourceAsStream("/" + fileName)) {
            if (!VerifyUserIsAdmin(exchange)) {
                exchange.getResponseHeaders().add("Location", "/admin");
                exchange.sendResponseHeaders(302, 0);
                return;
            }
            if (in == null) {
                System.out.println("File not found");
                exchange.sendResponseHeaders(404, 0);
            } else {
                // verify user is admin
                if (!VerifyUserIsAdmin(exchange)) {
                    exchange.getResponseHeaders().add("Location", "/admin");
                    exchange.sendResponseHeaders(302, 0);
                    return;
                }
                System.out.println("New customer page html file found");
                byte[] responseBytes = getBytesFromInputStream(in);
                // byte array to string
                String response = new String(responseBytes);
                // replace {{login}} with login button if user is not admin
                response = response.replace("{{login}}", "<li class=\"nav-item\"> <a href=\"/logout\" class=\"nav-link btn btn-outline-danger\">Logout</a> </li>");
                responseBytes = response.getBytes();
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseBytes);
                output.close();
            }
        }
    }
}