package com.game.share.domain;

import com.game.home.domain.CommParam;

public class ShareRVO extends CommParam {
    private String appId;
    private String gameId;
    private int type;
    private int score;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getGameId() {
        return gameId;
    }

    public int getType() {
        return type;
    }

    public int getScore() {
        return score;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
