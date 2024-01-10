package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import methods.customers.CustomerDAO;
import models.Customer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static server.helpers.HandlerHelpers.VerifyUserIsAdmin;
import static server.helpers.HtmlHelper.generateCustomerListHTML;
import static server.helpers.HtmlHelper.getBytesFromInputStream;

/**
 * This class is responsible for handling requests to the /customers endpoint.
 * It returns the page for viewing all customers in the database.
 * Only valid for Admin users. Redirects to /admin for login if user is not admin.
 */
public class CustomersHTMLHandler implements HttpHandler {
    String fileName;
    CustomerDAO customerDAO;

    /**
     * Constructor
     * @param customerDAO CustomerDAO object to handle database operations for the Customer class
     */
    public CustomersHTMLHandler(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
        this.fileName = "static/customers.html";
    }

    /**
     * This overridden method handles requests (GET) to the /customers endpoint
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
                System.out.println("Customer page html file found");
                byte[] responseBytes = getBytesFromInputStream(in);
                // byte array to string
                String response = new String(responseBytes);
                // replace {{productList}} with generated HTML
                List<Customer> customers = customerDAO.findAllCustomers();
                String customerListHTML = generateCustomerListHTML(customers, VerifyUserIsAdmin(exchange));
                response = response.replace("{{customerList}}", customerListHTML);
                if (!VerifyUserIsAdmin(exchange)) {
                    response = response.replace("{{login}}",
                            "<li class=\"nav-item\"> <a href=\"/logout\" class=\"nav-link btn btn-success\">Login</a> </li>");
                    response = response.replace("{{add_button}}", "");
                } else {
                    response = response.replace("{{login}}",
                            "<li class=\"nav-item\"> <a href=\"/logout\" class=\"nav-link btn btn-danger\">Logout</a> </li>");
                    response = response.replace("{{add_button}}",
                            "<a href=\"/customer/new\" class=\"btn btn-secondary \">Add New Customer</a>");
                }
                responseBytes = response.getBytes();
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream output = exchange.getResponseBody();
                output.write(responseBytes);
                output.close();
            }
        }
    }
}
