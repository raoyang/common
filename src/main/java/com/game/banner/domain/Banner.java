package com.game.banner.domain;

import com.game.home.domain.Rule;

public class Banner extends Rule{
    private int id;
    private String bannerId;
    private String name;
    private String icon;
    private int linkType;
    private String linkTo;
    private int status;
    private String albumId;
    private int albumPos;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getLinkType() {
        return linkType;
    }

    public String getLinkTo() {
        return linkTo;
    }

    public int getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public void setLinkTo(String linkTo) {
        this.linkTo = linkTo;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAlbumId() {
        return albumId;
    }

    public int getAlbumPos() {
        return albumPos;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public void setAlbumPos(int albumPos) {
        this.albumPos = albumPos;
    }
}
