package com.game.friend.domain;

import com.game.home.domain.CommParam;

public class AroundPlayerRVO extends CommParam{
    private int flush;
    private int page;
    private int limit;

    public int getFlush() {
        return flush;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public void setFlush(int flush) {
        this.flush = flush;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
