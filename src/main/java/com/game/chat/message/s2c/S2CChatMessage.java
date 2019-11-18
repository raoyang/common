package com.game.chat.message.s2c;

import com.game.chat.message.S2CContent;

public class S2CChatMessage extends S2CContent {

    private int from;

    private int groupId;

    private int type;

    private String suffix;

    private int sender;

    private boolean online;

    private String content;

    public static class Builder{

        private S2CChatMessage s2CChatMessage;

        public Builder(){
            s2CChatMessage = new S2CChatMessage();
        }

        public Builder setFrom(int from){
            s2CChatMessage.setFrom(from);
            return this;
        }

        public Builder setGroupId(int groupId){
            s2CChatMessage.setGroupId(groupId);
            return this;
        }

        public Builder setType(int type){
            s2CChatMessage.setType(type);
            return this;
        }

        public Builder setSender(int sender){
            s2CChatMessage.setSender(sender);
            return this;
        }

        public Builder setOnline(boolean isOnline){
            s2CChatMessage.setOnline(isOnline);
            return this;
        }

        public Builder setContent(String content){
            s2CChatMessage.setContent(content);
            return this;
        }

        public Builder setSuffix(String suffix){
            s2CChatMessage.setSuffix(suffix);
            return this;
        }

        public S2CChatMessage build(){
            return s2CChatMessage;
        }
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public static Builder newBuilder(){
        Builder builder = new Builder();
        return builder;
    }
}
