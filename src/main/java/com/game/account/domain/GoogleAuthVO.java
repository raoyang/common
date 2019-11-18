package com.game.account.domain;

public class GoogleAuthVO {
    private String iss;
    private String sub;
    private String azp;
    private String aud;
    private String iat;
    private String exp;

    private String email;
    private String email_verified;
    private String name;
    private String picture;
    private String given_name;
    private String family_name;
    private String locale;

    private String error;
    private String error_description;

    public String getIss() {
        return iss;
    }

    public String getSub() {
        return sub;
    }

    public String getAzp() {
        return azp;
    }

    public String getAud() {
        return aud;
    }

    public String getIat() {
        return iat;
    }

    public String getExp() {
        return exp;
    }

    public String getEmail() {
        return email;
    }

    public String getEmail_verified() {
        return email_verified;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getGiven_name() {
        return given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public String getLocale() {
        return locale;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setAzp(String azp) {
        this.azp = azp;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmail_verified(String email_verified) {
        this.email_verified = email_verified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}
