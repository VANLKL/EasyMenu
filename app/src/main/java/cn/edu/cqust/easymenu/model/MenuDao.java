package cn.edu.cqust.easymenu.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cqust.easymenu.DatabaseHelper;

public class MenuDao {

    private final DatabaseHelper dbHelper;

    public MenuDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insertMenu(Menu menu) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", menu.getName());
        cv.put("category", menu.getCategory());
        cv.put("price", menu.getPrice());
        cv.put("description", menu.getDescription());
        return db.insert("menus", null, cv);
    }

    public int updateMenu(Menu menu) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", menu.getName());
        cv.put("category", menu.getCategory());
        cv.put("price", menu.getPrice());
        cv.put("description", menu.getDescription());
        return db.update("menus", cv, "menu_id=?", new String[]{String.valueOf(menu.getMenuId())});
    }

    public int deleteMenuById(int menuId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("menus", "menu_id=?", new String[]{String.valueOf(menuId)});
    }

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
