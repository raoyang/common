package com.game.banner.domain;

import com.game.game.domain.GameVO;

public class BannerVO {
    private String name;
    private String icon;
    private int linkType;
    private String linkTo;
    private GameVO gameInfo;

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getLinkType() {
        return linkType;
    }

    public String getLinkTo() {
        return linkTo;
    }

    public GameVO getGameInfo() {
        return gameInfo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public void setLinkTo(String linkTo) {
        this.linkTo = linkTo;
    }

    public void setGameInfo(GameVO gameInfo) {
        this.gameInfo = gameInfo;
    }
}
