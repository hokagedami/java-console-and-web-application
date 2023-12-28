package methods.products;

import database.Migration;
import models.Product;
import models.ProductToUpdate;

import java.sql.Date;
import java.util.Scanner;

/**
 * This class is responsible for handling the console operations for the Product class
 */
public class ProductConsoleHelper {

    private final ProductDAO productDAO;

    /**
     * Constructor
     * @param migration Migration object to make connection the database
     */
    public ProductConsoleHelper(Migration migration) {
        productDAO = new ProductDAO(migration);
    }

    /**
     * This method retrieves all products from the database and prints them to the console
     */
    public void retrieveAllProducts() {
        System.out.println("Retrieving all products");
        var products = productDAO.findAllProducts();
        if (products != null) {
            for (var product : products) {
                System.out.printf("Product={id=%s, SKU=%s, description=%s, category=%s, price=%s}%n",
                        product.getId(), product.getSKU(), product.getDescription(),
                        product.getCategory(), product.getPrice());
            }
        }
    }

    /**
     * This method searches for a product by ID and prints it to the console
     */
    public void searchProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Searching for product");
        System.out.print("Enter product ID: ");
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
                        System.out.print("Enter product ID: ");
                        idStr = sc.nextLine();
                        id = Integer.parseInt(idStr);
                        break;
                    default:
                        System.out.println("Invalid input! Y or N expected.");
                }
            }
        } while (id == 0);
        var product = productDAO.findProduct(id);
        if (product != null) {
            System.out.printf("Product={id=%s, SKU=%s, description=%s, category=%s, price=%s}%n",
                    product.getId(), product.getSKU(), product.getDescription(),
                    product.getCategory(), product.getPrice());
        }
        else {
            System.out.println("Product not found");
        }
    }

    /**
     * This method adds a new product to the database and prints it to the console
     */
    public void addProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Adding new product");
        System.out.print("Enter product description: ");
        String description = sc.nextLine();
        System.out.print("Enter product category: ");
        String category = sc.nextLine();
        System.out.print("Enter product price: ");
        int price = sc.nextInt();
        // if price is not a number, ask the user to re-enter the price
        while (price == 0) {
            System.out.println("Invalid price entered.");
            System.out.print("Enter product price: ");
            price = sc.nextInt();
        }
        System.out.print("Enter product expiry date (yyyy-mm-dd): ");
        String expiryDateStr = sc.next();
        Date expiryDate = Date.valueOf(expiryDateStr);
        // if date is not in the correct format, ask the user to re-enter the date
        while (expiryDate == null) {
            System.out.println("Invalid date format");
            System.out.print("Enter product expiry date (yyyy-mm-dd): ");
            expiryDateStr = sc.next();
            expiryDate = Date.valueOf(expiryDateStr);
        }
        Product product = new Product(description, category, price, expiryDate);
        var addedProduct = productDAO.addProduct(product);
        if (addedProduct != null) {
            System.out.printf("Product={id=%s, SKU=%s, description=%s, category=%s, price=%s}%n",
                    addedProduct.getId(), addedProduct.getSKU(), addedProduct.getDescription(),
                    addedProduct.getCategory(), addedProduct.getPrice());
        }
        
    }

    /**
     * This method updates a product in the database and prints it to the console
     */
    public void updateProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Updating product");
        System.out.print("Enter product ID: ");
        var idStr = sc.nextLine();
        int id = 0;
        do {
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID entered.");
                System.out.print("Do you want to re-enter the valid ID? Y/N: ");
                idStr = sc.nextLine();
                switch (idStr.toUpperCase()){
                    case "N":
                        return;
                    case "Y":
                        System.out.print("Enter product ID: ");
                        idStr = sc.nextLine();
                        id = Integer.parseInt(idStr);
                        break;
                    default:
                        System.out.println("Invalid input! Y or N expected.");
                }
            }
        } while (id == 0);
        var product = productDAO.findProduct(id);
        if (product != null) {
            System.out.println("Updating product...");
            System.out.printf("Product={id=%s, SKU=%s, description=%s, category=%s, price=%s, expiry=%s}%n",
                    product.getId(), product.getSKU(), product.getDescription(),
                    product.getCategory(), product.getPrice(), product.getExpiryDate());
            System.out.print("Enter new product description: ");
            String description = sc.nextLine();
            System.out.print("Enter new product category: ");
            String category = sc.nextLine();
            System.out.print("Enter new product price: ");
            var priceStr = sc.nextLine();
            int price = 0;
            do {
                try {
                    price = Integer.parseInt(priceStr);
                }
                catch (NumberFormatException ex){
                    System.out.println("Invalid value entered for price.");
                    System.out.print("Enter new product price: ");
                }
            }
            while (price == 0);
            System.out.print("Enter new product expiry date (yyyy-mm-dd): ");
            String expiryDateStr = sc.next();
            Date expiryDate = Date.valueOf(expiryDateStr);
            // if date is not in the correct format, ask the user to re-enter the date
            while (expiryDate == null) {
                System.out.println("Invalid date format");
                System.out.print("Enter product expiry date (yyyy-mm-dd): ");
                expiryDateStr = sc.next();
                expiryDate = Date.valueOf(expiryDateStr);
            }
            var updatedProduct = productDAO
                    .updateProduct(new ProductToUpdate(description, category, price, expiryDate), id);
            if (updatedProduct != null) {
                System.out.println("Product updated successfully");
                System.out.printf("Product={id=%s, SKU=%s, description=%s, category=%s, price=%s}%n",
                        updatedProduct.getId(), updatedProduct.getSKU(), updatedProduct.getDescription(),
                        updatedProduct.getCategory(), updatedProduct.getPrice());
                return;
            }
        }
        System.out.println("Product update failed!");
    }

    /**
     * This method deletes a product from the database
     */
    public void deleteProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Deleting product");
        System.out.print("Enter product ID: ");
        int id = sc.nextInt();
        var product = productDAO.findProduct(id);
        if (product != null) {
            System.out.printf("Product={id=%s, SKU=%s, description=%s, category=%s, price=%s, expiry=%s}%n",
                    product.getId(), product.getSKU(), product.getDescription(),
                    product.getCategory(), product.getPrice(), product.getExpiryDate());
            System.out.print("Are you sure you want to delete this product? (y/n): ");
            String choice = sc.next();
            if (choice.equalsIgnoreCase("y")) {
                if (productDAO.deleteProduct(id)) {
                    System.out.println("Product deleted successfully");
                    retrieveAllProducts();
                }
            }
        }
        else {
            System.out.println("Product not found");
        }
        
    }

    /**
     * This method runs the Product console app
     */
    public void runConsoleApp(){
        while (true) {
            System.out.println("-------------------------");
            System.out.println("Welcome to The Food Store.");
            System.out.print("Choose from this options: ");
            System.out.println("-------------------------");
            System.out.println("[1] List all products");
            System.out.println("[2] Search for product by ID");
            System.out.println("[3] Add new product");
            System.out.println("[4] Update a product by ID");
            System.out.println("[5] Delete a product by ID");
            System.out.println("[6] Exit");

            try {
                Scanner sc = new Scanner(System.in);
                String choice = sc.next();
                switch (choice) {
                    case "1":
                        retrieveAllProducts();
                        break;
                    case "2":
                        searchProduct();
                        break;
                    case "3":
                        addProduct();
                        break;
                    case "4":
                        updateProduct();
                        break;
                    case "5":
                        deleteProduct();
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice");
            }
        }
    }
}
