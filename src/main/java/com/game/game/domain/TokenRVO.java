package com.game.game.domain;

import com.game.home.domain.CommParam;

public class TokenRVO extends CommParam{
    private String gameId;

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
