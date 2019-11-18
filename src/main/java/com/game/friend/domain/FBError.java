package com.game.friend.domain;

public class FBError {
    private String message;
    private String type;
    private int coce;
    private int error_subcode;
    private String fbtrace_id;

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public int getCoce() {
        return coce;
    }

    public int getError_subcode() {
        return error_subcode;
    }

    public String getFbtrace_id() {
        return fbtrace_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCoce(int coce) {
        this.coce = coce;
    }

    public void setError_subcode(int error_subcode) {
        this.error_subcode = error_subcode;
    }

    public void setFbtrace_id(String fbtrace_id) {
        this.fbtrace_id = fbtrace_id;
    }
}
