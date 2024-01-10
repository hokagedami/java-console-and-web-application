package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.products.ProductDAO;
import models.Product;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

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
            var idStr = "";
            if (in == null) {
                // redirect to not found page
                exchange.getResponseHeaders().add("Location", "/not-found");
                exchange.sendResponseHeaders(302, 0);
            } else {
                System.out.println("Edit product page html file found");
                var query = exchange.getRequestURI().getQuery();
                StringBuilder errorStrBuilder = new StringBuilder("<ul>\n");
                if (query != null){
                    var queryArray = query.split("&");
                    if (queryArray.length < 2) {
                        //redirect to customers page
                        exchange.getResponseHeaders().add("Location", "/products");
                        exchange.sendResponseHeaders(302, 0);
                        return;
                    }
                    var idStrQuery = queryArray[0].split("=");
                    idStr = idStrQuery.length > 1 && idStrQuery[1].isBlank() ? "" : idStrQuery[1];

                    if (idStr.isBlank()) {
                        //redirect to customers page
                        exchange.getResponseHeaders().add("Location", "/products");
                        exchange.sendResponseHeaders(302, 0);
                        return;
                    }
                    var pathQuery = queryArray[1].split("=");
                    var errorStr = pathQuery.length > 1 && pathQuery[1].isBlank() ? "" : pathQuery[1];
                    if(!errorStr.isBlank()) {
                        var errorStrDecoded = new String(Base64.getDecoder().decode(errorStr));
                        var errorArray = errorStrDecoded.split("&");
                        for (var error : errorArray) {
                            // error message
                            if(error.contains("errorMessage")) {
                                var errorMessageQuery = error.split("=");
                                var errorMsgArray = errorMessageQuery.length > 1
                                        && errorMessageQuery[1].isBlank() ? null : errorMessageQuery[1].split("#");
                                if(errorMsgArray != null && errorMsgArray.length > 0) {
                                    for (var errorMsg : errorMsgArray) {
                                        errorStrBuilder.append("<li id=errorMessage class=\"col\">")
                                                .append(errorMsg)
                                                .append("</li>\n");
                                    }
                                    errorStrBuilder.append("</ul>");
                                }
                                else {
                                    errorStrBuilder = new StringBuilder();
                                }
                            }
                            else {
                                break;
                            }
                        }
                    }
                }

                else
                    idStr = exchange.getRequestURI().toString().split("/")[3];
                int productId = Integer.parseInt(idStr);
                Product product = productDAO.findProduct(productId);
                if (product == null) {
                    //redirect to not found page
                    exchange.getResponseHeaders().add("Location", "/not-found");
                    exchange.sendResponseHeaders(302, 0);
                    return;
                }
                byte[] responseBytes = getBytesFromInputStream(in);
                // byte array to string
                String response = new String(responseBytes);

                if(query != null && !errorStrBuilder.isEmpty()) {
                    var errorHtml = "<div class=\"col alert alert-danger alert-dismissible fade show errorDiv\" role=\"alert\">\n" +
                            "        " + new String(errorStrBuilder) + "\n" +
                            "        <button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\" onclick=\"closeErrorDiv()\"></button>\n" +
                            "    </div>";
                    response = response.replace("{{errorMessage}}", errorHtml);
                }
                else {
                    response = response.replace("{{errorMessage}}", "");
                }

                response = response.replace("{{id}}", String.valueOf(product.getId()));
                response = response.replace("{{description}}", product.getDescription());
                response = response.replace("{{category}}", product.getCategory());
                response = response.replace("{{price}}", String.valueOf(product.getPrice()));
                response = response.replace("{{expiry_date}}", String.valueOf(product.getExpiryDate()));
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