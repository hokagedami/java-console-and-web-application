import database.Migration;
import methods.customers.CustomerDAO;
import methods.customers.CustomerConsoleHelper;
import methods.products.ProductDAO;
import methods.products.ProductConsoleHelper;
import methods.products.SessionDAO;
import server.Server;

import java.util.Scanner;

public class Main {

    /**
     * Main method
     */
    public static void main(String[] args) {
        ProductConsoleHelper productConsoleHelper;
        CustomerConsoleHelper customerConsoleHelper;
        Migration migration;
        try {
            Class.forName("org.sqlite.JDBC");
            migration = new Migration();
            productConsoleHelper = new ProductConsoleHelper(migration);
            customerConsoleHelper = new CustomerConsoleHelper(migration);

            ProductDAO productDAO = new ProductDAO(migration);
            CustomerDAO customerDAO = new CustomerDAO(migration);
            SessionDAO sessionDAO = new SessionDAO(migration);

            // Server Instantiation
            int PORT = 8000;
            new Server(productDAO, customerDAO, sessionDAO, PORT);

            // Console App
            while (true){
                try {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("--------------------------------");
                    System.out.println("Welcome to the Food Product App");
                    System.out.println("Please select an option:");
                    System.out.println("A. Run Product App");
                    System.out.println("B. Run Customer App");
                    System.out.print("Exit: Any other key: ");
                    String option = sc.nextLine();
                    if (option.equalsIgnoreCase("A")) {
                        productConsoleHelper.runConsoleApp();
                    } else if (option.equalsIgnoreCase("B")) {
                        customerConsoleHelper.runConsoleApp();
                    } else {
                        System.out.println("Exiting...");
                        System.exit(0);
                    }
                }
                catch (Exception e) {
                    System.out.printf("Error starting application: %s%n", e.getMessage());
                    System.exit(1);
                }

            }

        } catch (Exception e) {
            System.out.printf("Error starting application: %s%n", e.getMessage());
            System.exit(1);
        }
    }
}