package com.game.home.domain;

import com.game.ad.domain.AdConf;
import com.game.game.domain.Game;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class HomeGameVO extends HomeCard{
    private int online;
    private String gameId;
    private String name;
    private String icon;
    private String bgIcon;
    private String url;
    private int gameType;
    private int runType;
    private String color;
    private String cornerUrl = "";
    private int cornerPos;
    private int cornerType;
    private String md5;
    private int orientation;
    private int versionCode;
    private int microphone;
    private List<AdConf> ad;
    private String gameSecret = "";

    public static HomeGameVO valueOf(Game game, int onlineNum) {
        HomeGameVO gameVO = new HomeGameVO();
        BeanUtils.copyProperties(game, gameVO);
        gameVO.setOnline(onlineNum);
        return gameVO;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getUrl() {
        return url;
    }

    public int getGameType() {
        return gameType;
    }

    public int getRunType() {
        return runType;
    }

    public String getColor() {
        return color;
    }

    public String getCornerUrl() {
        return cornerUrl;
    }

    public int getCornerPos() {
        return cornerPos;
    }

    public String getMd5() {
        return md5;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public void setRunType(int runType) {
        this.runType = runType;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setCornerUrl(String cornerUrl) {
        this.cornerUrl = cornerUrl;
    }

    public void setCornerPos(int cornerPos) {
        this.cornerPos = cornerPos;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getBgIcon() {
        return bgIcon;
    }

    public void setBgIcon(String bgIcon) {
        this.bgIcon = bgIcon;
    }

    public int getMicrophone() {
        return microphone;
    }

    public void setMicrophone(int microphone) {
        this.microphone = microphone;
    }

    public List<AdConf> getAd() {
        return ad;
    }

    public void setAd(List<AdConf> ad) {
        this.ad = ad;
    }

    public String getGameSecret() {
        return gameSecret;
    }

    public void setGameSecret(String gameSecret) {
        this.gameSecret = gameSecret;
    }

    public int getCornerType() {
        return cornerType;
    }

    public void setCornerType(int cornerType) {
        this.cornerType = cornerType;
    }
}
