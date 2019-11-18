package com.game.friend.domain;

import com.google.gson.annotations.SerializedName;

public class ApplicationMessage {

    @SerializedName("s_uid")
    int sourceId;

    @SerializedName("s_name")
    String sourceName;

    @SerializedName("s_thumb")
    String thumbUrl;

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }


    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }


    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}
