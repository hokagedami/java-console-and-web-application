package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.customers.CustomerDAO;
import models.Address;
import models.CustomerToUpdate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;

/**
 * This class is responsible for handling POST requests to the /customers/update endpoint.
 * It updates a customer in the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class UpdateCustomerHandler implements HttpHandler{
    CustomerDAO customerDAO;

    /**
     * Constructor
     * @param customerDAO CustomerDAO object to handle database operations for the Customer class
     */
    public UpdateCustomerHandler(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    /**
     * This overridden method handles requests (POST) to the /customers/update endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if("POST".equals(exchange.getRequestMethod())) {
            // Get POST request body
            if (!VerifyUserIsAdmin(exchange)) {
                exchange.getResponseHeaders().add("Location", "/admin");
                exchange.sendResponseHeaders(302, 0);
                return;
            }
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            String[] split = URLDecoder.decode(body, StandardCharsets.UTF_8).split("&");
            String idStr = split[0].split("=")[1];
            String businessName = split[1].split("=")[1];
            String addressLine1 = split[2].split("=")[1];
            String addressLine2 = split[3].split("=")[1];
            String addressLine3 = split[4].split("=")[1];
            String postCode = split[5].split("=")[1];
            String country = split[6].split("=")[1];
            String telephone = split[7].split("=")[1];

            if (idStr != null && businessName != null && addressLine1 != null && addressLine2 != null &&
                    addressLine3 != null && postCode != null && country != null && telephone != null) {
                int id = Integer.parseInt(idStr);
                Address address = new Address(addressLine1, addressLine2, addressLine3, postCode, country);
                CustomerToUpdate customer = new CustomerToUpdate(address, telephone, businessName);
                customerDAO.updateCustomer(customer, id);
                exchange.getResponseHeaders().add("Location", "/customers");
                exchange.sendResponseHeaders(302, 0);
            }
            else {
                // Redirect to edit page
                exchange.getResponseHeaders().add("Location", "/customers/edit/" + idStr);
                exchange.sendResponseHeaders(302, 0);
            }
        }
    }
}