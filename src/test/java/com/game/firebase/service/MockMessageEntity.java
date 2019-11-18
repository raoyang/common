package com.game.firebase.service;

import com.google.gson.annotations.SerializedName;

public class MockMessageEntity {

    @SerializedName("s_uid")
    public int sourceId;

    @SerializedName("s_name")
    public String sourceName;

    @SerializedName("s_thumb")
    public String thumbUrl;
}
