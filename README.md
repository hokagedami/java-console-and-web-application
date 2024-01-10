# Java Console Food Product Application
## Description
This is a simple Java application that has a console interface and a web interface. 
The application uses SQLite to store the data.

### Console Interface
The console application has a simple menu that allows the user to select an option.
The user can select an option by entering the corresponding key on the keyboard for the different options.
The application allows the user to view a list of food products, add a new product,
edit an existing product, delete a product, and search for a product by ID.
The application also allows the user to view a list of customers, add a new customer,
edit an existing customer, delete a customer, and search for a customer by ID.

### Web Interface
The application also has a simple web interface that allows the user to view the list of products,
search for a product by description, filter by product category and expiry date. In addition, 
the application allows the user to view the list of customers. Product items can be added and 
removed from the cart. The user can also view the cart.

### Admin Features (Web Interface)
Admin user can log in to add new product, edit/delete a product from the list, 
add new customer, edit/delete a customer from the list.

## Project Structure
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
│   ├───tests
│   │   ├───CustomerTest.java
│   │   └───ProductTest.java
│   └───Main.java
├───.gitignore
└───README.md
```

## Libraries
The project uses the following libraries:
- [JUnit](https://junit.org/junit5/) - for unit testing
- [SQLite JDBC](https://github.com/xerial/sqlite-jdbc/releases) - for database connection
- [SLF4J](http://www.slf4j.org/) - for logging
- [Hamcrest](http://hamcrest.org/JavaHamcrest/) - for unit testing
- [Bootstrap](https://getbootstrap.com/) - for web interface
- [jQuery](https://jquery.com/) - for web interface

## Credentials
`
username: admin
password: admin
`

## How to run
1. Clone the repository
2. Open the project in IntelliJ IDEA or preferred IDE
3. Run the project

***N:B: The application will continue to run until the user selects the exit option.
Exiting the console using the assigned key will delete the database and stop the
web server serving the web interface. The application uses a new database on every run
i.e. database is deleted and recreated.***