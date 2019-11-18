package com.game.record.domain;

public class TopGameRecord {
    private int id;
    private String gameId;
    private int count;

    public int getId() {
        return id;
    }

    public String getGameId() {
        return gameId;
    }

    public int getCount() {
        return count;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
