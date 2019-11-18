package com.game.firebase.domain;


public class FirebaseInfo {

    private int accountId;
    private String firebaseId;
    private String firebaseToken;

    public FirebaseInfo(){

    }

    public FirebaseInfo(int accountId, String firebaseId, String token) {
        this.accountId = accountId;
        this.firebaseId = firebaseId;
        this.firebaseToken = token;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
