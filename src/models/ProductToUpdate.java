package models;

public class ProductToUpdate {
    private String description;
    private String category;
    private int price;

    public ProductToUpdate(String description, String category, int price) {
        this.description = description;
        this.category = category;
        this.price = price;
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

}
