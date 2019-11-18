package com.game.firebase.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Message {

    @SerializedName("to")
    public String to;

    @SerializedName("registration_ids")
    public List<String> recipients;

    @SerializedName("time_to_live")
    public long timeToLive;

    @SerializedName("notification")
    public Notification notification;

    @SerializedName("data")
    public Object data;


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }
}
