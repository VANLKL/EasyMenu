package cn.edu.cqust.easymenu.model;

/**
 * 【本地SQLite数据库-Model层】菜单实体类
 *
 * 【功能说明】
 * 菜单数据的实体类，对应数据库menus表
 *
 * 【设计要点-菜单信息字段】
 * 菜单信息字段（不少于4个）：
 * - menuId：菜单ID（主键）
 * - name：菜名
 * - category：分类
 * - price：价格
 * - description：描述
 */
public class Menu {
    /** 菜单ID（主键） */
    private int menuId;
    /** 菜名 */
    private String name;
    /** 分类 */
    private String category;
    /** 价格 */
    private double price;
    /** 描述 */
    private String description;

    /** 无参构造函数 */
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
