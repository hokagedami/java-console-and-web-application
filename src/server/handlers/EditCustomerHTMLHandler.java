package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.customers.CustomerDAO;
import models.Customer;

import java.io.IOException;
import java.io.OutputStream;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;
import static server.helpers.HtmlHelper.getBytesFromInputStream;

/**
 * This class is responsible for handling requests to the /edit-customer endpoint.
 * It returns the page for editing a customer in the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class EditCustomerHTMLHandler implements HttpHandler {
    String fileName;
    CustomerDAO customerDAO;

    /**
     * Constructor
     * @param customerDAO CustomerDAO object to handle database operations for the Customer class
     */
    public EditCustomerHTMLHandler(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
        this.fileName = "static/edit-customer.html";
    }

    /**
     * This overridden method handles requests (GET) to the /edit-customer endpoint
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
                System.out.println("Edit customer page html file found");
                String idStr = exchange.getRequestURI().toString().split("/")[3];
                int productId = Integer.parseInt(idStr);
                Customer customer = customerDAO.findCustomerByID(productId);
                if (customer == null) {
                    exchange.sendResponseHeaders(404, 0);
                    return;
                }
                byte[] responseBytes = getBytesFromInputStream(in);
                String response = new String(responseBytes);

                response = response.replace("{{id}}", String.valueOf(customer.getId()));
                response = response.replace("{{businessName}}", customer.getBusinessName());
                response = response.replace("{{addressLine1}}", customer.getAddressObject().getAddressLine1());
                response = response.replace("{{addressLine2}}", customer.getAddressObject().getAddressLine2());
                response = response.replace("{{addressLine3}}", customer.getAddressObject().getAddressLine3());
                response = response.replace("{{postCode}}", customer.getAddressObject().getPostCode());
                response = response.replace("{{country}}", customer.getAddressObject().getCountry());
                response = response.replace("{{telephone}}", customer.getTelephone());

                response = response.replace("{{login}}",
                        "<li class=\"nav-item\"> <a href=\"/logout\" class=\"nav-link btn btn-outline-danger\">Logout</a> </li>");
                responseBytes = response.getBytes();
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseBytes);
                output.close();
            }
        }
    }
}