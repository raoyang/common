package com.game.chat.callback;

import com.game.chat.message.S2CContent;

public interface OfflineCallBack {
    void callback(S2CContent msg) throws Exception;
}
