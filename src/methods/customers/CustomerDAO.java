package methods.customers;

import database.Migration;
import models.Address;
import models.Customer;
import models.CustomerToUpdate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for handling the database operations for the Customer class
 */
public class CustomerDAO {

    private final Migration migration;

    /**
     * Constructor
     * @param migration Migration object to make connection the database
     */
    public CustomerDAO(Migration migration) {
        this.migration = migration;
    }

    /**
     * This method adds a new customer to the database
     * @param customer Customer object
     * @return Customer object
     */
    public Customer addCustomer(Customer customer) {
        // add customer to database
        try {
            var con = migration.getDbConnection();
            String sql = "INSERT INTO customers (businessName, telephone, addressLine1, addressLine2, addressLine3, country, postCode) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, customer.getBusinessName());
            stmt.setString(2, customer.getTelephone());
            stmt.setString(3, customer.getAddressObject().getAddressLine1());
            stmt.setString(4, customer.getAddressObject().getAddressLine2());
            stmt.setString(5, customer.getAddressObject().getAddressLine3());
            stmt.setString(6, customer.getAddressObject().getCountry());
            stmt.setString(7, customer.getAddressObject().getPostCode());
            stmt.executeUpdate();
            String idStr = "SELECT last_insert_rowid()";
            Statement idStmt = con.createStatement();
            ResultSet rs = idStmt.executeQuery(idStr);
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.printf("Customer %s added successfully%n", customer.getBusinessName());
                con.close();
                return findCustomerByID(id);
            }
            System.out.printf("Customer %s not added!%n", customer.getBusinessName());
            return null;
        } catch (SQLException e) {
            System.out.printf("Error adding customer: %s%n", e.getMessage());
            return null;
        }
    }

    /**
     * This method searches for a customer by ID
     * @param id Customer ID
     * @return Customer object
     */
    public Customer findCustomerByID(int id) {
        // find customer in database by ID
        try {
            var con = migration.getDbConnection();
            String sql = "SELECT * FROM customers WHERE id = ? LIMIT 1";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String businessName = rs.getString("businessName");
                String addressLine1 = rs.getString("addressLine1");
                String addressLine2 = rs.getString("addressLine2");
                String addressLine3 = rs.getString("addressLine3");
                String country = rs.getString("country");
                String telephone = rs.getString("telephone");
                String postCode = rs.getString("postCode");
                int customerId = rs.getInt("id");
                var address = new Address(addressLine1, addressLine2, addressLine3, country, postCode);
                Customer customer = new Customer(customerId, address, telephone, businessName);
                System.out.printf("Customer={id=%s, businessName=%s, address=%s, telephone=%s}%n",
                        id, businessName, address, telephone);
                con.close();
                return customer;
            }
            else
            {
                System.out.printf("Customer with ID %s not found%n", id);
            }
            con.close();
            return null;
        }
        catch (SQLException e)
        {
            System.out.printf("Error retrieving customer: %s%n", e.getMessage());
            return null;
        }
    }

    /**
     * This method retrieves all customers from the database
     * @return List of customers
     */
    public List<Customer> findAllCustomers() {
        // find all customers in database
        try {
            var con = migration.getDbConnection();
            String sql = "SELECT * FROM customers";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String businessName = rs.getString("businessName");
                String addressLine1 = rs.getString("addressLine1");
                String addressLine2 = rs.getString("addressLine2");
                String addressLine3 = rs.getString("addressLine3");
                String country = rs.getString("country");
                String telephone = rs.getString("telephone");
                String postCode = rs.getString("postCode");
                var address = new Address(addressLine1, addressLine2, addressLine3, country, postCode);
                Customer customer = new Customer(id, address, telephone, businessName);
                customers.add(customer);
            }
            if (customers.isEmpty()) {
                System.out.println("No customers found");
            }
            con.close();
            return customers;
        } catch (SQLException e) {
            System.out.printf("Error retrieving customers: %s%n", e.getMessage());
            return null;
        }
    }

    /**
     * This method updates a customer in the database
     * @param customer CustomerToUpdate object
     * @param id Customer ID
     * @return Customer object
     */
    public Customer updateCustomer(CustomerToUpdate customer, int id){
    // update a customer in database
        try {
            var con = migration.getDbConnection();
            String sql = "UPDATE customers SET businessName = ?, telephone = ?, " +
                    "addressLine1 = ?, addressLine2 = ?, addressLine3 = ?," +
                    " country = ?, postCode = ? WHERE id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, customer.getBusinessName());
            stmt.setString(2, customer.getTelephone());
            stmt.setString(3, customer.getAddress().getAddressLine1());
            stmt.setString(4, customer.getAddress().getAddressLine2());
            stmt.setString(5, customer.getAddress().getAddressLine3());
            stmt.setString(6, customer.getAddress().getCountry());
            stmt.setString(7, customer.getAddress().getPostCode());
            stmt.setInt(8, id);
            stmt.executeUpdate();
            System.out.printf("Customer %s updated successfully%n", customer.getBusinessName());
            con.close();
            return findCustomerByID(id);
        } catch (SQLException e) {
            System.out.printf("Error updating customer: %s%n", e.getMessage());
            return null;
        }
    }

    /**
     * This method deletes a customer from the database and returns true if successful
     * @param id Customer ID
     * @return boolean
     */
    public boolean deleteCustomer(int id){
        // delete a customer from database
        try {
            var customer = findCustomerByID(id);
            if (customer == null) {
                System.out.printf("Customer with ID %s not found%n", id);
                return false;
            }
            var con = migration.getDbConnection();
            String sql = "DELETE FROM customers WHERE id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.printf("Customer with ID %s deleted successfully%n", id);
            con.close();
            return true;
        } catch (SQLException e) {
            System.out.printf("Error deleting customer: %s%n", e.getMessage());
            return false;
        }
    }

    public void deleteDatabase() {
        migration.deleteDatabase();
    }
}
