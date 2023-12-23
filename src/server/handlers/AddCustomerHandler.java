package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.customers.CustomerDAO;
import models.Address;
import models.Customer;
import models.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;

/**
 * This class is responsible for handling POST requests to the /customers/add endpoint.
 * It adds a customer to the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class AddCustomerHandler implements HttpHandler {
    User admin = new User("admin", "admin");
    CustomerDAO customerDAO;

    /**
     * Constructor
     * @param customerDAO CustomerDAO object to handle database operations for the Customer class
     */
    public AddCustomerHandler(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    /**
     * This overridden method handles requests (POST) to the /customers/add endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Get POST request body

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
            String businessName = split[0].split("=")[1];
            String addressLine1 = split[1].split("=")[1];
            String addressLine2 = split[2].split("=")[1];
            String addressLine3 = split[3].split("=")[1];
            String postCode = split[4].split("=")[1];
            String country = split[5].split("=")[1];
            String telephone = split[6].split("=")[1];


            // verify user is admin
            String cookie = exchange.getRequestHeaders().getFirst("Cookie");
            String[] tokenSplit = cookie.split("=");
            String token = tokenSplit[1];
            var decodedTokenBase64 = Base64.getDecoder().decode(token);
            String decodedToken = new String(decodedTokenBase64);
            String[] split2 = decodedToken.split(":");
            String username = split2[0];
            String password = split2[1];
            if (!username.equals(admin.username) || !password.equals(admin.password)) {
                exchange.getResponseHeaders().add("Location", "/admin");
                exchange.sendResponseHeaders(302, 0);
                return;
            }


            if (businessName != null && addressLine1 != null && addressLine2 != null &&
                    addressLine3 != null && postCode != null && country != null && telephone != null) {
                Address address = new Address(addressLine1, addressLine2, addressLine3, postCode, country);
                Customer customer = new Customer(address, telephone, businessName);
                this.customerDAO.addCustomer(customer);
                exchange.getResponseHeaders().add("Location", "/customers");
                exchange.sendResponseHeaders(302, 0);
            }
            else {
                // Redirect to edit page
                exchange.getResponseHeaders().add("Location", "/customers/new");
                exchange.sendResponseHeaders(302, 0);
            }
        }
    }
}
