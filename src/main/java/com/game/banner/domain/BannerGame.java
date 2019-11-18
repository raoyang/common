package com.game.banner.domain;

import com.game.ad.domain.AdConf;
import com.game.game.domain.Game;
import com.game.util.Constant;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class BannerGame extends BannerInfo {
    private String gameId;
    private String name;
    private String icon;
    private String bgIcon;
    private String url;
    private int gameType;
    private int runType;
    private String color;
    private String md5;
    private int orientation;
    private int versionCode;
    private int microphone;
    private int online;
    private List<AdConf> ad;
    private String gameSecret;

    public static BannerGame valueOf(Game game, int onlineCnt, Banner banner) {
        BannerGame bannerGame = new BannerGame();
        BeanUtils.copyProperties(game, bannerGame);
        bannerGame.setResType(1);
        bannerGame.setBannerType(Constant.BANNER_TYPE_GAME_RECOMMEND);
        bannerGame.setBannerIcon(banner.getIcon());
        bannerGame.setOnline(onlineCnt);

        return bannerGame;
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

    public void setOnline(int online) {
        this.online = online;
    }

    public int getOnline() {
        return online;
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
}
