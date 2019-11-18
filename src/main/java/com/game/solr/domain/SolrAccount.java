package com.game.solr.domain;

import org.apache.solr.client.solrj.beans.Field;

/***
 * solr中保存的用户信息
 */
public class SolrAccount extends SolrBaseDomain{

    @Field("accountId")
    private int accountId;

    @Field("nickName")
    private String nickName;

    @Field("headerImg")
    private String headerImg;

    @Override
    public String getKeywords(){
        return "nickName:" + nickName;
    }

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

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }
}
