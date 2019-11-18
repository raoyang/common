package com.game.record.domain;

public class GameRankingRecord {
    private String gameId;
    private int accountId;
    private int score;

    public String getGameId() {
        return gameId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getScore() {
        return score;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
