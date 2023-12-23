package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.products.ProductDAO;
import methods.products.SessionDAO;

import java.io.IOException;
import java.util.Arrays;

public class DeleteFromCartHandler implements HttpHandler {

    ProductDAO productDAO;
    SessionDAO sessionDAO;
    public DeleteFromCartHandler(ProductDAO productDAO, SessionDAO sessionDAO) {
        this.productDAO = productDAO;
        this.sessionDAO = sessionDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
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
            // get product id from path param
            var path = exchange.getRequestURI().getPath();
            var splitPath = path.split("/");
            var productId = Integer.parseInt(splitPath[splitPath.length - 1]);
            // remove product from cart
            if(cart.contains(productId + ":"))
            {
                var splitCart = cart.split(",");
                for (int i = 0; i < splitCart.length; i++) {
                    if(splitCart[i].contains(productId + ":")) {
                        var splitProduct = splitCart[i].split(":");
                        var quantity = Integer.parseInt(splitProduct[1]);
                        quantity--;
                        if(quantity == 0) {
                            splitCart[i] = "";
                        }
                        else {
                            splitCart[i] = productId + ":" + quantity;
                        }
                        break;
                    }
                }
                cart = String.join(",", splitCart);
            }
            // update session with new cartItems
            sessionDAO.updateSessionCartItem(sessionId, cart);
            // redirect to cart page
            exchange.getResponseHeaders().add("Location", "/cart");
            exchange.sendResponseHeaders(302, 0);
        }
        catch (Exception e) {
            System.out.println("Error: "+ e.getMessage());
            // redirect to products page
            exchange.getResponseHeaders().add("Location", "/products");
            exchange.sendResponseHeaders(302, 0);
        }
    }
}
