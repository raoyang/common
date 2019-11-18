package com.game.banner.domain;

import com.game.util.Constant;

public class BannerH5 extends BannerInfo{
    private String url;
    private String title;

    public static BannerH5 valueOf(Banner banner) {
        BannerH5 bannerH5 = new BannerH5();
        bannerH5.setUrl(banner.getLinkTo());
        bannerH5.setTitle(banner.getName());
        bannerH5.setBannerIcon(banner.getIcon());
        bannerH5.setResType(1);
        bannerH5.setBannerType(Constant.BANNER_TYPE_H5);

        return bannerH5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
