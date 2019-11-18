package com.game.firebase.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {

    @SerializedName("multicast_id")
    long multicastId;

    @SerializedName("success")
    int success;

    @SerializedName("failure")
    int failure;

    @SerializedName("results")
    List<Result> results;
}
