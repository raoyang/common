package com.game.login.domain;

import com.game.account.domain.AccountInfo;

public class RegisterVO {
    private int code;
    private int isNew;
    private AccountInfo accountInfo;

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }
}
