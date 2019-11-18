package com.game.record.domain;

import com.game.home.domain.CommParam;

public class Record extends CommParam{
    private String gameId;
    private int event;
    private int result;
    private String roomId;
    private int rankingType;
    private int score;
    private int gVer;
    private String gLan;
    private int type;
    private String content;
    private String ext;
    private String adId;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getResult() {
        return result;
    }

    public String getRoomId() {
        return roomId;
    }


    public void setResult(int result) {
        this.result = result;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }


    public int getRankingType() {
        return rankingType;
    }

    public void setRankingType(int rankingType) {
        this.rankingType = rankingType;
    }

    public int getgVer() {
        return gVer;
    }

    public String getgLan() {
        return gLan;
    }

    public void setgLan(String gLan) {
        this.gLan = gLan;
    }

    public void setgVer(int gVer) {
        this.gVer = gVer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getExt() {
        return ext;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }
}
