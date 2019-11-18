package com.game.login.domain;

public class AppAccountLogin {
    private int id;
    private int appId;
    private int accountId;
    private String loginTime;
    private String expireTime;

    public int getId() {
        return id;
    }

    public int getAppId() {
        return appId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
