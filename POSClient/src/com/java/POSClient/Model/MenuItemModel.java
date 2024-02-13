package com.java.POSClient.Model;

public class MenuItemModel {
    private String menuId;
    private String menuName;
    private String category;
    private int price;
    private String cookTime;
    private String productImage;
    private String description;

    // getters
    public String getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getCategory() {
        return category;
    }

    public int getPrice() {
        return price;
    }

    public String getCookTime() {
        return cookTime;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getDescription() {
        return description;
    }

    // setters
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}