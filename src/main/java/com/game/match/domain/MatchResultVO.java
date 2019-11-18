package com.game.match.domain;

import com.game.chat.message.S2CContent;

import java.util.Date;
import java.util.List;

public class MatchResultVO extends S2CContent {
    private String roomId;
    private String gameId;
    private List<PlayerVO> players;
    private long curTime;

    public String getRoomId() {
        return roomId;
    }

    public String getGameId() {
        return gameId;
    }

    public List<PlayerVO> getPlayers() {
        return players;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setPlayers(List<PlayerVO> players) {
        this.players = players;
    }

    public long getCurTime() {
        return curTime;
    }

    public void setCurTime(long curTime) {
        this.curTime = curTime;
    }
}
