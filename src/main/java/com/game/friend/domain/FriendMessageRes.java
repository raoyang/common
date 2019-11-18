package com.game.friend.domain;

import com.game.account.domain.AccountInfo;

public class FriendMessageRes {

    private int applyAccount;

    private String nickName;

    private int sex;

    private String headerImg;

    public static FriendMessageRes valueOf(AccountInfo info){
        FriendMessageRes panel = new FriendMessageRes();
        panel.applyAccount = info.getId();
        panel.headerImg = info.getHeaderImg();
        panel.nickName = info.getNickName();
        panel.sex = info.getSex();
        return panel;
    }

    public int getApplyAccount() {
        return applyAccount;
    }

    public void setApplyAccount(int applyAccount) {
        this.applyAccount = applyAccount;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }
}
