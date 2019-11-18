package com.game.match.domain;

import com.game.chat.message.S2CContent;

public class BroadcastMessage extends S2CContent {
    private String gameId;
    private String gameName;

    public String getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
