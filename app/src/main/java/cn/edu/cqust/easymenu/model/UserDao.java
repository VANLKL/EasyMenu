package cn.edu.cqust.easymenu.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.edu.cqust.easymenu.DatabaseHelper;

/**
 * 【本地SQLite数据库-Model层】用户数据访问对象
 *
 * 【功能说明】
 * 负责用户数据的CRUD（增删改查）操作，所有数据库操作都通过此类进行
 *
 * 【设计要点-本地SQLite数据库】
 * - 使用SQLite数据库存储用户信息（users表）
 * - 用户字段包含：user_id（主键）, username（用户名）, password（加密密码）, login_status（登录状态）
 * - 密码经过加密后存储
 * - 支持登录状态管理（login_status字段：0未登录，1已登录）
 * - 支持查询当前登录用户
 */
public class UserDao {

    /** 【本地SQLite数据库】数据库帮助类实例 */
    private final DatabaseHelper dbHelper;

    /**
     * 构造函数，获取数据库帮助类实例
     * @param context 上下文
     */
    public UserDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return true表示已存在，false表示不存在
     */
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor c = db.query(
                "users",
                new String[]{"user_id"},
                "username=?",
                new String[]{username},
                null, null, null
        )) {
            return c.moveToFirst();
        }
    }

    /**
     * 【注册功能】插入新用户
     * @param username 用户名
     * @param storedPasswordSaltColonHash 加密后的密码
     * @return 插入的行ID，失败返回-1
     */
    public long insertUser(String username, String storedPasswordSaltColonHash) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", storedPasswordSaltColonHash);
        cv.put("login_status", 0); // 默认未登录
        return db.insert("users", null, cv);
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    public User getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor c = db.query(
                "users",
                new String[]{"user_id", "username", "password", "login_status"},
                "username=?",
                new String[]{username},
                null, null, null
        )) {
            if (c.moveToFirst()) {
                User u = new User();
                u.setUserId(c.getInt(0));
                u.setUsername(c.getString(1));
                u.setPassword(c.getString(2));
                u.setLoginStatus(c.getInt(3));
                return u;
            }
        }
        return null;
    }

    /**
     * 【登录功能-登录成功】设置指定用户为登录状态
     * 同时将其他用户的登录状态设置为0（确保同一时间只有一个用户处于登录状态）
     * @param username 用户名
     */
    public void setLoggedInUser(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv0 = new ContentValues();
            cv0.put("login_status", 0);
            db.update("users", cv0, null, null);

            ContentValues cv1 = new ContentValues();
            cv1.put("login_status", 1);
            db.update("users", cv1, "username=?", new String[]{username});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 【退出登录】设置指定用户为未登录状态
     * @param username 用户名
     */
    public void setLogout(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("login_status", 0);
        db.update("users", cv, "username=?", new String[]{username});
    }

    /**
     * 【闪屏功能】查询当前登录的用户（login_status=1的用户）
     * @return 当前登录用户，不存在返回null
     */
    public User getLoggedInUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor c = db.query(
                "users",
                new String[]{"user_id", "username", "password", "login_status"},
                "login_status=?",
                new String[]{"1"},
                null, null,
                "user_id DESC",
                "1"
        )) {
            if (c.moveToFirst()) {
                User u = new User();
                u.setUserId(c.getInt(0));
                u.setUsername(c.getString(1));
                u.setPassword(c.getString(2));
                u.setLoginStatus(c.getInt(3));
                return u;
            }
        }
        return null;
    }
}
