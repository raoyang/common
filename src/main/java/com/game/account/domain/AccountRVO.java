package com.game.account.domain;

import com.game.home.domain.CommParam;

public class AccountRVO extends CommParam {
    private int taId;
    private int limit;

    public int getTaId() {
        return taId;
    }

    public void setTaId(int taId) {
        this.taId = taId;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }
}
