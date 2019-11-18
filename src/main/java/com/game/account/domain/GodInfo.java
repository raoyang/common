package com.game.account.domain;

public class GodInfo {
    private int accountId;
    private double winPer;
    private int totalCnt;
    private int godType;
    private int type;

    public int getAccountId() {
        return accountId;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public int getGodType() {
        return godType;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public void setGodType(int godType) {
        this.godType = godType;
    }

    public double getWinPer() {
        return winPer;
    }

    public void setWinPer(double winPer) {
        this.winPer = winPer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
