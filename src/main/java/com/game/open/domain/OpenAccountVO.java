package com.game.open.domain;

import com.game.account.domain.AccountInfo;

public class OpenAccountVO {
    private int uId;
    private String avatar;
    private String nickName;
    private int age;
    private int sex;
    private int constellation;
    private String address;

    public static OpenAccountVO valueOf(AccountInfo accountInfo) {
        OpenAccountVO accountVO = new OpenAccountVO();
        accountVO.setuId(accountInfo.getId());
        accountVO.setAvatar(accountInfo.getHeaderImg());
        accountVO.setNickName(accountInfo.getNickName());
        accountVO.setAge(accountInfo.getAge());
        accountVO.setSex(accountInfo.getSex());
        accountVO.setConstellation(accountInfo.getConstellation());
        accountVO.setAddress(accountInfo.getAddress());

        return accountVO;
    }
    public int getuId() {
        return uId;
    }

    public String getAvatar() {
        return avatar;
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

    public void setuId(int uId) {
        this.uId = uId;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
}
