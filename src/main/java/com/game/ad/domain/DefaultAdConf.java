package com.game.ad.domain;

import java.util.List;

public class DefaultAdConf {
    private String appId;
    private List<AdConf> adId;

    public String getAppId() {
        return appId;
    }

    public List<AdConf> getAdId() {
        return adId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAdId(List<AdConf> adId) {
        this.adId = adId;
    }
}
