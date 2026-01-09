package cn.edu.cqust.easymenu;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 【本地SQLite数据库】SQLite数据库帮助类
 *
 * 【功能说明】
 * 负责SQLite数据库的创建、升级和初始化数据
 *
 * 【设计要点-本地SQLite数据库】
 * 数据库名：EasyMenu.db
 * 数据库版本：2
 *
 * 【数据表结构】
 * 1. users表：存储用户信息（user_id, username, password, login_status, created_at）
 * 2. menus表：存储菜单信息（menu_id, name, category, price, description, created_at）
 * 3. login_history表：存储登录历史（history_id, username, login_time, device_info）
 *
 * 【设计要点-初始数据】
 * - 菜单表初始化时插入10条以上的菜单记录
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /** 【本地SQLite数据库】数据库名称 */
    public static final String DB_NAME = "EasyMenu.db";
    /** 【本地SQLite数据库】数据库版本 */
    public static final int DB_VERSION = 2; // 版本升级到2

    // ================== 建表 SQL ==================

    // 【本地SQLite数据库】1. 用户表 users
    private static final String SQL_CREATE_USERS =
            "CREATE TABLE users (\n" +
                    "    user_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    username TEXT UNIQUE,\n" +
                    "    password TEXT,\n" +
                    "    login_status INTEGER DEFAULT 0,\n" +
                    "    created_at DATETIME DEFAULT CURRENT_TIMESTAMP\n" +
                    ");";

    // 【本地SQLite数据库】2. 菜单表 menus（包含4个主要字段：name, category, price, description）
    private static final String SQL_CREATE_MENUS =
            "CREATE TABLE menus (\n" +
                    "    menu_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name TEXT,\n" +
                    "    category TEXT,\n" +
                    "    price REAL,\n" +
                    "    description TEXT,\n" +
                    "    created_at DATETIME DEFAULT CURRENT_TIMESTAMP\n" +
                    ");";

    // 【本地SQLite数据库】3. 登录历史表 login_history（用于记录曾登录过的用户名）
    private static final String SQL_CREATE_LOGIN_HISTORY =
            "CREATE TABLE login_history (\n" +
                    "    history_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    username TEXT,\n" +
                    "    login_time DATETIME,\n" +
                    "    device_info TEXT\n" +
                    ");";

    // 创建索引提升查询性能
    private static final String SQL_CREATE_INDEX_MENUS_NAME =
            "CREATE INDEX IF NOT EXISTS idx_menus_name ON menus(name)";
    private static final String SQL_CREATE_INDEX_MENUS_CATEGORY =
            "CREATE INDEX IF NOT EXISTS idx_menus_category ON menus(category)";
    private static final String SQL_CREATE_INDEX_LOGIN_HISTORY_USERNAME =
            "CREATE INDEX IF NOT EXISTS idx_login_history_username ON login_history(username)";

    // ================================================================

    /** 单例实例，使用volatile保证可见性 */
    private static volatile DatabaseHelper instance;

    /**
     * 获取DatabaseHelper单例实例（线程安全）
     * @param context 上下文
     * @return DatabaseHelper实例
     */
    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        initData(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS);
        db.execSQL(SQL_CREATE_MENUS);
        db.execSQL(SQL_CREATE_LOGIN_HISTORY);

        // 创建索引
        db.execSQL(SQL_CREATE_INDEX_MENUS_NAME);
        db.execSQL(SQL_CREATE_INDEX_MENUS_CATEGORY);
        db.execSQL(SQL_CREATE_INDEX_LOGIN_HISTORY_USERNAME);
    }

    /**
     * 【设计要点-初始数据】初始化菜单数据
     * 插入10条以上的菜单记录，确保数据量满足要求
     * @param db 数据库实例
     */
    private void initData(SQLiteDatabase db) {
        String sql = "INSERT INTO menus (name, category, price, description) VALUES (?, ?, ?, ?)";

        // 【设计要点-记录不少于10条】插入10条初始菜单数据
        db.execSQL(sql, new Object[]{"宫保鸡丁", "热菜", 38.0, "经典川菜，酸甜微辣，鸡肉嫩滑"});
        db.execSQL(sql, new Object[]{"鱼香肉丝", "热菜", 28.0, "酸甜可口，下饭神器"});
        db.execSQL(sql, new Object[]{"麻婆豆腐", "热菜", 18.0, "麻辣鲜香，口感顺滑"});
        db.execSQL(sql, new Object[]{"回锅肉", "热菜", 35.0, "肥而不腻，蒜苗提味"});
        db.execSQL(sql, new Object[]{"水煮牛肉", "热菜", 48.0, "麻辣味厚，滑嫩适口，分量足"});
        db.execSQL(sql, new Object[]{"糖醋排骨", "热菜", 45.0, "酸甜适中，色泽红亮，老少皆宜"});
        db.execSQL(sql, new Object[]{"清炒时蔬", "素菜", 16.0, "时令蔬菜，清淡健康，解腻"});
        db.execSQL(sql, new Object[]{"番茄鸡蛋汤", "汤类", 12.0, "家常靓汤，营养丰富"});
        db.execSQL(sql, new Object[]{"扬州炒饭", "主食", 22.0, "粒粒分明，配料丰富"});
        db.execSQL(sql, new Object[]{"担担面", "主食", 10.0, "面条劲道，卤汁酥香"});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 实现增量升级，保留用户数据
        if (oldVersion < 2) {
            // 版本1 -> 2: 添加created_at字段和索引
            upgradeToVersion2(db);
        }
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            try {
                db.execSQL("ALTER TABLE users ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP");
            } catch (Exception e) {
            }

            try {
                db.execSQL("ALTER TABLE menus ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP");
            } catch (Exception e) {
            }

            // 添加login_history表的device_info字段
            try {
                db.execSQL("ALTER TABLE login_history ADD COLUMN device_info TEXT");
            } catch (Exception e) {
            }

            // 创建索引
            db.execSQL(SQL_CREATE_INDEX_MENUS_NAME);
            db.execSQL(SQL_CREATE_INDEX_MENUS_CATEGORY);
            db.execSQL(SQL_CREATE_INDEX_LOGIN_HISTORY_USERNAME);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
