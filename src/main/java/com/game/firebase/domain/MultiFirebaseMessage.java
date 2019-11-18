package com.game.firebase.domain;


import java.util.LinkedList;
import java.util.List;

public class MultiFirebaseMessage extends FirebaseMessage {

    private List<String> recipientIds;

    public List<String> getRecipientIds() {
        return recipientIds;
    }

    public MultiFirebaseMessage(FirebaseMessage message) {
        super(message);
    }

    public static class Builder extends FirebaseMessage.Builder {

        List<String> recipientIds;


        public Builder recipients(int... recipients) {

            if (recipients != null) {
                if (recipientIds == null) {
                    recipientIds = new LinkedList<>();
                }

                for (int id : recipients) {
                    recipientIds.add(String.valueOf(id));
                }
            }

            return this;
        }

        public Builder recipients(List<Integer> recipients) {

            if (recipients != null) {
                if (recipientIds == null) {
                    recipientIds = new LinkedList<>();
                }

                for (Integer id : recipients) {
                    if (id != null) {
                        recipientIds.add(String.valueOf(id));
                    }
                }
            }

            return this;
        }

        @Override
        public MultiFirebaseMessage build() {
            MultiFirebaseMessage message = new MultiFirebaseMessage(super.build());
            message.recipientIds = recipientIds;
            return message;
        }
    }
}
