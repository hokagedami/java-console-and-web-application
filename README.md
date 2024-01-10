## Java Console Food Product Application
### Description
This is a simple Java console application. The application allows the user to view a list of food products, add a new product, edit an existing product, delete a product, and search for a product by description. The application uses SQLite to store the data. The application also has a simple web interface that allows the user to view the list of products and search for a product by description. Admin user can log in to edit/delete a product from the list.
It also allows to view list of customers, add a new customer, edit an existing customer, delete a customer, and search for a customer by name. The application uses SQLite to store the data. The application also has a simple web interface that allows the user to view the list of customers and search for a customer by name. Admin user can log in to edit/delete a customer from the list.
The application has a simple menu that allows the user to select an option. The user can select an option by entering the number of the option. The application will continue to run until the user selects the exit option.

The application also has a simple web interface that allows the user to view the list of products, search/filter for a product by description or category and add/remote item to cart. Admin user can log in to add, edit and delete a product from the list.

### Project Structure
The project has the following structure:
```
├───lib
|   ├───hamcrest-core-1.3.jar
|   ├───hamcrest-core-1.3-javadoc.jar
|   ├───hamcrest-core-1.3-sources.jar
|   ├───junit-4.13.1.jar
|   ├───junit-4.13.1-javadoc.jar
|   ├───junit-4.13.1-sources.jar
|   ├───slf4j-api-2.0.9.jar
|   ├───slf4j-nop-2.0.9.jar
|   └───sqlite-jdbc-3.43.2.1.jar
├───src
│   ├───database
|   │   ├───Migration.java
│   ├───methods
│   │   ├───customers
│   │   │   ├───CustomerConsoleHelper.java
│   │   │   └───CustomerDAO.java
│   │   ├───products
│   │   │   ├───ProductConsoleHelper.java
│   │   │   └───ProductDAO.java
│   │   └───session
│   │       └───SessionDAO.java
│   ├───models
│   │   ├───Address.java
│   │   ├───Customer.java
│   │   ├───CustomerToUpdate.java
│   │   ├───Product.java
│   │   ├───ProductToUpdate.java
│   │   └───Session.java
│   │   └───User.java
│   ├───server
│   │   ├───handlers
│   │   │   ├───AddCustomerHandler.java
│   │   │   ├───AddProductHandler.java
│   │   │   ├───AddToCartHandler.java
│   │   │   ├───AdminHandler.java
│   │   │   ├───BootstrapHandler.java
│   │   │   ├───CartHTMLHandler.java
│   │   │   ├───CustomersHTMLHandler.java
│   │   │   ├───DeleteCustomerHandler.java
│   │   │   ├───DeleteFromCartHandler.java
│   │   │   ├───DeleteProductHandler.java
│   │   │   ├───EditCustomerHTMLHandler.java
│   │   │   ├───EditProductHTMLHandler.java
│   │   │   ├───NewCustomerHTMLHandler.java
│   │   │   ├───NewProductHTMLHandler.java
│   │   │   ├───NotFoundHandler.java
│   │   │   ├───ProductsHTMLHandler.java
│   │   │   ├───StaticResourceHandler.java
│   │   │   ├───UpdateCustomerHandler.java
│   │   │   └───UpdateProductHandler.java
│   │   └───methods
│   │       ├───HandlerHelpers.java
│   │       └───HtmlHelpers.java
│   ├───Server.java
│   ├───static
│   │   └───bootstrap
│   │       ├───css
│   │       └───js
│   └───tests
│       ├───CustomerTest.java
│       └───ProductTest.java
└───Main.java
```

# Libraries
The project uses the following libraries:
- [JUnit](https://junit.org/junit5/) - for unit testing
- [SQLite JDBC](https://github.com/xerial/sqlite-jdbc/releases) - for database connection
- [SLF4J](http://www.slf4j.org/) - for logging
- [Hamcrest](http://hamcrest.org/JavaHamcrest/) - for unit testing
- [Bootstrap](https://getbootstrap.com/) - for web interface
- [jQuery](https://jquery.com/) - for web interface


### How to run
1. Clone the repository
2. Open the project in IntelliJ IDEA or preferred IDE
3. Run the project