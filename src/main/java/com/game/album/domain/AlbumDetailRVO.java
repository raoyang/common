package com.game.album.domain;

import com.game.home.domain.CommParam;

public class AlbumDetailRVO extends CommParam{
    private String id;
    private int page;
    private int limit;

    public String getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
