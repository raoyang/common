package com.game.account.domain;

import com.game.friend.domain.FBError;

public class FaceBookAuthVO {
    private FaceBookData data;
    private FBError error;

    public void setData(FaceBookData data) {
        this.data = data;
    }

    public FaceBookData getData() {
        return data;
    }

    public void setError(FBError error) {
        this.error = error;
    }

    public FBError getError() {
        return error;
    }
}
