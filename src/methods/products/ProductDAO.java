package methods.products;

import database.Migration;
import models.Product;
import models.ProductToUpdate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for handling the database operations for the Product class
 */
public class ProductDAO {
    private final Migration migration;

    /**
     * Constructor
     * @param migration Migration object to make connection the database
     */
    public ProductDAO(Migration migration) {
        // Initialize database connection
        this.migration = migration;
    }

    /**
     * This method retrieves all products from the database
     * @return List of products
     */
    public List<Product> findAllProducts() {
        try {
            var con = migration.getDbConnection();
            List<Product> products = new ArrayList<>();
            String sql = "SELECT * FROM food_products";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String SKU = rs.getString("sku");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                Date expiryDate = rs.getDate("expiry_date");
                Product product = new Product(id, SKU, description, category, price, expiryDate);
                products.add(product);
            }
            if (products.isEmpty()) {
                System.out.println("No products found");
            }
            con.close();
            return products;
        } catch (SQLException e) {
            System.out.printf("Error retrieving food products: %s%n", e.getMessage());
            return null;
        }
    }

    /**
     * This method searches for a product by ID
     * @param id Product ID
     * @return Product object
     */
    public Product findProduct(int id) {
        try {
            String sql = "SELECT * FROM food_products WHERE id = ?";
            var con = migration.getDbConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String description = rs.getString("description");
                String SKU = rs.getString("sku");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                Date expiryDate = rs.getDate("expiry_date");
                con.close();
                return new Product(id, SKU, description, category, price, expiryDate);
            }
            System.out.printf("Food product with ID %s not found%n", id);
            con.close();
            return null;
        } catch (SQLException e) {
            System.out.printf("Error finding food product with ID %s:: %s%n", id, e.getMessage());
            return null;
        }
    }

    /**
     * This method deletes a product by ID
     * @param id Product ID
     * @return True if product is deleted, false otherwise
     */
    public boolean deleteProduct(int id) {
        try {
            var product = findProduct(id);
            if (product == null) {
                return false;
            }
            String sql = "DELETE FROM food_products WHERE id = ?";
            var con = migration.getDbConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            con.close();
        } catch (SQLException e) {
            System.out.printf("Error deleting food product with ID %s:: %s%n", id, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * This method updates a product
     * @param product Product object to update
     * @param id Product ID to update
     * @return Updated Product object
     */
    public Product updateProduct(ProductToUpdate product, int id) {
        try {
            String sql = "UPDATE food_products SET description = ?, price = ?," +
                    " category = ?, expiry_date = ? WHERE id = ?";
            var con = migration.getDbConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, product.getDescription());
            stmt.setInt(2, product.getPrice());
            stmt.setString(3, product.getCategory());
            stmt.setDate(4, product.getExpiryDate());
            stmt.setInt(5, id);
            stmt.executeUpdate();
            con.close();
            return findProduct(id);
        } catch (SQLException e) {
            System.out.printf("Error updating food product with ID %s:: %s%n",
                    id, e.getMessage());
            return null;
        }
    }

    /**
     * This method adds a new product to the database
     * @param product Product object to add
     * @return Product object
     */
    public Product addProduct(Product product) {
        var con = migration.getDbConnection();
        try {
            String sql = "INSERT INTO food_products (description, category, price, sku, expiry_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, product.getDescription());
            stmt.setString(2, product.getCategory());
            stmt.setInt(3, product.getPrice());
            stmt.setString(4, product.getSKU());
            stmt.setDate(5, product.getExpiryDate());
            stmt.executeUpdate();
            ResultSet rs = con.createStatement().executeQuery("SELECT last_insert_rowid()");
            int id = rs.getInt(1);
            con.close();
            return findProduct(id);
        } catch (SQLException e) {
            System.out.printf("Error adding food product %s%n", e.getMessage());
            return null;
        }
    }
    public List<Product> findProductsInCart(List<Integer> cartProductIds) {
        try {
            // get cart products using list of productId from database
            var con = migration.getDbConnection();
            StringBuilder sql = new StringBuilder("SELECT * FROM food_products WHERE id IN (");
            for (int i = 0; i < cartProductIds.size(); i++) {
                sql.append("?");
                if (i != cartProductIds.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(")");
            PreparedStatement stmt = con.prepareStatement(sql.toString());
            for (int i = 0; i < cartProductIds.size(); i++) {
                stmt.setInt(i + 1, cartProductIds.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String SKU = rs.getString("sku");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                Date expiryDate = rs.getDate("expiry_date");
                Product product = new Product(id, SKU, description, category, price, expiryDate);
                products.add(product);
            }
            if (products.isEmpty()) {
                System.out.printf("No products found with categories %s%n", cartProductIds);
            }
            con.close();
            return products;
        } catch (SQLException e) {
            System.out.printf("Error finding product of cart:::" + e.getMessage());
            return null;
        }
    }

    /**
     * This method searches for a product by description using a search string
     * @param searchString Search string
     * @return List of product with matching description
     */
    public List<Product> findProductsByDescription(String searchString) {
        try {
            String sql = "SELECT * FROM food_products WHERE description LIKE ?";
            var con = migration.getDbConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, "%" + searchString + "%");
            ResultSet rs = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String SKU = rs.getString("sku");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                String description = rs.getString("description");
                Date expiryDate = rs.getDate("expiry_date");
                Product product = new Product(id, SKU, description, category, price, expiryDate);
                products.add(product);
            }
            if (products.isEmpty()) {
                System.out.printf("No products found with description %s%n", searchString);
            }
            con.close();
            return products;
        } catch (SQLException e) {
            System.out.printf("Error finding food product with description %s:: %s%n", searchString, e.getMessage());
            return null;
        }
    }

    /**
     * This method searches for products by category using a list of categories
     * @param categories List of categories
     * @return List of products with matching categories
     */
    public List<Product> filterProductsByCategories(ArrayList<String> categories){
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM food_products WHERE category IN (");
            for (int i = 0; i < categories.size(); i++) {
                sql.append("?");
                if (i != categories.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(")");
            var con = migration.getDbConnection();
            PreparedStatement stmt = con.prepareStatement(sql.toString());
            for (int i = 0; i < categories.size(); i++) {
                stmt.setString(i + 1, categories.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String SKU = rs.getString("sku");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                Date expiryDate = rs.getDate("expiry_date");
                Product product = new Product(id, SKU, description, category, price, expiryDate);
                products.add(product);
            }
            if (products.isEmpty()) {
                System.out.printf("No products found with categories %s%n", categories);
            }
            con.close();
            return products;
        } catch (SQLException e) {
            System.out.printf("Error finding food product with categories %s:: %s%n", categories, e.getMessage());
            return null;
        }
    }

    /**
     * This method searches for products by description and category using a search string,
     * list of categories and expiry date
     * @param description Search string
     * @param categories List of categories
     * @param expiryDate Expiry date
     * @return List of products with matching description and categories
     */
    public List<Product> findProductsBySearchAndFilterParameters(String description,
                                                                ArrayList<String> categories, Date expiryDate)
    {
        try {
            // if expiry date is null, search for products by description and categories
            if (expiryDate == null && !categories.isEmpty() && description != null) {
                return findProductsByDescriptionAndCategories(description, categories);
            }
            // if categories is null, search for products by description and expiry date
            else if (categories.isEmpty() && description != null && expiryDate != null) {
                return findProductsByDescriptionAndExpiryDate(description, expiryDate);
            }
            // if description is null, search for products by categories and expiry date
            else if (description == null && !categories.isEmpty() && expiryDate != null) {
                return findProductsByCategoriesAndExpiryDate(categories, expiryDate);
            }
            // if description and categories are null, search for products by expiry date
            else if (description == null && categories.isEmpty() && expiryDate != null) {
                return findProductsByExpiryDate(expiryDate);
            }
            // if description is null, search for products by categories
            else if (description == null && expiryDate == null && !categories.isEmpty()) {
                return filterProductsByCategories(categories);
            }
            // if categories is null, search for products by description
            else if (categories.isEmpty() && expiryDate == null && description != null) {
                return findProductsByDescription(description);
            }
            else {
                StringBuilder sql = new StringBuilder("SELECT * FROM food_products WHERE description" +
                        " LIKE ? AND category IN (");
                for (int i = 0; i < categories.size(); i++) {
                    sql.append("?");
                    if (i != categories.size() - 1) {
                        sql.append(",");
                    }
                }
                sql.append(") AND expiry_date = ?");
                var con = migration.getDbConnection();
                PreparedStatement stmt = con.prepareStatement(sql.toString());
                stmt.setString(1, "%" + description + "%");
                for (int i = 0; i < categories.size(); i++) {
                    stmt.setString(i + 2, categories.get(i));
                }
                stmt.setDate(categories.size() + 2, expiryDate);
                ResultSet rs = stmt.executeQuery();
                List<Product> products = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String SKU = rs.getString("sku");
                    String category = rs.getString("category");
                    int price = rs.getInt("price");
                    Date expiryDateFromDb = rs.getDate("expiry_date");
                    Product product = new Product(id, SKU, description, category, price, expiryDateFromDb);
                    products.add(product);
                }
                if (products.isEmpty()) {
                    System.out.printf("No products found with categories %s%n", categories);
                }
                con.close();
                return products;
            }
        }
        catch (Exception e) {
            System.out.printf("Error finding food product with categories %s:: %s%n", categories, e.getMessage());
            return null;
        }
    }

    /**
     * This method searches for products by expiry date
     * @param expiryDate Expiry Date
     * @return List of products expiring on or before the expiry date
     */
    public List<Product> findProductsByExpiryDate(Date expiryDate){
        try {
            // get products from database whose expiry date is on or before the expiry date
            String sql = "SELECT * FROM food_products WHERE expiry_date <= ?";
            var con = migration.getDbConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setDate(1, expiryDate);
            ResultSet rs = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String SKU = rs.getString("sku");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                Date expiryDateFromDb = rs.getDate("expiry_date");
                Product product = new Product(id, SKU, description, category, price, expiryDateFromDb);
                products.add(product);
            }
            if (products.isEmpty()) {
                System.out.printf("No products found with expiry date %s%n", expiryDate);
            }
            con.close();
            return products;
        }
        catch (Exception e) {
            System.out.printf("Error finding food product with expiry date %s:: %s%n", expiryDate, e.getMessage());
            return null;
        }
    }

    /**
     * This method searches for products by description and expiry date
     * @param description Search string
     * @param expiryDate Expiry date
     * @return List of products with matching description and expiry date
     */
    public List<Product> findProductsByDescriptionAndExpiryDate(String description, Date expiryDate){
        try {
            String sql = "SELECT * FROM food_products WHERE description LIKE ? AND expiry_date <= ?";
            var con = migration.getDbConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, "%" + description + "%");
            stmt.setDate(2, expiryDate);
            ResultSet rs = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String SKU = rs.getString("sku");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                Date expiryDateFromDb = rs.getDate("expiry_date");
                Product product = new Product(id, SKU, description, category, price, expiryDateFromDb);
                products.add(product);
            }
            if (products.isEmpty()) {
                System.out.printf("No products found with description %s and expiry date %s%n", description, expiryDate);
            }
            con.close();
            return products;
        }
        catch (Exception e) {
            System.out.printf("Error finding food product with description %s and expiry date %s:: %s%n", description, expiryDate, e.getMessage());
            return null;
        }
    }

    /**
     * This method searches for products by description, and categories
     * @param description Search string
     * @param categories List of categories
     * @return List of products with matching categories and expiry date
     */
    public List<Product> findProductsByDescriptionAndCategories(String description, ArrayList<String> categories){
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM food_products WHERE description" +
                    " LIKE ? AND category IN (");
            for (int i = 0; i < categories.size(); i++) {
                sql.append("?");
                if (i != categories.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(")");
            var con = migration.getDbConnection();
            PreparedStatement stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, "%" + description + "%");
            for (int i = 0; i < categories.size(); i++) {
                stmt.setString(i + 2, categories.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String SKU = rs.getString("sku");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                Date expiryDate = rs.getDate("expiry_date");
                Product product = new Product(id, SKU, description, category, price, expiryDate);
                products.add(product);
            }
            if (products.isEmpty()) {
                System.out.printf("No products found with description %s and categories %s%n", description, categories);
            }
            con.close();
            return products;
        }
        catch (Exception e) {
            System.out.printf("Error finding food product with description %s and categories %s:: %s%n", description, categories, e.getMessage());
            return null;

        }
    }

    /**
     * This method searches for products by categories and expiry date
     * @param categories List of categories
     * @param expiryDate Expiry date
     * @return List of products with matching categories and expiry date
     */
    public List<Product> findProductsByCategoriesAndExpiryDate(ArrayList<String> categories, Date expiryDate){
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM food_products WHERE category IN (");
            for (int i = 0; i < categories.size(); i++) {
                sql.append("?");
                if (i != categories.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") AND expiry_date <= ?");
            var con = migration.getDbConnection();
            PreparedStatement stmt = con.prepareStatement(sql.toString());
            for (int i = 0; i < categories.size(); i++) {
                stmt.setString(i + 1, categories.get(i));
            }
            stmt.setDate(categories.size() + 1, expiryDate);
            ResultSet rs = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String SKU = rs.getString("sku");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                Date expiryDateFromDb = rs.getDate("expiry_date");
                Product product = new Product(id, SKU, description, category, price, expiryDateFromDb);
                products.add(product);
            }
            if (products.isEmpty()) {
                System.out.printf("No products found with categories %s and expiry date %s%n", categories, expiryDate);
            }
            con.close();
            return products;
        }
        catch (Exception e) {
            System.out.printf("Error finding food product with categories %s and expiry date %s:: %s%n", categories, expiryDate, e.getMessage());
            return null;
        }
    }

    /**
     * This method deletes the database
     */
    public void deleteDatabase() {
        migration.deleteDatabase();
    }

}

