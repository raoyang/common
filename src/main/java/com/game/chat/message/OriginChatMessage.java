package com.game.chat.message;

import com.game.netty.message.BaseMessage;

public class OriginChatMessage {

    private int sender;

    private int moduleId;

    private int cmd;

    private byte[] content;

    public static OriginChatMessage valueOf(BaseMessage message, int sender){
        OriginChatMessage origin = new OriginChatMessage();
        origin.sender = sender;
        origin.moduleId = message.getModule();
        origin.cmd = message.getCmd();
        origin.content = message.getBody();
        return origin;
    }

    public int getSender() {
        return sender;
    }

    public int getModuleId() {
        return moduleId;
    }

    public int getCmd() {
        return cmd;
    }

    public byte[] getContent() {
        return content;
    }
}
