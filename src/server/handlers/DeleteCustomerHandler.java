package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.customers.CustomerDAO;

import java.io.IOException;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;

/**
 * This class is responsible for handling POST requests to the /customers/del endpoint.
 * It deletes a customer from the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class DeleteCustomerHandler implements HttpHandler {

    CustomerDAO customerDAO;

    /**
     * Constructor
     * @param customerDAO CustomerDAO object to handle database operations for the Customer class
     */
    public DeleteCustomerHandler(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    /**
     * This overridden method handles requests (POST) to the /customers/del endpoint
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
        // Verify username and password
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            customerDAO.deleteCustomer(id);
        }
        exchange.getResponseHeaders().add("Location", "/customers");
        exchange.sendResponseHeaders(302, 0);
    }
}
