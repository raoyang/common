package com.game.game.domain;

import java.util.Map;

public class GameVersion {
    private int id;
    private int versionCode;
    private int status;
    private int playerNum;
    private int cornerPos;
    private int cornerType;
    private String cornerUrl = "";
    private String shieldCountry;
    private String specifyCountry;
    private String shieldPhoneType;
    private String specifyPhoneType;
    private String accountIds;
    private int testNum;
    private Map<String, GameLan> gLan;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public int getStatus() {
        return status;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getCornerPos() {
        return cornerPos;
    }

    public String getCornerUrl() {
        return cornerUrl;
    }

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

    public int getTestNum() {
        return testNum;
    }

    public Map<String, GameLan> getgLan() {
        return gLan;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public void setCornerPos(int cornerPos) {
        this.cornerPos = cornerPos;
    }

    public void setCornerUrl(String cornerUrl) {
        this.cornerUrl = cornerUrl;
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

    public void setTestNum(int testNum) {
        this.testNum = testNum;
    }

    public void setgLan(Map<String, GameLan> gLan) {
        this.gLan = gLan;
    }

    public int getCornerType() {
        return cornerType;
    }

    public void setCornerType(int cornerType) {
        this.cornerType = cornerType;
    }
}
