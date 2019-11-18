package com.game.account.domain;

import com.game.home.domain.CommParam;
import java.util.List;

public class AccountsInfo extends CommParam {

    private List<Integer> accounts;

    public List<Integer> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Integer> accounts) {
        this.accounts = accounts;
    }
}
