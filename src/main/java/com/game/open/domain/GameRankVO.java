package com.game.open.domain;

public class GameRankVO {
    private int rankId;

    public static GameRankVO valueOf(int rankId) {
        GameRankVO rankVO = new GameRankVO();
        rankVO.setRankId(rankId);

        return rankVO;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public int getRankId() {
        return rankId;
    }
}
