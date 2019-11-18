package com.game.account.domain;

import java.util.List;

public class FaceBookData {
    private String app_id;
    private String type;
    private String application;
    private Long expires_at;
    private Boolean is_valid;
    private Long issued_at;
    private String user_id;
    private List<String> scopes;

    public String getApp_id() {
        return app_id;
    }

    public String getType() {
        return type;
    }

    public String getApplication() {
        return application;
    }

    public Long getExpires_at() {
        return expires_at;
    }

    public Boolean getIs_valid() {
        return is_valid;
    }

    public Long getIssued_at() {
        return issued_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void setExpires_at(Long expires_at) {
        this.expires_at = expires_at;
    }

    public void setIs_valid(Boolean is_valid) {
        this.is_valid = is_valid;
    }

    public void setIssued_at(Long issued_at) {
        this.issued_at = issued_at;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
