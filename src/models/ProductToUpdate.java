package models;

import java.sql.Date;

public class ProductToUpdate {
    private String description;
    private String category;
    private int price;
    private Date expiryDate;

    public ProductToUpdate(String description, String category, int price, Date expiryDate) {
        this.description = description;
        this.category = category;
        this.price = price;
        this.expiryDate = expiryDate;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
