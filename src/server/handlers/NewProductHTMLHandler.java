package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;
import static server.helpers.HtmlHelper.getBytesFromInputStream;

/**
 * This class is responsible for handling requests to the /products/new endpoint.
 * It returns the page for adding a new product to the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class NewProductHTMLHandler implements HttpHandler {
    String fileName;

    public NewProductHTMLHandler() {
        this.fileName = "static/new-product.html";
    }

    /**
     * This overridden method handles requests (GET) to the /products/new endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (var in = getClass().getResourceAsStream("/" + fileName)) {
            if (!VerifyUserIsAdmin(exchange)) {
                // redirect to admin page
                exchange.getResponseHeaders().add("Location", "/admin");
                exchange.sendResponseHeaders(302, 0);
                return;
            }
                if (in == null) {
                    System.out.println("File not found");
                    exchange.sendResponseHeaders(404, 0);
                } else {
                    System.out.println("New product page html file found");
                    byte[] responseBytes = getBytesFromInputStream(in);
                    // byte array to string
                    String response = new String(responseBytes);
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
