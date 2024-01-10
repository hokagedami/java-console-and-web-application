package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.customers.CustomerDAO;
import models.Address;
import models.Customer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;

/**
 * This class is responsible for handling POST requests to the /customers/add endpoint.
 * It adds a customer to the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class AddCustomerHandler implements HttpHandler {
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
            String[] bodyArray = body.split("&");
            var businessNameArray = bodyArray[0].split("=");
            String businessName = businessNameArray.length > 1 ? businessNameArray[1] : "";
            var addressLine1Array = bodyArray[1].split("=");
            String addressLine1 = addressLine1Array.length > 1 ? addressLine1Array[1] : "";
            var addressLine2Array = bodyArray[2].split("=");
            String addressLine2 = addressLine2Array.length > 1 ? addressLine2Array[1] : "";
            var addressLine3Array = bodyArray[3].split("=");
            String addressLine3 = addressLine3Array.length > 1 ? addressLine3Array[1] : "";
            var postCodeArray = bodyArray[4].split("=");
            String postCode = postCodeArray.length > 1 ? postCodeArray[1] : "";
            var countryArray = bodyArray[5].split("=");
            String country = countryArray.length > 1 ? countryArray[1] : "";
            var telephoneArray = bodyArray[6].split("=");
            String telephone = telephoneArray.length > 1 ? telephoneArray[1] : "";

            List<String> errors = new ArrayList<>();

            if (businessName.isBlank()) {
                errors.add("Business name is required");
            }
            if (addressLine1.isBlank()) {
                errors.add("Address line 1 is required");
            }
            if(postCode.isBlank()) {
                errors.add("Post code is required");
            }
            if (country.isBlank() || country.equals("Select Country")) {
                errors.add("Country is required");
            }
            if (telephone.isBlank()) {
                errors.add("Telephone number is required");
            }

            if(!errors.isEmpty()) {
                // Make request to edit page with error message
                // encode error message and request parameters
                var query = "errorMessage=" + String.join("#", errors) +
                        "&businessName=" + businessName +
                        "&addressLine1=" + addressLine1 +
                        "&addressLine2=" + addressLine2 +
                        "&addressLine3=" + addressLine3 +
                        "&postCode=" + postCode +
                        "&country=" + country +
                        "&telephone=" + telephone;
                var encodedQuery = Base64.getEncoder().encodeToString(query.getBytes());
                exchange.getResponseHeaders().add("Location",
                        "/customer/new?errorMessage=" + encodedQuery);
                exchange.sendResponseHeaders(302, 0);
                return;
            }
            Address address = new Address(addressLine1, addressLine2, addressLine3, country, postCode);
            Customer customer = new Customer(address, telephone, businessName);
            this.customerDAO.addCustomer(customer);
            exchange.getResponseHeaders().add("Location", "/customers");
            exchange.sendResponseHeaders(302, 0);
        }
    }
}
