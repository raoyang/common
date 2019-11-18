package com.game.friend.domain;

import java.sql.Timestamp;

public class BlackData {

    private int accountId;

    private Timestamp time;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
