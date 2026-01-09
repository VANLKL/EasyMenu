package cn.edu.cqust.easymenu.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private int loginStatus;

    public User() {}

    public User(int userId, String username, String password, int loginStatus) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.loginStatus = loginStatus;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getLoginStatus() { return loginStatus; }
    public void setLoginStatus(int loginStatus) { this.loginStatus = loginStatus; }
}
