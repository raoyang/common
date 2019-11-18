package com.game.account.domain;

import com.game.home.domain.CommParam;

public class Upload extends CommParam{
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
