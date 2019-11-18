package com.game.firebase.base;

import java.util.Map;

public class FirebaseResult {

    /**
     * When success, the value is 200;
     * others is something wrong, such as connection timeout with FCM.
     *
     * Just the value is 200, the response and the errorMessage are valid.
     *
     */
    int responseCode;

    Response response;

    Map<String, Result> errorMessage;

    public int getFcmStatus() {
        return responseCode;
    }

    public int getSuccessCount() {
        if (response != null) {
            return response.success;
        }

        return 0;
    }

    public int getFailedCount() {
        if (response != null) {
            return response.failure;
        }

        return 0;
    }

    public Map<String, Result> getErrorMessage() {
        return errorMessage;
    }
}
