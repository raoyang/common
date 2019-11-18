package com.game.util.rc4;

public class RC4Factory {
    public static IRC4 create(String key) {
        return new RC4JavaxImpl(key.getBytes());
    }
}
