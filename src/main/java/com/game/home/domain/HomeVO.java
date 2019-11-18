package com.game.home.domain;

import java.util.List;

public class HomeVO {
    private String id;
    private String title;
    private int more;
    private List<HomeCard> games;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<HomeCard> getGames() {
        return games;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGames(List<HomeCard> games) {
        this.games = games;
    }

    public void setMore(int more) {
        this.more = more;
    }

    public int getMore() {
        return more;
    }
}
