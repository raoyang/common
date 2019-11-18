package com.game.friend.domain;

import com.game.account.domain.AccountInfo;

public class RecommendFriend {

    public static final int SUPER_GAME_GOD = 2;
    public static final int GAME_GOD = 1;

    private int accountId;
    private String headerImg;
    private String nickName;
    private int sex;
    private int age;
    //推荐类型，0 Facebook好友 1 附近的人
    private int type;
    //附近的人，间隔距离，单位：米
    private int distance;

    private int godType; //大神类型 1:大神 2:超级大神
    private double winPersent;
    private String voice;
    private int like;
    private int totalCnt;

    public static RecommendFriend valueOf(AccountInfo accountInfo) {
        RecommendFriend friend = new RecommendFriend();
        friend.setAccountId(accountInfo.getId());
        friend.setHeaderImg(accountInfo.getHeaderImg());
        friend.setNickName(accountInfo.getNickName());

        return friend;
    }

    public static RecommendFriend valueOf(AccountInfo accountInfo, int godType, double winPersent, int totalCnt){
        RecommendFriend friend = new RecommendFriend();
        friend.setAccountId(accountInfo.getId());
        friend.setHeaderImg(accountInfo.getHeaderImg());
        friend.setNickName(accountInfo.getNickName());
        friend.setSex(accountInfo.getSex());
        friend.setAge(accountInfo.getAge());
        friend.godType = godType;
        friend.winPersent = winPersent;
        friend.totalCnt = totalCnt;
        return friend;
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

    public int getType() {
        return type;
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

    public void setType(int type) {
        this.type = type;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getGodType() {
        return godType;
    }

    public void setGodType(int godType) {
        this.godType = godType;
    }

    public double getWinPersent() {
        return winPersent;
    }

    public void setWinPersent(double winPersent) {
        this.winPersent = winPersent;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }
}
