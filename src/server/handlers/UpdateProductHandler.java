package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.products.ProductDAO;
import models.ProductToUpdate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;

/**
 * This class is responsible for handling POST requests to the /products/update endpoint.
 * It updates a product in the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class UpdateProductHandler implements HttpHandler {
    ProductDAO productDAO;

    /**
     * Constructor
     * @param productDAO ProductDAO object to handle database operations for the Product class
     */
    public UpdateProductHandler(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
    /**
     * This overridden method handles requests (POST) to the /products/update endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if("POST".equals(exchange.getRequestMethod())) {
            if (!VerifyUserIsAdmin(exchange)) {
                exchange.getResponseHeaders().add("Location", "/admin");
                exchange.sendResponseHeaders(302, 0);
                return;
            }

            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            String[] split = URLDecoder.decode(body, StandardCharsets.UTF_8).split("&");
            var idArray = split[0].split("=");
            String idStr = idArray.length > 1 ? idArray[1] : "";
            var descriptionArray = split[1].split("=");
            String description = descriptionArray.length > 1 ? descriptionArray[1] : "";
            var categoryArray = split[2].split("=");
            String category = categoryArray.length > 1 ? categoryArray[1] : "";
            var priceArray = split[3].split("=");
            String priceStr = priceArray.length > 1 ? priceArray[1] : "";
            var expiryDateArray = split[4].split("=");
            Date expiryDate = expiryDateArray.length > 1 ? Date.valueOf(expiryDateArray[1]) : null;

            if (!idStr.isBlank() && !description.isBlank()
                    && !category.isBlank() && !priceStr.isBlank() && expiryDate != null) {
                int id = Integer.parseInt(idStr);
                int price = Integer.parseInt(priceStr);
                // Update product
                var oldProduct = productDAO.findProduct(id);
                if (oldProduct == null) {
                    exchange.sendResponseHeaders(404, 0);
                    return;
                }
                productDAO.updateProduct(new ProductToUpdate(description, category, price, expiryDate),
                        id);
                // Redirect to products page
                exchange.getResponseHeaders().add("Location", "/products");
                exchange.sendResponseHeaders(302, 0);
            }
            else {
                // Redirect to edit page
                List<String> errors = new ArrayList<>();
                if (idStr.isBlank()) {
                    errors.add("id");
                }
                if (description.isBlank()) {
                    errors.add("Description is required");
                }
                if (category.isBlank()) {
                    errors.add("Category is required");
                }
                if (priceStr.isBlank()) {
                    errors.add("Price is required");
                }
                if (expiryDate == null) {
                    errors.add("Expiry date is required");
                }
                var query = "errorMessage=" + String.join("#", errors);
                var encodedQuery = Base64.getEncoder().encodeToString(query.getBytes());
                exchange.getResponseHeaders().add("Location", "/products/edit?id=" + idStr + "&errorMessage=" + encodedQuery);
                exchange.sendResponseHeaders(302, 0);
            }
        }
    }
}
