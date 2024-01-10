package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;
import static server.helpers.HtmlHelper.getBytesFromInputStream;

/**
 * This class is responsible for handling requests to the /products/new endpoint.
 * It returns the page for adding a new product to the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class NewProductHTMLHandler implements HttpHandler {
    String fileName;

    public NewProductHTMLHandler() {
        this.fileName = "static/new-product.html";
    }

    /**
     * This overridden method handles requests (GET) to the /products/new endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (var in = getClass().getResourceAsStream("/" + fileName)) {
            if (!VerifyUserIsAdmin(exchange)) {
                // redirect to admin page
                exchange.getResponseHeaders().add("Location", "/admin");
                exchange.sendResponseHeaders(302, 0);
                return;
            }
                if (in == null) {
                    System.out.println("File not found");
                    exchange.sendResponseHeaders(404, 0);
                } else {
                    System.out.println("New product page html file found");
                    byte[] responseBytes = getBytesFromInputStream(in);
                    // byte array to string
                    String response = new String(responseBytes);

                    var encodedQuery = exchange.getRequestURI().getQuery();
                    // decode query
                    var encodedQueryArray = encodedQuery != null ? encodedQuery.split("=") : null;
                    var query = encodedQueryArray != null && encodedQueryArray.length > 1
                            ? new String(Base64.getDecoder().decode(encodedQueryArray[1])) : null;
                    var queryArray = query != null ? query.split("&") : null;
                    var errorMessageArray = queryArray != null ? queryArray[0].split("=") : null;
                    String errorMessage = errorMessageArray != null && errorMessageArray.length > 1
                            ? errorMessageArray[1] : null;
                    var descriptionArray = queryArray != null ? queryArray[1].split("=") : null;
                    String description = descriptionArray != null && descriptionArray.length > 1
                            ? descriptionArray[1] : "";
                    var priceArray = queryArray != null ? queryArray[2].split("=") : null;
                    String price = priceArray != null && priceArray.length > 1
                            ? priceArray[1] : "";
                    var categoryArray = queryArray != null ? queryArray[3].split("=") : null;
                    String category = categoryArray != null && categoryArray.length > 1
                            ? categoryArray[1] : "";
                    var expiryDateArray = queryArray != null ? queryArray[4].split("=") : null;
                    String expiryDate = expiryDateArray != null && expiryDateArray.length > 1
                            ? expiryDateArray[1] : "";
                    if (errorMessage != null) {
                        StringBuilder errorString = new StringBuilder("<ul>\n");
                        var errorMessages = errorMessage.split("#");
                        for (String error : errorMessages) {
                            errorString.append("<li id=errorMessage class=\"col\">").append(error).append("</li>");
                        }
                        var errorHtml = "<div class=\"alert alert-danger alert-dismissible fade show errorDiv\" role=\"alert\">\n" +
                                "        <span id=\"errorMessage\">"+ new String(errorString) +"</span>\n" +
                                "        <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\" onclick=\"closeErrorDiv()\"></button>\n" +
                                "    </div>";
                        response = response.replace("{{errorMessage}}", errorHtml);
                    } else {
                        response = response.replace("{{errorMessage}}", "");
                    }

                    response = response.replace("{{description}}", description);
                    response = response.replace("{{price}}", price);
                    response = response.replace("{{category}}", category);
                    response = response.replace("{{expiryDate}}", expiryDate);

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
