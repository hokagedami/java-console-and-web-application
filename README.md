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
├───src
│   ├───database
│   ├───methods
│   ├───models
│   ├───server
│   │   ├───handlers
│   │   └───methods
│   ├───static
│   │   └───bootstrap
│   │       ├───css
│   │       └───js
│   └───tests

lib - contains the SQLite JDBC driver and other dependencies
src - contains the source code
```

### How to run
1. Clone the repository
2. Open the project in IntelliJ IDEA or preferred IDE
3. Run the project