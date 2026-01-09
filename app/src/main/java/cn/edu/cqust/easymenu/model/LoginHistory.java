package cn.edu.cqust.easymenu.model;

public class LoginHistory {
    private int historyId;
    private String username;
    private String loginTime;

    public LoginHistory() {}

    public LoginHistory(int historyId, String username, String loginTime) {
        this.historyId = historyId;
        this.username = username;
        this.loginTime = loginTime;
    }

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLoginTime() { return loginTime; }
    public void setLoginTime(String loginTime) { this.loginTime = loginTime; }
}
