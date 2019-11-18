package com.game.firebase.domain;


/**
 *
 *  最简单的Firebase信息
 *
 */
public class FirebaseMessage {

    /**
     *
     */
    protected static long MAX_TIME_TO_LIVE_FOUR_WEEK = 4 * 7 * 24 * 3600;

    /**
     *
     */
    String sourceUserId;

    /**
     *
     */
    String targetUserId;


    /**
     *
     */
    boolean isNotification;


    /**
     *
     */
    long timeToLive;

    /**
     *
     */
    MessageEntity messageEntity;

    protected FirebaseMessage() {

    }

    protected FirebaseMessage(FirebaseMessage other) {
        this.timeToLive = other.timeToLive;
        this.messageEntity = other.messageEntity;
        this.sourceUserId = other.sourceUserId;
        this.targetUserId = other.targetUserId;
        this.isNotification = other.isNotification;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public MessageEntity getMessageEntity() {
        return messageEntity;
    }

    public void setMessageEntity(MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public static class Builder {
        String sourceUserId;
        String targetUserId;
        boolean isNotification;
        MessageEntity messageEntity;
        long timeToLive = MAX_TIME_TO_LIVE_FOUR_WEEK;

        public Builder source(int sourceUserId) {
            this.sourceUserId = String.valueOf(sourceUserId);
            return this;
        }

        public Builder target(int targetUserId) {
            this.targetUserId = String.valueOf(targetUserId);
            return this;
        }

        public Builder isNotification(boolean isNotification) {
            this.isNotification = isNotification;
            return this;
        }

        public Builder entity(MessageEntity entity) {
            this.messageEntity = entity;
            return this;
        }

        /**
         * set the message live time in FCM, when the target is not online.
         *
         * @param timeToLive  it is time to live in fcm, unit is second.
         * @return
         */
        public Builder timeToLive(long timeToLive) {
            this.timeToLive = timeToLive;
            return this;
        }

        public FirebaseMessage build() {
            FirebaseMessage message = new FirebaseMessage();
            message.timeToLive = timeToLive;
            message.isNotification = isNotification;
            message.targetUserId = targetUserId;
            message.sourceUserId = sourceUserId;
            message.messageEntity = messageEntity;
            return message;
        }

    }
}
