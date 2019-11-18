package com.game.ranking.domain;

import com.game.account.domain.AccountInfo;

public class RankingData {
    private int accountId;
    private String headerImg;
    private String nickName;
    private String score;

    public static RankingData valueOf(String score, AccountInfo accountInfo) {
        RankingData vo = new RankingData();
        vo.setScore(score);
        vo.setAccountId(accountInfo.getId());
        vo.setNickName(accountInfo.getNickName());
        vo.setHeaderImg(accountInfo.getHeaderImg());
        return vo;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public String getNickName() {
        return nickName;
    }

    public String getScore() {
        return score;
    }


    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
