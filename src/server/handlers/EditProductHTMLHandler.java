package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.products.ProductDAO;
import models.Product;

import java.io.IOException;
import java.io.OutputStream;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;
import static server.helpers.HtmlHelper.getBytesFromInputStream;

/**
 * This class is responsible for handling requests to the /products/edit endpoint.
 * It returns the page for editing a product in the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class EditProductHTMLHandler implements HttpHandler {
    String fileName;
    ProductDAO productDAO;

    /**
     * Constructor
     * @param productDAO ProductDAO object to handle database operations for the Product class
     */
    public EditProductHTMLHandler(ProductDAO productDAO) {
        this.productDAO = productDAO;
        this.fileName = "static/edit-product.html";
    }

    /**
     * This overridden method handles requests (GET) to the /products/edit endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (var in = getClass().getResourceAsStream("/" + fileName)) {
            if(!VerifyUserIsAdmin(exchange)) {
                // redirect to admin page
                exchange.getResponseHeaders().add("Location", "/admin");
                exchange.sendResponseHeaders(302, 0);
                return;
            }
            if (in == null) {
                System.out.println("File not found");
                exchange.sendResponseHeaders(404, 0);
            } else {
                System.out.println("Edit product page html file found");
                String idStr = exchange.getRequestURI().toString().split("/")[3];
                int productId = Integer.parseInt(idStr);
                Product product = productDAO.findProduct(productId);
                if (product == null) {
                    exchange.sendResponseHeaders(404, 0);
                    return;
                }
                byte[] responseBytes = getBytesFromInputStream(in);
                // byte array to string
                String response = new String(responseBytes);
                response = response.replace("{{id}}", String.valueOf(product.getId()));
                response = response.replace("{{description}}", product.getDescription());
                response = response.replace("{{category}}", product.getCategory());
                response = response.replace("{{price}}", String.valueOf(product.getPrice()));
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