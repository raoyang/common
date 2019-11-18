package com.game.game.domain;

public class GameRule {
    private String shieldCountry;
    private String specifyCountry;
    private String shieldPhoneType;
    private String specifyPhoneType;
    private String accountIds;
    private int testNum;

    public String getShieldCountry() {
        return shieldCountry;
    }

    public String getSpecifyCountry() {
        return specifyCountry;
    }

    public String getShieldPhoneType() {
        return shieldPhoneType;
    }

    public String getSpecifyPhoneType() {
        return specifyPhoneType;
    }

    public String getAccountIds() {
        return accountIds;
    }

    public void setShieldCountry(String shieldCountry) {
        this.shieldCountry = shieldCountry;
    }

    public void setSpecifyCountry(String specifyCountry) {
        this.specifyCountry = specifyCountry;
    }

    public void setShieldPhoneType(String shieldPhoneType) {
        this.shieldPhoneType = shieldPhoneType;
    }

    public void setSpecifyPhoneType(String specifyPhoneType) {
        this.specifyPhoneType = specifyPhoneType;
    }

    public void setAccountIds(String accountIds) {
        this.accountIds = accountIds;
    }

    public int getTestNum() {
        return testNum;
    }

    public void setTestNum(int testNum) {
        this.testNum = testNum;
    }
}
