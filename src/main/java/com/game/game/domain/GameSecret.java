package com.game.game.domain;

public class GameSecret {
    private String gameId;
    private String secret;
    private String signKey;
    private String gameSecret;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getGameSecret() {
        return gameSecret;
    }

    public void setGameSecret(String gameSecret) {
        this.gameSecret = gameSecret;
    }
}
