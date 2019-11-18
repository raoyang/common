package com.game.firebase.domain;

import com.google.gson.annotations.SerializedName;

public class MessageEntity {

    public static final int CMD_FRIEND_APPLY = 1;
    public static final int CMD_FRIEND_AGREE = 2;

    /**
     * 消息编号
     */
    @SerializedName("cmd")
    int cmd;


    /**
     * 消息版本号
     */
    @SerializedName("ver")
    int ver;

    /**
     *  0x00: 没有通知
     *  0x01: 应用内通知
     *  0x02：状态栏通知
     *  0x03: 应用内和状态栏通知
     *
     */
    @SerializedName("n_type")
    int notificationType;


    /**
     *
     */
    @SerializedName("expire")
    long expire = 0;

    /**
     * 发送消息内容
     */
    @SerializedName("content")
    Object data;

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }
}
