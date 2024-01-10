package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.products.ProductDAO;
import methods.session.SessionDAO;
import models.Product;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static server.helpers.HandlerHelpers.GenerateSessionId;
import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;
import static server.helpers.HtmlHelper.*;

/**
 * This class is responsible for handling requests to the /products endpoint.
 * It returns the products.html page for GET requests to view all products.
 * It also handles POST requests for filtering products by category and searching products by description
 */
public class ProductsHTMLHandler implements HttpHandler {
    String fileName;
    ProductDAO productDAO;
    SessionDAO sessionDao;

    /**
     * Constructor
     * @param productDAO ProductDAO object to handle database operations for the Product class
     */
    public ProductsHTMLHandler(ProductDAO productDAO, SessionDAO sessionDao) {
        this.fileName = "static/products.html";
        this.productDAO = productDAO;
        this.sessionDao = sessionDao;
    }

    /**
     * This overridden method handles requests (GET & POST) to the /products endpoint
     * @param exchange HttpExchange object
     * @throws IOException Exception thrown if there is an error handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (var in = getClass().getResourceAsStream("/" + fileName)) {
            if (in == null) {
                System.out.println("File not found");
                exchange.sendResponseHeaders(404, 0);
            } else {
                List<Product> products;
                System.out.println("Products page html file found");
                // get response bytes
                byte[] responseBytes = getBytesFromInputStream(in);
                // byte array to string
                String response = new String(responseBytes);
                // check if request is post
                if (exchange.getRequestMethod().equals("POST")) {
                    InputStream is = exchange.getRequestBody();
                    String body = URLDecoder.decode(new String(is.readAllBytes(), StandardCharsets.UTF_8),
                            StandardCharsets.UTF_8);
                    var split = body.split("&");
                    if(split.length <= 1) {
                        // redirect to products page
                        exchange.getResponseHeaders().add("Location", "/products");
                        exchange.sendResponseHeaders(302, 0);
                        return;
                    }

                    ArrayList<String> categories = new ArrayList<>();
                    for (var sp : split) {
                        if (sp.contains("category")) {
                            categories.add(sp.split("=")[1]);
                        }
                    }
                    var descriptionBody = Arrays.stream(split).filter(s -> s.contains("description")).findFirst().orElse(null);
                    var description = descriptionBody != null && descriptionBody.split("=").length > 1
                            ? descriptionBody.split("=")[1].trim() : null;
                    var expiryDateBody = Arrays.stream(split).filter(s -> s.contains("date")).findAny().orElse(null);
                    var expiryDate = expiryDateBody != null && expiryDateBody.split("=").length > 1
                            ? Date.valueOf(expiryDateBody.split("=")[1]) : null;
                    if(categories.isEmpty()
                            && (description == null || description.isEmpty())
                            && expiryDate == null) {
                        // redirect to products page
                        exchange.getResponseHeaders().add("Location", "/products");
                        exchange.sendResponseHeaders(302, 0);
                        return;
                    }
                    products = productDAO.findProductsBySearchAndFilterParameters(description, categories, expiryDate);
                    description = description != null ? description : "";
                    var expiryDateString = expiryDate != null ? expiryDate.toString() : "";
                    response = response.replace("{{searchFilterView}}",
                            generateFilterAndSearchViewHtml(categories, description, expiryDateString));

                }
                else {
                    products = productDAO.findAllProducts();
                }

                // replace {{productList}} with generated HTML
                String productListHTML = generateProductListHTML(products, VerifyUserIsAdmin(exchange));
                String filterHtml = generateProductCategoryCheckboxes(products);
                response = response.replace("{{filters}}", filterHtml);
                response = response.replace("{{productList}}", productListHTML);
                response = response.replace("{{searchFilterView}}", "");
                // get sessionId from cookie
                String cookie = exchange.getRequestHeaders().getFirst("Cookie");
                if (cookie != null) {
                    if (VerifyUserIsAdmin(exchange)) {
                        exchange.getResponseHeaders().add("Set-Cookie", "sessionId=; Max-Age=0");
                    }
                    else {
                        var split = cookie.split(";");
                        var sessionCookie = Arrays.stream(split).filter(s -> s.contains("sessionId")).findFirst().orElse(null);
                        if (sessionCookie == null) {
                            // create session id
                            String sessionId = GenerateSessionId();
                            // save session id to database
                            var sess = sessionDao.createSession(sessionId);
                            if (sess == null) {
                                System.out.println("Error creating session for Product page");
                                // return 404-page
                                // read the 404.html file
                                try (var notFound = getClass().getResourceAsStream("/static/not-found.html")) {
                                    if (notFound == null) {
                                        System.out.println("404 file not found");
                                        exchange.sendResponseHeaders(404, 0);
                                    } else {
                                        // get response bytes
                                        responseBytes = getBytesFromInputStream(notFound);
                                        // send response
                                        exchange.sendResponseHeaders(404, responseBytes.length);
                                        OutputStream output = exchange.getResponseBody();
                                        output.write(responseBytes);
                                        output.close();
                                    }
                                }
                                return;
                            }
                            // add session id to header
                            exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + sessionId);
                        }
                    }
                    // get cart from database using sessionId
                    if(!VerifyUserIsAdmin(exchange)){
                        var cart = sessionDao.getCartItemsIdBySessionId(cookie.split("=")[1]);
                        if(!cart.isEmpty()) {
                            var cartSplit = cart.split(",");
                            // replace {{cartSize}} with cart size
                            response = response.replace("{{cartSize}}", String.valueOf(cartSplit.length));
                        }
                        else {
                            // replace {{cartSize}} with cart size
                            response = response.replace("{{cartSize}}", String.valueOf(0));
                        }
                    }
                    else {
                        response = response.replace("{{cartSize}}", String.valueOf(0));
                    }
                }
                else {
                    if (!VerifyUserIsAdmin(exchange)) {
                        // create session id
                        String sessionId = GenerateSessionId();
                        // save session id to database
                        var sess = sessionDao.createSession(sessionId);
                        if (sess == null) {
                            System.out.println("Error creating session for Product page");
                            // return 404-page
                            // read the 404.html file
                            try (var notFound = getClass().getResourceAsStream("/static/not-found.html")) {
                                if (notFound == null) {
                                    System.out.println("404 file not found");
                                    exchange.sendResponseHeaders(404, 0);
                                } else {
                                    // get response bytes
                                    responseBytes = getBytesFromInputStream(notFound);
                                    // send response
                                    exchange.sendResponseHeaders(404, responseBytes.length);
                                    OutputStream output = exchange.getResponseBody();
                                    output.write(responseBytes);
                                    output.close();
                                }
                            }
                            return;
                        }
                        response = response.replace("{{cartSize}}", String.valueOf(0));
                        exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + sessionId);
                    }
                }


                if (!VerifyUserIsAdmin(exchange)) {
                    response = response.replace("{{login}}",
                            "<li class=\"nav-item\"> <a href=\"/logout\" class=\"nav-link btn btn-success\">Login</a> </li>");
                    response = response.replace("{{addButton}}", "");
                } else {
                    response = response.replace("visible", "hidden");
                    response = response.replace("{{login}}",
                            "<li class=\"nav-item\"> <a href=\"/logout\" class=\"nav-link btn btn-danger\">Logout</a> </li>");
                    response = response.replace("{{addButton}}",
                            "<a href=\"/product/new\" class=\"btn btn-secondary \">Add New Product</a>");
                }
                response = response.replace("{{productList}}", productListHTML);
                responseBytes = response.getBytes();

                // send response
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseBytes);
                output.close();
            }
        }
    }
}