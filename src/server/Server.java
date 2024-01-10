package server;

import com.sun.net.httpserver.HttpServer;
import methods.customers.CustomerDAO;
import methods.products.ProductDAO;
import methods.session.SessionDAO;
import server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * This class is responsible for starting the HTTP server
 */

public class Server {

    /**
     * Constructor
     * @param productDAO ProductDAO object to handle database operations for the Product class
     * @param customerDAO CustomerDAO object to handle database operations for the Customer class
     * @param PORT Port number for the server
     * @throws IOException Exception thrown if there is an error starting the server
     */
    public Server(ProductDAO productDAO, CustomerDAO customerDAO, SessionDAO sessionDAO, int PORT) throws IOException {
        // Initialize DAOs


        // Initialize HttpServer
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Set up context for serving admin.html

        // Set up context for bootstrap files
        server.createContext("/bootstrap", new BootstrapHandler());
        server.createContext("/admin", new AdminHandler());
        server.createContext("/logout", new AdminHandler());

        // Set up context for serving static resources (Bootstrap)
        server.createContext("/static", new StaticResourceHandler());

        // Set up context for serving products endpoints
        server.createContext("/products", new ProductsHTMLHandler(productDAO, sessionDAO));
        server.createContext("/product/new", new NewProductHTMLHandler());
        server.createContext("/products/edit", new EditProductHTMLHandler(productDAO));
        server.createContext("/products/update", new UpdateProductHandler(productDAO));
        server.createContext("/product/add", new AddProductHandler(productDAO));
        server.createContext("/products/del", new DeleteProductHandler(productDAO));

        // Set up context for serving cart endpoints
        server.createContext("/cart", new CartHTMLHandler(productDAO, sessionDAO));
        server.createContext("/cart/add", new AddToCartHandler(productDAO, sessionDAO));
        server.createContext("/cart/del", new DeleteFromCartHandler(productDAO, sessionDAO));

        // Set up context for serving customers endpoints
        server.createContext("/customers", new CustomersHTMLHandler(customerDAO));
        server.createContext("/customer/new", new NewCustomerHTMLHandler());
        server.createContext("/customers/edit", new EditCustomerHTMLHandler(customerDAO));
        server.createContext("/customers/update", new UpdateCustomerHandler(customerDAO));
        server.createContext("/customer/add", new AddCustomerHandler(customerDAO));
        server.createContext("/customers/del", new DeleteCustomerHandler(customerDAO));

        // Set up context for serving not found page
        server.createContext("/not-found", new NotFoundHandler());

        // Set up context for serving other endpoints
        server.createContext("/", new NotFoundHandler());
        // start server
        server.start();
        System.out.println("Server started on port 8000");
    }

}