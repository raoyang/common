package com.game.favogame.domain;

import java.sql.Timestamp;

public class AccountGameRecord {

    private int id;

    private int accountId;

    private String gameId;

    private int count;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public static AccountGameRecord valueOf(int accountId, String gameId, int count){
        AccountGameRecord record = new AccountGameRecord();
        record.accountId = accountId;
        record.gameId = gameId;
        record.count = count;
        return record;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getGameId() { return gameId; }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
