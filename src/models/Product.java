package models;

import java.time.LocalDate;
import java.util.Random;

public class Product {

    private final Random random = new Random();
    int id;
    String SKU; //stock keeping unit (a unique code for each product)
    String description;
    String category;
    int price;

    public Product(String description, String category, int price) {
        this.description = description;
        this.category = category;
        this.price = price;
        this.SKU = generateSKU();
    }


    public Product(int id, String SKU, String description, String category, int price) {
        this.id = id;
        this.SKU = SKU;
        this.description = description;
        this.category = category;
        this.price = price;
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
