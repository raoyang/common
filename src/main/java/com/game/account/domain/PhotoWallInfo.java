package com.game.account.domain;

import com.game.chat.util.JsonUtils;

public class PhotoWallInfo {

    private int accountId;

    private String photo; //json格式

    public static PhotoWallInfo valueOf(PhotoWallVO vo){
        PhotoWallInfo info = new PhotoWallInfo();
        info.accountId = vo.getAccountId();
        info.photo = JsonUtils.objectToString(vo.getUrl());
        return info;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
