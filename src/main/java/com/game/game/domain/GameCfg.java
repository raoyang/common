package com.game.game.domain;

import java.sql.Timestamp;

public class GameCfg {
    private String gameId;
    private int versionCode;
    private String shieldCountry;
    private String specifyCountry;
    private String shieldPhoneType;
    private String specifyPhoneType;
    private String accountIds;
    private int cronStatus;
    private Timestamp cronTime;
    private int testNum;
    private int cornerId;
    private String dataConfig;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getShieldCountry() {
        return shieldCountry;
    }

    public void setShieldCountry(String shieldCountry) {
        this.shieldCountry = shieldCountry;
    }

    public String getSpecifyCountry() {
        return specifyCountry;
    }

    public void setSpecifyCountry(String specifyCountry) {
        this.specifyCountry = specifyCountry;
    }

    public String getShieldPhoneType() {
        return shieldPhoneType;
    }

    public void setShieldPhoneType(String shieldPhoneType) {
        this.shieldPhoneType = shieldPhoneType;
    }

    public String getSpecifyPhoneType() {
        return specifyPhoneType;
    }

    public void setSpecifyPhoneType(String specifyPhoneType) {
        this.specifyPhoneType = specifyPhoneType;
    }

    public String getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(String accountIds) {
        this.accountIds = accountIds;
    }

    public int getCronStatus() {
        return cronStatus;
    }

    public void setCronStatus(int cronStatus) {
        this.cronStatus = cronStatus;
    }

    public Timestamp getCronTime() {
        return cronTime;
    }

    public void setCronTime(Timestamp cronTime) {
        this.cronTime = cronTime;
    }

    public int getTestNum() {
        return testNum;
    }

    public int getCornerId() {
        return cornerId;
    }

    public String getDataConfig() {
        return dataConfig;
    }

    public void setTestNum(int testNum) {
        this.testNum = testNum;
    }

    public void setCornerId(int cornerId) {
        this.cornerId = cornerId;
    }

    public void setDataConfig(String dataConfig) {
        this.dataConfig = dataConfig;
    }
}
