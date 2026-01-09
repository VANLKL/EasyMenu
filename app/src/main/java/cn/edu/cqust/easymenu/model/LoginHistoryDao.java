package cn.edu.cqust.easymenu.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cqust.easymenu.DatabaseHelper;

public class LoginHistoryDao {

    private final DatabaseHelper dbHelper;

    public LoginHistoryDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insertHistory(String username, String loginTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("login_time", loginTime);
        return db.insert("login_history", null, cv);
    }

    //获取曾登录过的用户名（去重，按最近登录时间倒序）

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
