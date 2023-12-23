package methods.customers;

import database.Migration;
import models.Address;
import models.Customer;
import models.CustomerToUpdate;

import java.util.Scanner;

/**
 * This class is responsible for handling the console operations for the Customer class
 */
public class CustomerConsoleHelper {
    private final CustomerDAO customerDAO;

    /**
     * Constructor
     * @param migration Migration object to make connection the database
     */
    public CustomerConsoleHelper(Migration migration) {
            customerDAO = new CustomerDAO(migration);
    }

    /**
     * This method retrieves all customers from the database and prints them to the console
     */
    public void retrieveAllCustomers() {
        System.out.println("Retrieving all customers");
        var customers = customerDAO.findAllCustomers();
        if (customers != null) {
            for (var customer : customers) {
                System.out.printf("Customer={id=%s, address=%s, telephone=%s, businessName=%s}%n",
                        customer.getId(), customer.getAddress(), customer.getTelephone(),
                        customer.getBusinessName());
                System.out.println();
            }
        }
    }

    /**
     * This method searches for a customer by ID and prints it to the console
     */
    public void searchCustomer() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Searching for customer");
        System.out.print("Enter customer ID: ");
        int id = sc.nextInt();
        var customer = customerDAO.findCustomerByID(id);
        if (customer != null) {
            System.out.printf("Customer={id=%s, address=%s, telephone=%s, businessName=%s}%n",
                    customer.getId(), customer.getAddress(), customer.getTelephone(),
                    customer.getBusinessName());
        }
        else {
            System.out.println("Customer not found");
        }
    }

    /**
     * This method adds a new customer to the database and prints it to the console
     */
    public void addCustomer() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Adding new customer");
        System.out.print("Enter customer business name: ");
        String businessName = sc.nextLine();
        System.out.print("Enter customer telephone: ");
        String telephone = sc.nextLine();
        System.out.print("Enter customer address line 1: ");
        String addressLine1 = sc.nextLine();
        System.out.print("Enter customer address line 2: ");
        String addressLine2 = sc.nextLine();
        System.out.print("Enter customer address line 3: ");
        String addressLine3 = sc.nextLine();
        System.out.print("Enter customer country: ");
        String country = sc.nextLine();
        System.out.print("Enter customer post code: ");
        String postCode = sc.nextLine();
        var address = new Address(addressLine1, addressLine2, addressLine3, country, postCode);
        Customer customer = new Customer(address, telephone, businessName);
        var addedCustomer = customerDAO.addCustomer(customer);
        if (addedCustomer != null) {
            System.out.printf("Customer={id=%s, address=%s, telephone=%s, businessName=%s}%n",
                    addedCustomer.getId(), addedCustomer.getAddress(), addedCustomer.getTelephone(),
                    addedCustomer.getBusinessName());
        }
    }

    /**
     * This method updates a customer in the database and prints it to the console
     */
    public void updateCustomer(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Updating customer");
        System.out.print("Enter customer ID: ");
        String idStr = sc.nextLine();
        int id = 0;
        do {
            try {
                id = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID entered.");
                System.out.print("Do you want to re-enter the valid ID? Y/N: ");
                idStr = sc.nextLine();
                switch (idStr.toUpperCase()){
                    case "N":
                        return;
                    case "Y":
                        System.out.print("Enter customer ID: ");
                        idStr = sc.nextLine();
                        id = Integer.parseInt(idStr);
                        break;
                    default:
                        System.out.println("Invalid input! Y or N expected.");
                }
            }
        } while (id == 0);
        var customer = customerDAO.findCustomerByID(id);
        if (customer != null) {
            System.out.printf("Customer={id=%s, address=%s, telephone=%s, businessName=%s}%n",
                    customer.getId(), customer.getAddress(), customer.getTelephone(),
                    customer.getBusinessName());
            System.out.println("Enter new business name: ");
            String businessName = sc.nextLine();
            System.out.println("Enter new telephone: ");
            String telephone = sc.nextLine();
            System.out.println("Enter new address line 1: ");
            String addressLine1 = sc.nextLine();
            System.out.println("Enter new address line 2: ");
            String addressLine2 = sc.nextLine();
            System.out.println("Enter new address line 3: ");
            String addressLine3 = sc.nextLine();
            System.out.println("Enter new country: ");
            String country = sc.nextLine();
            System.out.println("Enter new post code: ");
            String postCode = sc.nextLine();
            var address = new Address(addressLine1, addressLine2, addressLine3, country, postCode);
            CustomerToUpdate customerToUpdate = new CustomerToUpdate(address, telephone, businessName);
            var updatedCustomer = customerDAO.updateCustomer(customerToUpdate, id);
            if (updatedCustomer != null) {
                System.out.printf("Customer={id=%s, address=%s, telephone=%s, businessName=%s}%n",
                        updatedCustomer.getId(), updatedCustomer.getAddress(), updatedCustomer.getTelephone(),
                        updatedCustomer.getBusinessName());
            }
        }
        else {
            System.out.println("Customer not found");
        }
    }

    /**
     * This method deletes a customer from the database
     */
    public void deleteCustomer(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Deleting customer");
        System.out.print("Enter customer ID: ");
        String idStr = sc.nextLine();
        int id = 0;
        do {
            try {
                id = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID entered.");
                System.out.print("Do you want to re-enter the valid ID? Y/N: ");
                idStr = sc.nextLine();
                switch (idStr.toUpperCase()){
                    case "N":
                        return;
                    case "Y":
                        System.out.print("Enter customer ID: ");
                        idStr = sc.nextLine();
                        id = Integer.parseInt(idStr);
                        break;
                    default:
                        System.out.println("Invalid input! Y or N expected.");
                }
            }
        } while (id == 0);
        var customer = customerDAO.findCustomerByID(id);
        if (customer != null) {
            System.out.printf("Customer={id=%s, address=%s, telephone=%s, businessName=%s}%n",
                    customer.getId(), customer.getAddress(), customer.getTelephone(),
                    customer.getBusinessName());
            System.out.println("Are you sure you want to delete this customer? (y/n)");
            String confirmation = sc.nextLine();
            if (confirmation.equals("y")) {
                var isDeleted = customerDAO.deleteCustomer(id);
                System.out.println( isDeleted ? "Customer deleted" : "Customer not deleted");
            }
            else {
                System.out.println("Customer not deleted");
            }
        }
        else {
            System.out.println("Customer not found");
        }
    }

    /**
     * This method runs the Customer console app
     */
    public void runConsoleApp(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the customer console app");
        while (true) {
            System.out.println("Please select an option:");
            System.out.println("1. Retrieve all customers");
            System.out.println("2. Search for a customer");
            System.out.println("3. Add a customer");
            System.out.println("4. Update a customer");
            System.out.println("5. Delete a customer");
            System.out.println("6. Exit");
            System.out.print("Enter option number: ");
            String option = sc.nextLine();
            switch (option) {
                case "1":
                    retrieveAllCustomers();
                    break;
                case "2":
                    searchCustomer();
                    break;
                case "3":
                    addCustomer();
                    break;
                case "4":
                    updateCustomer();
                    break;
                case "5":
                    deleteCustomer();
                    break;
                case "6":
                    System.out.println("Exiting customer console app");
                    return;
                default:
                    System.out.println("Invalid option selected");
            }
        }
    }

}
