package com.game.ranking.domain;

import com.game.account.domain.AccountInfo;

public class RankingMe extends RankingData{
    private int rankId;

    public static RankingMe valueOf(int rankId, String score, AccountInfo accountInfo) {
        RankingMe me = new RankingMe();
        me.setRankId(rankId);
        me.setHeaderImg(accountInfo.getHeaderImg());
        me.setNickName(accountInfo.getNickName());
        me.setScore(score);
        return me;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }
}
