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
import java.util.Base64;

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

            String[] bodyArray = URLDecoder.decode(body, StandardCharsets.UTF_8).split("&");
            var idArray = bodyArray[0].split("=");
            String idStr = idArray.length > 1 ? idArray[1] : "";
            var businessNameArray = bodyArray[1].split("=");
            String businessName = businessNameArray.length > 1 ? businessNameArray[1] : "";
            var addressLine1Array = bodyArray[2].split("=");
            String addressLine1 = addressLine1Array.length > 1 ? addressLine1Array[1] : "";
            var addressLine2Array = bodyArray[3].split("=");
            String addressLine2 = addressLine2Array.length > 1 ? addressLine2Array[1] : "";
            var addressLine3Array = bodyArray[4].split("=");
            String addressLine3 = addressLine3Array.length > 1 ? addressLine3Array[1] : "";
            var postCodeArray = bodyArray[5].split("=");
            String postCode = postCodeArray.length > 1 ? postCodeArray[1] : "";
            var countryArray = bodyArray[6].split("=");
            String country = countryArray.length > 1 ? countryArray[1] : "";
            var telephoneArray = bodyArray[7].split("=");
            String telephone = telephoneArray.length > 1 ? telephoneArray[1] : "";

            if (!idStr.isBlank() && !businessName.isBlank() && !addressLine1.isBlank() && !addressLine2.isBlank() &&
                    !addressLine3.isBlank() && !postCode.isBlank() && !country.isBlank() && !telephone.isBlank()) {
                int id = Integer.parseInt(idStr);
                Address address = new Address(addressLine1, addressLine2, addressLine3, postCode, country);
                CustomerToUpdate customer = new CustomerToUpdate(address, telephone, businessName);
                customerDAO.updateCustomer(customer, id);
                exchange.getResponseHeaders().add("Location", "/customers");
                exchange.sendResponseHeaders(302, 0);
            }
            else {
                // Redirect to edit page
                var query = "Please fill in all fields!";
                var encodedQuery = Base64.getEncoder().encodeToString(query.getBytes());
                // add error message to cookie
                exchange.getResponseHeaders().add("Set-Cookie", encodedQuery);
                exchange.getResponseHeaders().add("Location", "/customers/edit/" + idStr);
                exchange.sendResponseHeaders(302, 0);
            }
        }
    }
}