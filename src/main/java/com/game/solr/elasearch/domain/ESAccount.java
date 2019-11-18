package com.game.solr.elasearch.domain;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class ESAccount {

    private int accountId;

    private String nickName;

    @Expose
    private Date time;

    private long createTime;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void cal(){
        if(time != null){
            createTime = time.getTime();
        }
    }
}
