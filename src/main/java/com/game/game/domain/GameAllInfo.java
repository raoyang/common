package com.game.game.domain;

import com.game.ad.domain.AdConf;

import java.util.List;

public class GameAllInfo {
    private int id;
    private String gameId;
    private int gameStyle;
    private int gameType;
    private int runType;
    private int orientation;
    private int rankType;
    private int microphone;
    private int memLimit;
    private int appVerLimit;
    private String gameSecret;
    private List<AdConf> ad;
    private List<GameVersion> versions;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getGameId() {
        return gameId;
    }

    public int getGameType() {
        return gameType;
    }

    public int getRunType() {
        return runType;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getRankType() {
        return rankType;
    }

    public List<GameVersion> getVersions() {
        return versions;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public void setRunType(int runType) {
        this.runType = runType;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setRankType(int rankType) {
        this.rankType = rankType;
    }

    public void setVersions(List<GameVersion> versions) {
        this.versions = versions;
    }

    public int getGameStyle() {
        return gameStyle;
    }

    public void setGameStyle(int gameStyle) {
        this.gameStyle = gameStyle;
    }

    public void setMicrophone(int microphone) {
        this.microphone = microphone;
    }

    public int getMicrophone() {
        return microphone;
    }

    public int getMemLimit() {
        return memLimit;
    }

    public void setMemLimit(int memLimit) {
        this.memLimit = memLimit;
    }

    public void setAppVerLimit(int appVerLimit) {
        this.appVerLimit = appVerLimit;
    }

    public int getAppVerLimit() {
        return appVerLimit;
    }

    public List<AdConf> getAd() {
        return ad;
    }

    public void setAd(List<AdConf> ad) {
        this.ad = ad;
    }

    public String getGameSecret() {
        return gameSecret;
    }

    public void setGameSecret(String gameSecret) {
        this.gameSecret = gameSecret;
    }
}
