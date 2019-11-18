package com.game.chat.event.impl;

import com.game.chat.event.BaseEvent;

public class LoginEvent implements BaseEvent {

    private int accountId;

    @Override
    public int getId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
