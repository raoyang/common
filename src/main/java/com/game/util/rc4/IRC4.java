package com.game.util.rc4;

public interface IRC4 {
    byte[] encrypt(byte[] toEncrypt);
    byte[] decrypt(byte[] toDecrypt);
    /*String encryptToBase64(String toEncrypt);
    String decryptFromBase64(String toDecrypt);*/
}