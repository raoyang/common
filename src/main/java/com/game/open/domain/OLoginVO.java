package com.game.open.domain;

public class OLoginVO {
    private int accountId;
    private String gToken;

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getgToken() {
        return gToken;
    }

    public void setgToken(String gToken) {
        this.gToken = gToken;
    }
}
