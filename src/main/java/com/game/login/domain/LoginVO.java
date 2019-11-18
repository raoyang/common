package com.game.login.domain;

import java.util.List;

public class LoginVO {
    private String gToken;
    private int accountId;
    private String headerImg;
    private String nickName;
    private int age;
    private int sex;
    private int constellation;
    private String address;
    private String birthday;
    private String voice;
    private String signature;
    private long like;
    private int isNew;
    private List<String> photoWall;

    public String getgToken() {
        return gToken;
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

    public int getAge() {
        return age;
    }

    public int getSex() {
        return sex;
    }

    public int getConstellation() {
        return constellation;
    }

    public String getAddress() {
        return address;
    }

    public void setgToken(String gToken) {
        this.gToken = gToken;
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

    public void setAge(int age) {
        this.age = age;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setConstellation(int constellation) {
        this.constellation = constellation;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getVoice() {
        return voice;
    }

    public String getSignature() {
        return signature;
    }

    public long getLike() {
        return like;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setLike(long like) {
        this.like = like;
    }


    public int getIsNew() {
        return isNew;
    }


    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public List<String> getPhotoWall() {
        return photoWall;
    }

    public void setPhotoWall(List<String> photoWall) {
        this.photoWall = photoWall;
    }
}
