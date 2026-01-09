package cn.edu.cqust.easymenu.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.edu.cqust.easymenu.DatabaseHelper;

public class UserDao {

    private final DatabaseHelper dbHelper;

    public UserDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

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

    public long insertUser(String username, String storedPasswordSaltColonHash) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", storedPasswordSaltColonHash);
        cv.put("login_status", 0);
        return db.insert("users", null, cv);
    }

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

    public void setLogout(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("login_status", 0);
        db.update("users", cv, "username=?", new String[]{username});
    }

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
