package cn.edu.cqust.easymenu.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cqust.easymenu.DatabaseHelper;

/**
 * 【本地SQLite数据库-Model层】菜单数据访问对象
 *
 * 【功能说明】
 * 负责菜单数据的CRUD（增删改查）操作，所有数据库操作都通过此类进行
 *
 * 【设计要点-本地SQLite数据库】
 * - 使用SQLite数据库存储菜单信息（menus表）
 * - 菜单字段包含：menu_id（主键）, name（菜名）, category（分类）, price（价格）, description（描述）
 * - 初始数据包含10条以上的菜单记录
 * - 支持批量删除操作
 * - 支持按菜名和分类搜索
 */
public class MenuDao {

    /** 【本地SQLite数据库】数据库帮助类实例 */
    private final DatabaseHelper dbHelper;

    /**
     * 构造函数，获取数据库帮助类实例
     * @param context 上下文
     */
    public MenuDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * 【菜单添加】插入新的菜单项
     * @param menu 菜单对象
     * @return 插入的行ID，失败返回-1
     */
    public long insertMenu(Menu menu) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", menu.getName());
        cv.put("category", menu.getCategory());
        cv.put("price", menu.getPrice());
        cv.put("description", menu.getDescription());
        return db.insert("menus", null, cv);
    }

    /**
     * 【菜单修改】更新已有的菜单项
     * @param menu 菜单对象
     * @return 更新的行数
     */
    public int updateMenu(Menu menu) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", menu.getName());
        cv.put("category", menu.getCategory());
        cv.put("price", menu.getPrice());
        cv.put("description", menu.getDescription());
        return db.update("menus", cv, "menu_id=?", new String[]{String.valueOf(menu.getMenuId())});
    }

    /**
     * 【菜单删除】根据ID删除菜单项
     * @param menuId 菜单ID
     * @return 删除的行数
     */
    public int deleteMenuById(int menuId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("menus", "menu_id=?", new String[]{String.valueOf(menuId)});
    }

    /**
     * 根据ID查询菜单
     * @param menuId 菜单ID
     * @return 菜单对象，不存在返回null
     */
    public Menu getMenuById(int menuId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor c = db.query(
                "menus",
                new String[]{"menu_id", "name", "category", "price", "description"},
                "menu_id=?",
                new String[]{String.valueOf(menuId)},
                null, null, null
        )) {
            if (c.moveToFirst()) {
                Menu m = new Menu();
                m.setMenuId(c.getInt(0));
                m.setName(c.getString(1));
                m.setCategory(c.getString(2));
                m.setPrice(c.getDouble(3));
                m.setDescription(c.getString(4));
                return m;
            }
        }
        return null;
    }

    /**
     * 查询所有菜单（按ID降序排列）
     * @return 菜单列表
     */
    public List<Menu> getAllMenus() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Menu> list = new ArrayList<>();
        try (Cursor c = db.query(
                "menus",
                new String[]{"menu_id", "name", "category", "price", "description"},
                null, null, null, null,
                "menu_id DESC"
        )) {
            while (c.moveToNext()) {
                Menu m = new Menu();
                m.setMenuId(c.getInt(0));
                m.setName(c.getString(1));
                m.setCategory(c.getString(2));
                m.setPrice(c.getDouble(3));
                m.setDescription(c.getString(4));
                list.add(m);
            }
        }
        return list;
    }

    /**
     * 按关键词搜索菜单（支持菜名和分类搜索）
     * @param keyword 搜索关键词
     * @return 匹配的菜单列表
     */
    public List<Menu> searchMenus(String keyword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Menu> list = new ArrayList<>();
        try (Cursor c = db.rawQuery(
                "SELECT menu_id, name, category, price, description " +
                "FROM menus " +
                "WHERE name LIKE ? OR category LIKE ? " +
                "ORDER BY menu_id DESC",
                new String[]{"%" + keyword + "%", "%" + keyword + "%"}
        )) {
            while (c.moveToNext()) {
                Menu m = new Menu();
                m.setMenuId(c.getInt(0));
                m.setName(c.getString(1));
                m.setCategory(c.getString(2));
                m.setPrice(c.getDouble(3));
                m.setDescription(c.getString(4));
                list.add(m);
            }
        }
        return list;
    }

    /**
     * 【菜单删除-批量删除】批量删除多个菜单项
     * @param menuIds 菜单ID列表
     * @return 删除的总行数
     */
    public int batchDeleteMenus(List<Integer> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return 0;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int totalDeleted = 0;

        db.beginTransaction();
        try {
            for (Integer id : menuIds) {
                totalDeleted += db.delete("menus", "menu_id=?",
                        new String[]{String.valueOf(id)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return totalDeleted;
    }
}
