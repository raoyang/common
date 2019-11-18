package com.game.home.domain;

public class Home {
    private int id;
    private String title;
    private int numLimit;
    private String resourceId;
    private int asort;
    private int status;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getNumLimit() {
        return numLimit;
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getAsort() {
        return asort;
    }

    public int getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNumLimit(int numLimit) {
        this.numLimit = numLimit;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setAsort(int asort) {
        this.asort = asort;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
