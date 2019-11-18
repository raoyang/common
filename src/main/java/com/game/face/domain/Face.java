package com.game.face.domain;

public class Face {
    private int id;
    private String name;
    private String url;
    private int property;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }
}
