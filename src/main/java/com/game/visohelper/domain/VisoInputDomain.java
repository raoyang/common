package com.game.visohelper.domain;

import java.nio.charset.Charset;

public class VisoInputDomain {

    private int accountId;

    private byte[] msg;

    public static VisoInputDomain valueOf(VisoInputVO vo){
        VisoInputDomain domain = new VisoInputDomain();
        domain.accountId = vo.getAccountId();
        domain.msg = vo.getMsg().getBytes(Charset.forName("utf-8"));
        return domain;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public byte[] getMsg() {
        return msg;
    }

    public void setMsg(byte[] msg) {
        this.msg = msg;
    }
}
