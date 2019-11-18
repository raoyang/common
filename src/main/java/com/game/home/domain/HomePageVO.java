package com.game.home.domain;

public class HomePageVO extends CommParam {
    private int page;
    private int limit;

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
