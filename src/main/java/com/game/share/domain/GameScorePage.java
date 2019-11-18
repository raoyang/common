package com.game.share.domain;

public class GameScorePage extends ScorePage {
    private String gameIcon;
    private String gameName;

    public String getGameIcon() {
        return gameIcon;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
