package com.game.login.domain;

import com.game.home.domain.CommParam;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class Account extends CommParam{
    private String openId = "";
    private int platform = 0;
    private String token = "";
    private String headerImg = "";
    private String nickName = "";
    private byte[] nickNameByte;
    private int age = -1;
    private int sex = 0;
    private int constellation = 0;
    private String address = "";
    private byte[] addressByte;
    private String birthday = "";
    private short changeAccount;
    private int flush;
    private String firebaseId;
    private String firebaseToken;

    private String voice; //个性语音存储地址
    private String signature; //个人签名 -> 转字节流存储
    private List<String> photoWall;

    public String getOpenId() {
        return openId;
    }

    public int getPlatform() {
        return platform;
    }

    public String getToken() {
        return token;
    }

    public String getHeaderImg() {
        return headerImg;
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

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        this.addressByte = address.getBytes(StandardCharsets.UTF_8);
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        this.nickNameByte = nickName.getBytes(StandardCharsets.UTF_8);
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public short getChangeAccount() {
        return changeAccount;
    }

    public void setChangeAccount(short changeAccount) {
        this.changeAccount = changeAccount;
    }

    public int getFlush() {
        return flush;
    }

    public void setFlush(int flush) {
        this.flush = flush;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public List<String> getPhotoWall() {
        return photoWall;
    }

    public void setPhotoWall(List<String> photoWall) {
        this.photoWall = photoWall;
    }
}
