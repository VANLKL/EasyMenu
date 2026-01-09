package cn.edu.cqust.easymenu.model;

public class Menu {
    private int menuId;
    private String name;
    private String category;
    private double price;
    private String description;

    public Menu() {}

    public Menu(int menuId, String name, String category, double price, String description) {
        this.menuId = menuId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
