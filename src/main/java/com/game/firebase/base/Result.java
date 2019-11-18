package com.game.firebase.base;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("message_id")
    public String messageId;

    @SerializedName("registration_id")
    public String registrationId;

    @SerializedName("error")
    public String error;


    @Override
    public String toString() {
        return "Result{" +
                "messageId='" + messageId + '\'' +
                ", registrationId='" + registrationId + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
