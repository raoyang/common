package com.game.account.domain;

import com.game.home.domain.CommParam;
import java.util.List;

public class PhotoWallVO extends CommParam {

    private List<String> url;

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PhotoWallVO{" +
                "url=" + url +
                '}';
    }
}
