package com.game.home.domain;

public class HomeRule extends Rule {
    private String albumId;
    private String title;
    private int numLimit;
    private int more;

    public String getAlbumId() {
        return albumId;
    }

    public String getTitle() {
        return title;
    }

    public int getNumLimit() {
        return numLimit;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNumLimit(int numLimit) {
        this.numLimit = numLimit;
    }

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }
}
