package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.products.ProductDAO;
import models.ProductToUpdate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
            String idStr = split[0].split("=")[1];
            String description = split[1].split("=")[1];
            String category = split[2].split("=")[1];
            String priceStr = split[3].split("=")[1];

            if (idStr != null && description != null && category != null && priceStr != null) {
                int id = Integer.parseInt(idStr);
                int price = Integer.parseInt(priceStr);
                // Update product
                var oldProduct = productDAO.findProduct(id);
                if (oldProduct == null) {
                    exchange.sendResponseHeaders(404, 0);
                    return;
                }
                productDAO.updateProduct(new ProductToUpdate(description, category, price),
                        id);
                // Redirect to products page
                exchange.getResponseHeaders().add("Location", "/products");
                exchange.sendResponseHeaders(302, 0);
            }
            else {
                // Redirect to edit page
                exchange.getResponseHeaders().add("Location", "/products/edit/" + idStr);
                exchange.sendResponseHeaders(302, 0);
            }
        }
    }
}
