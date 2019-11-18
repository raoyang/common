package com.game.friend.domain;

import java.util.List;

public class FBFriendData {
    private List<FBFriend> data;
    private FBError error;

    public void setData(List<FBFriend> data) {
        this.data = data;
    }

    public List<FBFriend> getData() {
        return data;
    }

    public FBError getError() {
        return error;
    }

    public void setError(FBError error) {
        this.error = error;
    }
}
