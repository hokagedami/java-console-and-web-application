package tests;

import database.Migration;
import methods.products.ProductDAO;
import models.Product;
import models.ProductToUpdate;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductTest {
    private ProductDAO productDAO;
    private final List<Product> products = new ArrayList<>(){
        {
            add(new Product("Test Product 1", "Test Category 1", 100, Date.valueOf("2021-12-12")));
            add(new Product("Test Product 2", "Test Category 2", 200, Date.valueOf("2021-12-12")));
            add(new Product("Test Product 3", "Test Category 1", 300, Date.valueOf("2021-12-12")));
            add(new Product("Test Product 4", "Test Category 2", 400, Date.valueOf("2021-12-12")));
            add(new Product("Test Product 5", "Test Category 1", 500, Date.valueOf("2021-12-12")));
        }
    };
    @Before
    public void setUp() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Migration migration = new Migration("test_db.sqlite");
        productDAO = new ProductDAO(migration);
        for (Product product : products) {
            productDAO.addProduct(product);
        }
    }
    @Test
    public void a_testFindAllProducts() {
        Assert.assertEquals(products.size(), productDAO.findAllProducts().size());
    }
    @Test
    public void b_testAddProduct() {
        String description = "Test Product";
        String category = "Test Category 3";
        int price = 100;
        productDAO.addProduct(new Product(description, category, price, Date.valueOf("2021-12-12")));
        Assert.assertEquals(products.size() + 1, productDAO.findAllProducts().size());
        Assert.assertEquals(description, productDAO.findAllProducts().getLast().getDescription());
    }
    @Test
    public void c_testUpdateProduct() {
        var product = productDAO.findProduct(1);
        String description = "Updated Test Product";
        String category = "Updated Test Category";
        int price = 1500;
        var updatedProduct = productDAO
                .updateProduct(new ProductToUpdate(description, category, price, Date.valueOf("2021-12-12")), product.getId());
        Assert.assertEquals(description, updatedProduct.getDescription());
        Assert.assertEquals(category, updatedProduct.getCategory());
        Assert.assertEquals(price, updatedProduct.getPrice());
        Assert.assertEquals(product.getSKU(), updatedProduct.getSKU());
    }
    @Test
    public void d_testDeleteProduct() {
        var product = productDAO.findProduct(1);
        productDAO.deleteProduct(product.getId());
        Assert.assertEquals(products.size() - 1, productDAO.findAllProducts().size());
    }
    @Test
    public void e_testFindProduct() {
        var product = productDAO.findProduct(1);
        Assert.assertEquals(products.getFirst().getDescription(), product.getDescription());
    }
    @Test
    public void f_testFindInvalidProduct() {
        var product = productDAO.findProduct(100);
        Assert.assertNull(product);
    }
    @Test
    public void g_testDeleteInvalidProduct() {
        Assert.assertFalse(productDAO.deleteProduct(100));
    }
    @Test
    public void h_testUpdateInvalidProduct() {
        Assert.assertNull(productDAO.updateProduct(new ProductToUpdate("Test", "Test", 100, Date.valueOf("2021-12-12")), 100));
    }
    @Test
    public void i_testDeleteValidProduct() {
        Assert.assertTrue(productDAO.deleteProduct(1));
    }
    @Test
    public void j_testFindProductsByDescription() {
        var products = productDAO.findProductsByDescription("Test Product");
        Assert.assertEquals(5, products.size());
    }
    @Test
    public void j_testFindProductsByInvalidDescription() {
        var products = productDAO.findProductsByDescription("Invalid Product");
        Assert.assertEquals(0, products.size());
    }
    @Test
    public void k_testFindProductsByCategory() {
        var products = productDAO
                .filterProductsByCategories(new ArrayList<>(List.of("Test Category 1", "Test Category 2")));
        Assert.assertEquals(5, products.size());
    }
    @Test
    public void k_testFindProductsByInvalidCategories() {
        var products = productDAO
                .filterProductsByCategories(new ArrayList<>(List.of("Invalid Category 1", "Invalid Category 2")));
        Assert.assertEquals(0, products.size());
    }
    @After
    public void tearDown() {
        productDAO.deleteDatabase();
    }
}
