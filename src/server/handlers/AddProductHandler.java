package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.products.ProductDAO;
import models.Product;
import models.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;

/**
 * This class is responsible for handling POST requests to the /products/add endpoint.
 * It adds a product to the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class AddProductHandler implements HttpHandler {
    User admin = new User("admin", "admin");
    ProductDAO productDAO;

    /**
     * Constructor
     * @param productDAO ProductDAO object to handle database operations for the Product class
     */
    public AddProductHandler(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    /**
     * This overridden method handles requests (POST) to the /products/add endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            if (!VerifyUserIsAdmin(exchange)) {
                exchange.getResponseHeaders().add("Location", "/admin");
                exchange.sendResponseHeaders(302, 0);
                return;
            }

            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Get username and password from body
            body = URLDecoder.decode(body, StandardCharsets.UTF_8);
            String[] split = body.split("&");
            String description = split[0].split("=")[1];
            String priceStr = split[1].split("=")[1];
            String category = split[2].split("=")[1];

            if(description != null && category != null && priceStr != null) {
                int price = Integer.parseInt(priceStr);
                productDAO.addProduct(new Product(description, category, price));
                // Redirect to products page
                exchange.getResponseHeaders().add("Location", "/products");
                exchange.sendResponseHeaders(302, 0);
            }
            else {
                // Redirect to add page
                exchange.getResponseHeaders().add("Location", "/products/new");
                exchange.sendResponseHeaders(302, 0);
            }
        }
    }
}
