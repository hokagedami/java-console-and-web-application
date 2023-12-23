package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.products.ProductDAO;

import java.io.IOException;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;


/**
 * This class is responsible for handling POST requests to the /products/del endpoint.
 * It deletes a product from the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class DeleteProductHandler implements HttpHandler {
    ProductDAO productDAO;

    /**
     * Constructor
     * @param productDAO ProductDAO object to handle database operations for the Product class
     */
    public DeleteProductHandler(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    /**
     * This overridden method handles requests (POST) to the /products/del endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!VerifyUserIsAdmin(exchange)) {
            exchange.getResponseHeaders().add("Location", "/admin");
            exchange.sendResponseHeaders(302, 0);
            return;
        }
        String idStr = exchange.getRequestURI().toString().split("/")[3];
        if(idStr != null) {
            int id = Integer.parseInt(idStr);
            productDAO.deleteProduct(id);
        }
        exchange.getResponseHeaders().add("Location", "/products");
        exchange.sendResponseHeaders(302, 0);
    }
}
