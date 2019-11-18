package com.game.account.domain;

import com.game.chat.util.JsonUtils;
import com.game.solr.domain.SolrAccount;

import java.util.ArrayList;
import java.util.List;

/***
 * 用户面板信息
 */
public class AccountPanelInfo {

    private int id;
    private int platform;
    private String headerImg;
    private String nickName;
    private int age;
    private int sex;
    private String address;
    private String birthday;
    private int constellation;
    private long like;
    private int isLike;

    private String voice; //个性语音
    private String signature; //个性签名
    private List<String> photoWall; //照片墙


    public static AccountPanelInfo valueOf(AccountInfo info){
        AccountPanelInfo panel = new AccountPanelInfo();
        panel.id = info.getId();
        panel.platform = info.getPlatform();
        panel.headerImg = info.getHeaderImg();
        panel.nickName = info.getNickName();
        panel.age = info.getAge();
        panel.sex = info.getSex();
        panel.address = info.getAddress();
        panel.birthday = info.getBirthday();
        panel.constellation = info.getConstellation();
        panel.voice = info.getVoice();
        panel.signature = info.getSignature();
        panel.like = info.getLike();
        //panel.photoWall = info.getPhotoWall();
        if(info.getPhotoWall() != null && !info.getPhotoWall().equals("")){
            panel.photoWall = JsonUtils.stringToList(info.getPhotoWall(), String[].class);
        }else{
            panel.photoWall = new ArrayList<>();
        }
        return panel;
    }

    public static AccountPanelInfo valueOfSearch(AccountInfo info){
        AccountPanelInfo panel = new AccountPanelInfo();
        panel.id = info.getId();
        panel.headerImg = info.getHeaderImg();
        panel.nickName = info.getNickName();
        return panel;
    }

    public static AccountPanelInfo valueOfSolrAccount(SolrAccount account){
        AccountPanelInfo panel = new AccountPanelInfo();
        panel.id = (int)account.getId();
        panel.headerImg = account.getHeaderImg();
        panel.nickName = account.getNickName();
        return panel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getConstellation() {
        return constellation;
    }

    public void setConstellation(int constellation) {
        this.constellation = constellation;
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

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public List<String> getPhotoWall() {
        return photoWall;
    }

    public void setPhotoWall(List<String> photoWall) {
        this.photoWall = photoWall;
    }

    @Override
    public String toString() {
        return "AccountPanelInfo{" +
                "id=" + id +
                ", platform=" + platform +
                ", headerImg='" + headerImg + '\'' +
                ", nickName='" + nickName + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", address='" + address + '\'' +
                ", birthday='" + birthday + '\'' +
                ", constellation=" + constellation +
                ", like=" + like +
                ", isLike=" + isLike +
                ", voice='" + voice + '\'' +
                ", signature='" + signature + '\'' +
                ", photoWall=" + photoWall +
                '}';
    }
}
