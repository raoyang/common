package com.game.friend.domain;

import com.game.account.domain.AccountInfo;

public class AroundPlayer {
    private int accountId;
    private String headerImg;
    private String nickName;
    private int sex;
    private int age;
    //附近的人，间隔距离，单位：米
    private int distance;
    private int like;
    private String voice = "";

    public static AroundPlayer valueOf(AccountInfo accountInfo) {
        AroundPlayer player = new AroundPlayer();
        player.setAccountId(accountInfo.getId());
        player.setHeaderImg(accountInfo.getHeaderImg());
        player.setNickName(accountInfo.getNickName());
        player.setSex(accountInfo.getSex());
        player.setAge(accountInfo.getAge());
        player.setVoice("");

        return player;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public String getNickName() {
        return nickName;
    }

    public int getDistance() {
        return distance;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getLike() {
        return like;
    }

    public String getVoice() {
        return voice;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
