package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;
import static server.helpers.HtmlHelper.getBytesFromInputStream;

/**
 * This class is responsible for handling requests to the /new-customer endpoint.
 * It returns the page for adding a new customer to the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class NewCustomerHTMLHandler implements HttpHandler {
    String fileName;

    public NewCustomerHTMLHandler() {
        this.fileName = "static/new-customer.html";
    }

    /**
     * This overridden method handles requests (GET) to the /new-customer endpoint
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
                // verify user is admin
                if (!VerifyUserIsAdmin(exchange)) {
                    exchange.getResponseHeaders().add("Location", "/admin");
                    exchange.sendResponseHeaders(302, 0);
                    return;
                }
                System.out.println("New customer page html file found");
                byte[] responseBytes = getBytesFromInputStream(in);
                // byte array to string
                String response = new String(responseBytes);
                // get error message from query
                var encodedQuery = exchange.getRequestURI().getQuery();
                // decode query
                var encodedQueryArray = encodedQuery != null ? encodedQuery.split("=") : null;
                var query = encodedQueryArray != null && encodedQueryArray.length > 1
                        ? new String(Base64.getDecoder().decode(encodedQueryArray[1])) : null;
                var queryArray = query != null ? query.split("&") : null;
                var errorMessageArray = queryArray != null ? queryArray[0].split("=") : null;
                String errorMessage = errorMessageArray != null && errorMessageArray.length > 1
                        ? errorMessageArray[1] : null;
                var businessNameArray = queryArray != null ? queryArray[1].split("=") : null;
                String businessName = businessNameArray != null && businessNameArray.length > 1
                        ? businessNameArray[1] : "";
                var addressLine1Array = queryArray != null ? queryArray[2].split("=") : null;
                String addressLine1 = addressLine1Array != null && addressLine1Array.length > 1
                        ? addressLine1Array[1] : "";
                var addressLine2Array = queryArray != null ? queryArray[3].split("=") : null;
                String addressLine2 = addressLine2Array != null && addressLine2Array.length > 1
                        ? addressLine2Array[1] : "";
                var addressLine3Array = queryArray != null ? queryArray[4].split("=") : null;
                String addressLine3 = addressLine3Array != null && addressLine3Array.length > 1
                        ? addressLine3Array[1] : "";
                var postCodeArray = queryArray != null ? queryArray[5].split("=") : null;
                String postCode = postCodeArray != null && postCodeArray.length > 1
                        ? postCodeArray[1] : "";
                var countryArray = queryArray != null ? queryArray[6].split("=") : null;
                String country = countryArray != null && countryArray.length > 1
                        ? countryArray[1] : "";
                var telephoneArray = queryArray != null ? queryArray[7].split("=") : null;
                String telephone = telephoneArray != null && telephoneArray.length > 1
                        ? telephoneArray[1] : "";
                if (errorMessage != null) {
                    // replace {{errorMessage}} with error message div if there is one
                    var errorHtml = "<div class=\"alert alert-danger alert-dismissible fade show errorDiv\" role=\"alert\">\n" +
                            "        <span id=\"errorMessage\">"+ errorMessage +"</span>\n" +
                            "        <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\" onclick=\"closeErrorDiv()\"></button>\n" +
                            "    </div>";
                    response = response.replace("{{errorMessage}}", errorHtml);
                } else {
                    response = response.replace("{{errorMessage}}", "");
                }
                response = response.replace("{{businessName}}", businessName);
                response = response.replace("{{addressLine1}}", addressLine1);
                response = response.replace("{{addressLine2}}", addressLine2);
                response = response.replace("{{addressLine3}}", addressLine3);
                response = response.replace("{{postCode}}", postCode);
                response = country.isBlank() ? response.replace("{{country}}", "Select Country")
                        : response.replace("{{country}}", country);
                response = response.replace("{{telephone}}", telephone);
                // replace {{login}} with login button if user is not admin
                response = response.replace("{{login}}", "<li class=\"nav-item\"> <a href=\"/logout\" class=\"nav-link btn btn-danger\">Logout</a> </li>");
                responseBytes = response.getBytes();
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseBytes);
                output.close();
            }
        }
    }
}