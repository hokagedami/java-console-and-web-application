package tests;

import database.Migration;
import methods.customers.CustomerDAO;
import models.Address;
import models.Customer;
import models.CustomerToUpdate;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerTest {
    private static final String DATABASE_FILE_NAME = "test_db.sqlite";
    private CustomerDAO customerDAO;
    private final List<Customer> customers = new ArrayList<>(){
{
            add(new Customer(new Address("Test Address Line 1", "Test Address Line 2",
                    "Test Address Line 3", "Test Country 1", "Test Post Code"),
                    "Test Telephone", "Test Business Name 1"));
            add(new Customer(new Address("Test Address Line 1", "Test Address Line 2",
                    "Test Address Line 3", "Test Country 1", "Test Post Code"),
                    "Test Telephone", "Test Business Name 2"));
            add(new Customer(new Address("Test Address Line 1", "Test Address Line 2",
                    "Test Address Line 3", "Test Country 2", "Test Post Code"),
                    "Test Telephone", "Test Business Name 3"));
            add(new Customer(new Address("Test Address Line 1", "Test Address Line 2",
                    "Test Address Line 3", "Test Country 2", "Test Post Code"),
                    "Test Telephone", "Test Business Name 4"));

        }
    };

    @Before
    public void setUp() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Migration migration = new Migration(DATABASE_FILE_NAME);
        customerDAO = new CustomerDAO(migration);
        for (Customer customer : customers) {
            customerDAO.addCustomer(customer);
        }
    }
    @Test
    public void test0FindAllCustomers() {
        List<Customer> customersFromDB = customerDAO.findAllCustomers();
        Assert.assertEquals(customersFromDB.size(), 4);
    }
    @Test
    public void test1FindCustomerByID() {
        Customer customer = customerDAO.findCustomerByID(1);
        assert customer != null;
        assert customer.getBusinessName().equals("Test Business Name 1");
        assert customer.getTelephone().equals("Test Telephone");
        assert customer.getAddressObject().getAddressLine1().equals("Test Address Line 1");
        assert customer.getAddressObject().getAddressLine2().equals("Test Address Line 2");
        assert customer.getAddressObject().getAddressLine3().equals("Test Address Line 3");
        assert customer.getAddressObject().getCountry().equals("Test Country 1");
        assert customer.getAddressObject().getPostCode().equals("Test Post Code");
    }
    @Test
    public void test2FindInvalidCustomerByID() {
        Customer customer = customerDAO.findCustomerByID(100);
        assert customer == null;
    }
    @Test
    public void test3AddCustomer(){
        Customer customer = new Customer(new Address("Test Address Line 1", "Test Address Line 2",
                "Test Address Line 3", "Test Country 3", "Test Post Code"),
                "Test Telephone", "Test Business Name 5");
        customerDAO.addCustomer(customer);
        Customer customerFromDB = customerDAO.findCustomerByID(5);
        assert customerFromDB != null;
        assert customerFromDB.getBusinessName().equals("Test Business Name 5");
    }
    @Test
    public void test4UpdateCustomer(){
        // test update valid customer
        var customer = customerDAO.findCustomerByID(1);
        var customerToUpdate = new CustomerToUpdate(new Address("Test Address Line 1", "Test Address Line 2",
                "Test Address Line 3", "Test Country 3", "Test Post Code"),
                "Test Telephone", "Test Business Name 5");
        var updated = customerDAO.updateCustomer(customerToUpdate, customer.getId());
        assert updated != null;
        assert updated.getBusinessName().equals("Test Business Name 5");
        assert !Objects.equals(updated.getBusinessName(), customer.getBusinessName());
    }
    @Test
    public void test5UpdateInvalidCustomer(){
        var customerToUpdate = new CustomerToUpdate(new Address("Test Address Line 1", "Test Address Line 2",
                "Test Address Line 3", "Test Country 3", "Test Post Code"),
                "Test Telephone", "Test Business Name 5");
        var updated = customerDAO.updateCustomer(customerToUpdate, 100);
        assert updated == null;
    }
    @Test
    public void test6DeleteCustomer(){
        Assert.assertTrue(customerDAO.deleteCustomer(1));
    }
    @Test
    public void test7DeleteInvalidCustomer(){
        var deleted = customerDAO.deleteCustomer(100);
        assert !deleted;
    }
    @After
    public void tearDown() {
        customerDAO.deleteDatabase();
    }
}
