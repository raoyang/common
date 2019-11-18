package com.game.record.domain;

public class GameResultLog extends BaseInfo {
    private int event;
    private int result;
    private String roomId;

    public int getEvent() {
        return event;
    }

    public int getResult() {
        return result;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
