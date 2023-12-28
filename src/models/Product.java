package models;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Random;

public class Product {

    private final Random random = new Random();
    int id;
    String SKU; //stock keeping unit (a unique code for each product)
    String description;
    String category;
    int price;
    Date expiryDate;

    public Product(String description, String category, int price, Date expiryDate) {
        this.description = description;
        this.category = category;
        this.price = price;
        this.SKU = generateSKU();
        this.expiryDate = expiryDate;
    }


    public Product(int id, String SKU, String description, String category, int price, Date expiryDate) {
        this.id = id;
        this.SKU = SKU;
        this.description = description;
        this.category = category;
        this.price = price;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getPrice() {
        return price;
    }

    public String getSKU() {
        return SKU;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    private String generateSKU() {

        int randomNum = random.nextInt(90000) + 10000;

        LocalDate now = LocalDate.now();
        String year = now.getYear() + "";
        String month = now.getMonthValue() + "";
        if (month.length() == 1) {
            month = "0" + month;
        }
        return "SKU-" + year + month + "-" + randomNum;
    }
}
