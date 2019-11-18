package com.game.account.domain;

public class VisitorVO {
    private int accountId;
    private String gToken;
    private String headerImg;
    private String nickName;
    private int age;
    private int sex;
    private int constellation;
    private long like;
    private int isNew;

    public static VisitorVO valueOf(AccountInfo accountInfo) {
        VisitorVO vo = new VisitorVO();
        vo.setAccountId(accountInfo.getId());
        vo.setAge(accountInfo.getAge());
        vo.setHeaderImg(accountInfo.getHeaderImg());
        vo.setNickName(accountInfo.getNickName());
        vo.setSex(accountInfo.getSex());
        vo.setConstellation(accountInfo.getConstellation());

        return vo;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getgToken() {
        return gToken;
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

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }


    public void setgToken(String gToken) {
        this.gToken = gToken;
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

    public long getLike() {
        return like;
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
}
