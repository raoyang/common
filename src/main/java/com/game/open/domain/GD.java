package com.game.open.domain;

public class GD {
    private int type;
    private String gameId;
    private int version;
    private int score;

    public int getType() {
        return type;
    }

    public String getGameId() {
        return gameId;
    }

    public int getVersion() {
        return version;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
