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
import java.sql.Date;
import java.util.Base64;

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
            var descriptionArray = split[0].split("=");
            String description = descriptionArray.length > 1 ? descriptionArray[1] : "";
            var priceArray = split[1].split("=");
            String priceStr = priceArray.length > 1 ? priceArray[1] : "";
            var categoryArray = split[2].split("=");
            String category = categoryArray.length > 1 ? categoryArray[1] : "";
            var expiryDateArray = split[3].split("=");
            Date expiryDate = expiryDateArray.length > 1 ? Date.valueOf(expiryDateArray[1]) : null;

            if(!description.isBlank() && !category.isBlank() && !priceStr.isBlank() && expiryDate != null) {
                int price = Integer.parseInt(priceStr);
                productDAO.addProduct(new Product(description, category, price, expiryDate));
                // Redirect to products page
                exchange.getResponseHeaders().add("Location", "/products");
                exchange.sendResponseHeaders(302, 0);
            }
            else {
                // Make request to edit page with error message
                // encode error message and request parameters
                var expiryDateStr = expiryDate != null ? expiryDate.toString() : "";
                var query = "errorMessage=" + "Please fill in all fields" +
                        "&description=" + description +
                        "&price=" + priceStr +
                        "&category=" + category +
                        "&expiryDate=" + expiryDateStr;
                var encodedQuery = Base64.getEncoder().encodeToString(query.getBytes());
                exchange.getResponseHeaders().add("Location",
                        "/product/new?errorMessage=" + encodedQuery);
                exchange.sendResponseHeaders(302, 0);
            }
        }
    }
}
