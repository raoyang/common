package com.game.match.domain;

/***
 * 通用转发数据
 */
public class CommonMessage {

    private String roomId;

    private String data;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
