package com.game.ranking.domain;

import com.game.home.domain.CommParam;

public class RankingRVO extends CommParam {
    private String gameId;
    private int type;
    private int category;
    private int page;
    private int limit;

    public String getGameId() {
        return gameId;
    }

    public int getType() {
        return type;
    }

    public int getCategory() {
        return category;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
