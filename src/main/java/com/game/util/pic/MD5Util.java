/**
 * qiku.com Inc.
 * copyright (c) 2015-2017 All Rights Reserved.
 */

package com.game.util.pic;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 工具类
 */
public class MD5Util {
    public static String getMD5String(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] e = md.digest(value.getBytes());
            return toHexString(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return value;
        }
    }

    public static String digest(String beforeDigest) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bytes = md5.digest(beforeDigest.getBytes());
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bytes) {
            int bt = b & 0xff;
            if (bt < 16) {
                stringBuffer.append(0);
            }
            stringBuffer.append(Integer.toHexString(bt));
        }
        return stringBuffer.toString();
    }

    public static String getMD5String(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] e = md.digest(bytes);
            return toHexString(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String toHexString(byte bytes[]) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < bytes.length; n++) {
            stmp = Integer.toHexString(bytes[n] & 0xff);
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }

        return hs.toString();
    }

    public static String getFileMD5(File paramString) {
        FileInputStream localFileInputStream = null;
        String str = null;
        try {
            localFileInputStream = new FileInputStream(paramString);
            byte[] arrayOfByte = new byte[262144];
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            int i = 0;
            while ((i = localFileInputStream.read(arrayOfByte)) > 0)
                localMessageDigest.update(arrayOfByte, 0, i);
            str = toHexString(localMessageDigest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
//             logger.error("getFileMD5 error: " + localIOException.getMessage(), localIOException.fillInStackTrace());
        } finally {
            if (localFileInputStream != null)
                try {
                    localFileInputStream.close();
                } catch (IOException localIOException6) {
                    localIOException6.printStackTrace();
                }
        }
        return str;
    }

    public static void main(String[] args) {
        System.out.println(MD5Util.getFileMD5(new File("C:\\Users\\zhaopengfei-os\\Downloads\\ea86debb99dd8a5199c38ad4cfaac026.ttf")));
    }
}
