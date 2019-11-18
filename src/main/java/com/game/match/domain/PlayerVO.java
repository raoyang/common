package com.game.match.domain;

import com.game.account.domain.AccountInfo;
import com.game.match.service.MatchService;

public class PlayerVO {
    private int accountId;
    private String nickName;
    private String headerImg;
    private int sex;
    private int ai;
    private int aiRank;

    public static PlayerVO valueOf(AccountInfo accountInfo) {
        PlayerVO playerVO = new PlayerVO();
        playerVO.setAccountId(accountInfo.getId());
        playerVO.setNickName(accountInfo.getNickName());
        playerVO.setHeaderImg(accountInfo.getHeaderImg());
        playerVO.setSex(accountInfo.getSex());
        playerVO.setAi(accountInfo.isAi() ? MatchService.ACCOUNT_TYPE_AI : 0);
        return playerVO;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public int getSex() {
        return sex;
    }

    public int getAi() {
        return ai;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setAi(int ai) {
        this.ai = ai;
    }

    public int getAiRank() {
        return aiRank;
    }

    public void setAiRank(int aiRank) {
        this.aiRank = aiRank;
    }
}
