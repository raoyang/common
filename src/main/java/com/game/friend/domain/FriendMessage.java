package com.game.friend.domain;

import com.game.home.domain.CommParam;

public class FriendMessage extends CommParam {

    /** 被申请者ID **/
    private int applyId;

    public int getApplyId() {
        return applyId;
    }

    public void setApplyId(int applyId) {
        this.applyId = applyId;
    }
}
