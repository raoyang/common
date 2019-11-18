package com.game.record.domain;

public class AdLog extends BaseInfo {
    private int event;
    private String adId;
    private int result;
    private String time;

    public int getEvent() {
        return event;
    }

    public String getAdId() {
        return adId;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
