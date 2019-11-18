package com.game.friend.domain;

import com.game.account.domain.AccountInfo;

public class BlackMessageRes {

    private int applyAccount;

    private String nickName;

    private int sex;

    private String headerImg;

    /** 拉黑的具体时间 **/
    private long blackTime;

    /***
     *
     * @param info
     * @param blackTime
     * @return
     */
    public static BlackMessageRes valueOf(AccountInfo info, long blackTime){
        BlackMessageRes panel = new BlackMessageRes();
        panel.applyAccount = info.getId();
        panel.headerImg = info.getHeaderImg();
        panel.nickName = info.getNickName();
        panel.sex = info.getSex();
        panel.blackTime = blackTime;
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

    public long getBlackTime() {
        return blackTime;
    }

    public void setBlackTime(long blackTime) {
        this.blackTime = blackTime;
    }
}
