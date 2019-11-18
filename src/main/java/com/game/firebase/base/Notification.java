package com.game.firebase.base;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("title")
    public String title;

    @SerializedName("body")
    public String body;

    @SerializedName("body_loc_key")
    public String bodyLocKey;

    @SerializedName("body_loc_args")
    public String bodyLocArg;

    @SerializedName("title_loc_key")
    public String titleLocKey;

    @SerializedName("title_loc_args")
    public String titleLocArg;

    @SerializedName("click_action")
    public String clickAction;

    @SerializedName("tag")
    public String tag;
}
