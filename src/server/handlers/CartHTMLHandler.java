package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.products.ProductDAO;
import methods.products.SessionDAO;
import models.Product;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static server.helpers.HtmlHelper.generateCartItemsListHtml;

/**
 * This class is responsible for handling requests to the /cart endpoint.
 * It returns the cart.html page for GET requests to view the cart.
 */
public class CartHTMLHandler implements HttpHandler {

    String fileName;
    ProductDAO productDAO;
    SessionDAO sessionDAO;
    public CartHTMLHandler(ProductDAO productDAO, SessionDAO sessionDAO) {
        this.fileName = "static/products-cart.html";
        this.productDAO = productDAO;
        this.sessionDAO = sessionDAO;
    }

    /**
     * Handle the given request to /cart endpoint.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is {@code null}
     * @throws IOException          if an I/O error occurs
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            try(var in = getClass().getResourceAsStream("/" + fileName)) {
                if (in == null) {
                    System.out.println("File not found");
                    exchange.sendResponseHeaders(404, 0);
                } else {
                    // get sessionId from cookie
                    String cookie = exchange.getRequestHeaders().getFirst("Cookie");
                    var split = cookie.split(";");
                    var sessionCookie = Arrays.stream(split).filter(s -> s.contains("sessionId")).findFirst().orElse(null);
                    if(sessionCookie == null) {
                        exchange.getResponseHeaders().add("Location", "/products");
                        exchange.sendResponseHeaders(302, 0);
                        return;
                    }
                    var sessionId = sessionCookie.split("=")[1];
                    // get cart from database
                    var cart = sessionDAO.getCartItemsIdBySessionId(sessionId);
                    var cartSplit = cart.split(",");
                    var cartDict = new HashMap<Product, Integer>();
                    if(!cart.isEmpty()) {
                        for (String s : cartSplit) {
                            var product = productDAO.findProduct(Integer.parseInt(s.split(":")[0]));
                            cartDict.put(product, Integer.parseInt(s.split(":")[1]));
                        }
                    }
                    var cartHtmlTable = generateCartItemsListHtml(cartDict);
                    // replace cart table in html file
                    var html = new String(in.readAllBytes());
                    html = html.replace("{{productsCart}}", cartHtmlTable);
                    // send response
                    exchange.sendResponseHeaders(200, html.getBytes().length);
                    var output = exchange.getResponseBody();
                    output.write(html.getBytes());
                    output.close();
                }
            }
        } catch (Exception e) {
            // Print the error message
            System.out.println("Error: "+ e.getMessage());
            // redirect to products page
            exchange.getResponseHeaders().add("Location", "/products");
            exchange.sendResponseHeaders(302, 0);
        }
    }
}
