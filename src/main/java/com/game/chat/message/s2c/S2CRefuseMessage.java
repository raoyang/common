package com.game.chat.message.s2c;

import com.game.chat.message.S2CContent;

public class S2CRefuseMessage extends S2CContent {

    private String msgId;

    private int reason;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }
}
