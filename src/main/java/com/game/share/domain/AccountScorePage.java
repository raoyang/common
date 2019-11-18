package com.game.share.domain;

public class AccountScorePage extends ScorePage {
    private String nickName;
    private int accountId;
    private String avatar;

    public String getNickName() {
        return nickName;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
