package database;

import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * This class is responsible for creating the database tables
 */
public class Migration {

    private String DATABASE_FILE_NAME = "db.sqlite";
    /**
     * Constructor
     */
    public Migration() {
        createTables();
    }

    public Migration(String databaseFileName) {
        DATABASE_FILE_NAME = databaseFileName;
        createTables();
    }

    /**
     * This method creates the database tables
     */

    private void createTables() {
        try {
            var conn = getConnection();
            System.out.println("Customers Table creation started");
            if (conn == null) {
                System.out.println("Database Connection is null");
                System.exit(0);
            }
            var dbMeta = conn.getMetaData();
            ResultSet customers = dbMeta.getTables(null, null,
                    "customers", null);
            if (!customers.next()) {
                // Table does not exist, create it
                String sql = "CREATE TABLE IF NOT EXISTS customers " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " businessName TEXT NOT NULL, telephone TEXT NOT NULL," +
                        " addressLine1 TEXT NOT NULL, addressLine2 TEXT NOT NULL," +
                        " addressLine3 TEXT NOT NULL, country TEXT NOT NULL, " +
                        "postCode TEXT NOT NULL)";

                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                System.out.println("Customers Table created successfully");
            }

            ResultSet products = dbMeta.getTables(null, null,
                    "food_products", null);
            if (!products.next()) {
                String sql = "CREATE TABLE IF NOT EXISTS food_products " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " description TEXT NOT NULL, category TEXT NOT NULL," +
                        " price INTEGER NOT NULL, sku TEXT NOT NULL UNIQUE," +
                        " expiry_date DATE NOT NULL)";

                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                System.out.println("Products Table created successfully");
            }

            ResultSet cart = dbMeta.getTables(null, null,
                    "sessions", null);
            if (!cart.next()) {
                String sql = "CREATE TABLE IF NOT EXISTS sessions " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " cart_items_ids TEXT NULL, session_id TEXT NOT NULL," +
                        " is_admin BOOLEAN NOT NULL DEFAULT FALSE, is_deleted BOOLEAN NOT NULL DEFAULT FALSE)";

                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                System.out.println("Sessions Table created successfully");
            }

            conn.close();
        } catch (SQLException e) {
            System.out.printf("Error creating database: %s%n", e.getMessage());
        }
    }

    /**
     * This method returns a database connection
     * @return Connection
     */
    public Connection getDbConnection(){
        return getConnection();
    }

    /**
     * This method deletes the database
     */
    public void deleteDatabase(){
        String tempDirectory = System.getProperty("java.io.tmpdir");
        String temporaryFilePath = tempDirectory + File.separator + DATABASE_FILE_NAME;
        deleteDatabaseFile(temporaryFilePath);
    }

    /**
     * This method creates a database connection
     * @return Connection
     */

    private Connection getConnection() {
        try {
            String tempDirectory = System.getProperty("java.io.tmpdir");
            String temporaryFilePath = tempDirectory + File.separator + DATABASE_FILE_NAME;
            createDatabaseFileIfNotExists(temporaryFilePath);
            return DriverManager.getConnection("jdbc:sqlite:" + temporaryFilePath);
        } catch (SQLException e) {
            System.out.println("Error connecting to database");
            return null;
        }
    }

    /**
     * This method creates a database file if it does not exist
     * @param filePath the path to the database file
     */
    private void createDatabaseFileIfNotExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                var created = file.createNewFile();
                if (created) {
                    System.out.println("Database file created successfully");
                } else {
                    System.out.println("Database file creation failed");
                }
            } catch (IOException e) {
                System.out.printf("Error creating database file: %s%n", e.getMessage());
            }
        }
    }

    private void deleteDatabaseFile(String filePath){
        File file = new File(filePath);
        if (file.exists()){
            var deleted = file.delete();
            if (deleted){
                System.out.println("Database file deleted successfully");
            }
            else {
                System.out.println("Database file deletion failed");
            }
        }
    }
}
