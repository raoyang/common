package com.game.preconf.domain;

import java.util.List;

public class PreConfVO {
    private int netType;
    private List<String> gameId;

    public static PreConfVO valueOf(int net, List<String> ids) {
        PreConfVO confVO = new PreConfVO();
        confVO.setNetType(net);
        confVO.setGameId(ids);

        return confVO;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public int getNetType() {
        return netType;
    }

    public List<String> getGameId() {
        return gameId;
    }

    public void setGameId(List<String> gameId) {
        this.gameId = gameId;
    }
}
