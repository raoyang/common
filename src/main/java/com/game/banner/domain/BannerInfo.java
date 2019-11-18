package com.game.banner.domain;

import com.game.home.domain.HomeCard;

public class BannerInfo extends HomeCard{
    private int bannerType;
    private String bannerIcon;

    public int getBannerType() {
        return bannerType;
    }

    public String getBannerIcon() {
        return bannerIcon;
    }

    public void setBannerType(int bannerType) {
        this.bannerType = bannerType;
    }

    public void setBannerIcon(String bannerIcon) {
        this.bannerIcon = bannerIcon;
    }
}
