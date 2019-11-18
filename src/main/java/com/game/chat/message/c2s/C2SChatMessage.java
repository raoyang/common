package com.game.chat.message.c2s;

import com.game.chat.constant.ChatContentType;
import com.game.chat.message.OriginChatMessage;
import com.game.chat.util.JsonUtils;

import java.nio.charset.Charset;

public class C2SChatMessage {

    private int sender;

    private String msgId;

    private int from; // 1.个人聊天 （当前版本只支持个人聊天）

    private int type; // 1.文本 2.图片 3.表情 4.语音

    private String suffix; //文件格式

    private int to; //消息接收者的账号id，后期如果扩展为群消息，则为群id

    private String content; //消息内容

    public static C2SChatMessage valueOf(OriginChatMessage msg){
        String json = new String(msg.getContent(), Charset.forName("utf-8"));
        C2SChatMessage message = JsonUtils.stringToObject(json, C2SChatMessage.class);
        message.sender = msg.getSender();
        return message;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSender(){
        return sender;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /***
     * 是否保存离线内容到disk
     * @return
     */
    public boolean saveOfflineContentOndisk(){
        return type == ChatContentType.ICON || type == ChatContentType.VOICE;
    }

    @Override
    public String toString() {
        return "C2SChatMessage{" +
                "sender=" + sender +
                ", msgId=" + msgId +
                ", from=" + from +
                ", type=" + type +
                ", suffix='" + suffix + '\'' +
                ", to=" + to +
                ", content='" + content + '\'' +
                '}';
    }
}
