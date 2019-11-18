package com.game.match.domain;

import com.game.chat.message.S2CContent;

public class MatchMessage extends S2CContent {
    private String gameId;
    private int accountId;
    private int type = 0;
    private String roomId;
    private int friendId;

    public String getGameId() {
        return gameId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "MatchMessage{" +
                "gameId='" + gameId + '\'' +
                ", accountId=" + accountId +
                ", type=" + type +
                ", roomId='" + roomId + '\'' +
                ", friendId=" + friendId +
                '}';
    }
}
