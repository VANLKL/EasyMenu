package cn.edu.cqust.easymenu.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cqust.easymenu.DatabaseHelper;

/**
 * 【本地SQLite数据库-Model层】登录历史数据访问对象
 *
 * 【功能说明】
 * 负责登录历史数据的记录和查询
 *
 * 【设计要点-本地SQLite数据库】
 * - 使用SQLite数据库存储登录历史（login_history表）
 * - 记录每次登录的用户名和登录时间
 * - 支持查询所有曾登录过的用户名（去重，按最近登录时间排序）
 * - 用于在登录页面显示历史登录用户名，方便用户快速选择
 */
public class LoginHistoryDao {

    /** 【本地SQLite数据库】数据库帮助类实例 */
    private final DatabaseHelper dbHelper;

    /**
     * 构造函数，获取数据库帮助类实例
     * @param context 上下文
     */
    public LoginHistoryDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * 【登录功能-记录历史】插入登录历史记录
     * @param username 用户名
     * @param loginTime 登录时间
     * @return 插入的行ID，失败返回-1
     */
    public long insertHistory(String username, String loginTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("login_time", loginTime);
        return db.insert("login_history", null, cv);
    }

    /**
     * 【历史登录功能】获取曾登录过的用户名列表
     * 去重，按最近登录时间倒序排列
     * @return 用户名列表
     */
    public List<String> getDistinctUsernames() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        try (Cursor c = db.rawQuery(
                "SELECT username, MAX(login_time) AS last_time " +
                "FROM login_history " +
                "WHERE username IS NOT NULL AND username <> ? " +
                "GROUP BY username " +
                "ORDER BY last_time DESC",
                new String[]{""}
        )) {
            while (c.moveToNext()) {
                String username = c.getString(0);
                if (username != null && !username.trim().isEmpty()) {
                    list.add(username);
                }
            }
        }
        return list;
    }
}
