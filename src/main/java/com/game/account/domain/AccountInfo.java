package com.game.account.domain;

import com.game.util.Constant;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AccountInfo {
    private int id;
    private String openId;
    private int platform;
    private String headerImg;
    private String photoWall;
    private String nickName;
    private byte[] nickNameByte;
    private int age;
    private int constellation;
    private int sex;
    private String address;
    private byte[] addressByte;
    private String birthday;
    private String longitude;
    private String latitude;
    private String voice; //个性语音
    private String signature;
    private byte[] signatureByte; //个性签名
    private long like;
    private short type;
    private String uuid;

    public int getId() {
        return id;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOpenId() {
        return openId;
    }

    public int getPlatform() {
        return platform;
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

    public int getConstellation() {
        return constellation;
    }

    public int getSex() {
        return sex;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
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

    public void setConstellation(int constellation) {
        this.constellation = constellation;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public byte[] getNickNameByte() {
        return nickNameByte;
    }

    public byte[] getAddressByte() {
        return addressByte;
    }

    public void setNickNameByte(byte[] nickNameByte) {
        this.nickNameByte = nickNameByte;
        this.nickName = new String(nickNameByte, StandardCharsets.UTF_8);
    }

    public void setAddressByte(byte[] addressByte) {
        this.addressByte = addressByte;
        this.address = new String(addressByte, StandardCharsets.UTF_8);
    }

    public String getSignature() {
        return signature;
    }

    public byte[] getSignatureByte() {
        return signatureByte;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setSignatureByte(byte[] signatureByte) {
        this.signatureByte = signatureByte;
        this.signature = new String(signatureByte, StandardCharsets.UTF_8);
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public long getLike() {
        return like;
    }

    public String getPhotoWall() {
        return photoWall;
    }

    public void setPhotoWall(String photoWall) {
        this.photoWall = photoWall;
    }

    public short getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setType(short type) {
        this.type = type;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isAi(){
        if(platform == Constant.ACCOUNT_PLATFORM_AI){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "id=" + id +
                ", openId='" + openId + '\'' +
                ", platform=" + platform +
                ", headerImg='" + headerImg + '\'' +
                ", nickName='" + nickName + '\'' +
                ", age=" + age +
                ", constellation=" + constellation +
                ", sex=" + sex +
                ", address='" + address + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
