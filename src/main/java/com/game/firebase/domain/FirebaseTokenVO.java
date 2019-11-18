package com.game.firebase.domain;

import com.game.home.domain.CommParam;

public class FirebaseTokenVO extends CommParam {

    private String firebaseId;
    private String firebaseToken;

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
