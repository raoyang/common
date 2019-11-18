package com.game.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * AES加密/解密
 */
public class AESUtil {

    /**
     * model
     */
    private static final String MODEL = "AES/CFB/NOPadding";

    /**
     * key
     */
    private static final String DEFAULT_KEY = "a46e0b79689788b3d8ee39afc77d6d95";

    /**
     * 时间：2015-9-1下午10:46:05
     * 方法：encrypt
     * 描述：加密
     * 参数和返回值：
     *    @param strIn
     *    @return
     *    @throws Exception
     */
    public static String encrypt(String strIn) throws Exception {
        if (strIn == null || strIn.length() == 0) {
            return null;
        }
        return new String(encrypt(strIn.getBytes("utf-8")),"utf-8");
    }

    /**
     * 时间：2015-9-6下午3:03:26
     * 方法：encrypt
     * 描述：TODO
     * 参数和返回值：
     *    @return
     *    @throws Exception
     */
    public static byte[] encrypt(byte[] byteIn) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        byte[] encryptResult = encryptByte(byteIn, DEFAULT_KEY);
        return base64Encode(encryptResult);
    }

    /**
     * 时间：2015-11-30 下午4:32:42
     * 方法：encryptNoneBase
     * 描述：TODO
     * 参数和返回值：
     *	@param byteIn
     *	@return
     *	@throws Exception byte[]
     */
    public static byte[] encryptNoneBase64(byte[] byteIn) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        byte[] encryptResult = encryptByte(byteIn, DEFAULT_KEY);
        return encryptResult;
    }

    /**
     * 时间：2015-9-2下午5:44:17
     * 方法：encrypt
     * 描述：TODO
     * 参数和返回值：
     *    @param strIn 加密内容
     *    @param key 用户ID
     *    @return
     *    @throws Exception
     */
    public static String encrypt(String strIn, Long key) throws Exception {
        if (strIn == null || strIn.length() == 0) {
            return null;
        }
        byte[] byteIn = strIn.getBytes("utf-8");
        String rt = new String(encrypt(byteIn, key),"utf-8");
        return rt;
    }

    /**
     * 时间：2015-9-6下午3:04:03
     * 方法：encrypt
     * 描述：TODO
     * 参数和返回值：
     *    @param key 用户ID

     *    @return
     *    @throws Exception
     */
    public static byte[] encrypt(byte[] byteIn, Long key) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        String skey = mergeKey(String.valueOf(key), 8) + DEFAULT_KEY;
        byte[] encryptResult = encryptByte(byteIn, skey);
        return base64Encode(encryptResult);
    }

    /**
     * 时间：2015-11-30 下午4:37:26
     * 方法：encryptNoneBase
     * 描述：TODO
     * 参数和返回值：
     *	@param byteIn
     *	@param key
     *	@param syncSource
     *	@return
     *	@throws Exception byte[]
     */
    public static byte[] encryptNoneBase64(byte[] byteIn, Long key, String syncSource) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        String skey = mergeKey(String.valueOf(key), 8) + syncSource;
        byte[] encryptResult = encryptByte(byteIn, skey);
        return encryptResult;
    }

    /**
     * 时间：2015-9-1下午10:57:58
     * 方法：encrypt
     * 描述：加密
     * 参数和返回值：
     *    @param strIn 加密内容
     *    @param secretkey	密钥KEY
     *    @return
     *    @throws Exception
     */
    public static String encryptStr(String strIn, String secretkey) throws Exception {
        if (strIn == null || strIn.length() == 0) {
            return null;
        }
        byte[] byteIn = strIn.getBytes("utf-8");
        String encrypted = new String(base64Encode(encryptByte(byteIn, secretkey)),"utf-8");
        return encrypted;
    }

    /**
     * 时间：2015-9-6下午2:55:39
     * 方法：encrypt
     * 描述：TODO
     * 参数和返回值：
     *    @param secretkey	密钥KEY
     *    @return
     *    @throws Exception
     */
    private static byte[] encryptByte(byte[] byteIn, String secretkey) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        secretkey = md5Encode(secretkey).substring(0,16);
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        SecretKeySpec key = new SecretKeySpec(secretkey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(MODEL);// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);// 初始化
        byte[] encrypted = cipher.doFinal(byteIn);
        return encrypted;
    }

    /**
     * 时间：2015-9-1下午10:46:45
     * 方法：decrypt
     * 描述：解密
     * 参数和返回值：
     *    @return
     *    @throws Exception
     */
    public static String decrypt(String strIn) throws Exception {
        if (strIn == null || strIn.length() == 0) {
            return null;
        }
        return new String(decrypt(strIn.getBytes("utf-8")),"utf-8");
    }

    /**
     * 时间：2015-9-6下午2:40:58
     * 方法：decrypt
     * 描述：TODO
     * 参数和返回值：
     *    @param byteIn 解密内容
     *    @return
     *    @throws Exception
     */
    public static byte[] decrypt(byte[] byteIn) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        byte[] encryptResult = base64Decode(byteIn);
        return decryptByte(encryptResult, DEFAULT_KEY);
    }

    /**
     * 时间：2015-11-30 下午4:41:20
     * 方法：decryptNoneBase64
     * 描述：TODO
     * 参数和返回值：
     *	@param byteIn
     *	@return
     *	@throws Exception byte[]
     */
    public static byte[] decryptNoneBase64(byte[] byteIn) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        return decryptByte(byteIn, DEFAULT_KEY);
    }

    /**
     * 时间：2015-9-2下午5:48:03
     * 方法：decrypt
     * 描述：TODO
     * 参数和返回值：
     *    @param strIn 解密内容
     *    @param key 用户ID
     *    @return
     *    @throws Exception
     */
    public static String decrypt(String strIn, Long key) throws Exception {
        if (strIn == null || strIn.length() == 0) {
            return null;
        }
        byte[] byteIn = strIn.getBytes("utf-8");
        return new String(decrypt(byteIn, key),"utf-8");
    }

    /**
     * 时间：2015-9-6下午2:49:10
     * 方法：decrypt
     * 描述：TODO
     * 参数和返回值：
     *    @param key 用户ID
     *    @return
     *    @throws Exception
     */
    public static byte[] decrypt(byte[] byteIn, Long key) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        String skey = mergeKey(String.valueOf(key), 8) + DEFAULT_KEY;
        byte[] encryptResult = base64Decode(byteIn);
        return decryptByte(encryptResult, skey);
    }

    /**
     * 时间：2015-11-30 下午4:45:03
     * 作者：wangkai4
     * 方法：decryptNoneBase64
     * 描述：TODO
     * 参数和返回值：
     *	@param byteIn
     *	@param key
     *	@param syncSource
     *	@return
     *	@throws Exception byte[]
     */
    public static byte[] decryptNoneBase64(byte[] byteIn, Long key, String syncSource) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        String skey = mergeKey(String.valueOf(key), 8) + syncSource;
        return decryptByte(byteIn, skey);
    }

    /**
     * 时间：2015-9-1下午10:49:03
     * 方法：decrypt
     * 描述：解密
     * 参数和返回值：
     *    @param esecretkey	解密key
     *    @return
     *    @throws Exception
     */
    public static String decryptStr(String strIn, String esecretkey) throws Exception {
        if (strIn == null || strIn.length() == 0) {
            return null;
        }
        byte[] originalByte = decryptByte(base64Decode(strIn.getBytes("utf-8")), esecretkey);
        return new String(originalByte,"utf-8");
    }

    /**
     * 时间：2015-9-6下午2:41:31
     * 方法：decrypt
     * 描述：TODO
     * 参数和返回值：
     *    @param byteIn 解密内容
     *    @param esecretkey	解密key
     *    @return
     *    @throws Exception
     */
    private static byte[] decryptByte(byte[] byteIn, String esecretkey) throws Exception {
        if (byteIn == null || byteIn.length == 0) {
            return null;
        }
        esecretkey = md5Encode(esecretkey).substring(0,16);
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes("utf-8"));
        SecretKeySpec key = new SecretKeySpec(esecretkey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(MODEL);		// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, key, iv);		// 初始化
        byte[] result = cipher.doFinal(byteIn);
        return result;
    }

    /**
     * 时间：2015-9-6下午2:38:58
     * 方法：base64decode
     * 描述：TODO
     * 参数和返回值：
     *    @param buff
     *    @return
     *    @throws Exception
     */
    private static byte[] base64Decode(byte[] buff) throws Exception {
        return Base64.decode(buff);
    }

    /**
     * 时间：2015-9-6下午3:02:36
     * 方法：base64Encode
     * 描述：TODO
     * 参数和返回值：
     *    @param buff
     *    @return
     */
    private static byte[] base64Encode(byte[] buff) throws Exception{
        return Base64.encode(buff);
    }

    /**
     * 时间：2015-9-1下午10:53:52
     * 方法：mergeKey
     * 描述：TODO
     * 参数和返回值：
     *    @param v
     *    @param len
     *    @return
     */
    private static String mergeKey(String v, int len) {
        if (v.length() < len) {
            String tmp = "";
            for (int i = 0; i < len - v.length(); i++) {
                tmp += "0";
            }
            v = tmp + v;
        }
        return v;
    }

    /**
     * 时间：2015-9-1下午10:53:56
     * 方法：MD5
     * 描述：TODO
     * 参数和返回值：
     *    @param content
     *    @return
     */
    public static String md5Encode(String content) throws Exception{
        if (content == null || content.length() == 0) {
            return "";
        }
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        byte[] strTemp = content.getBytes();
        MessageDigest mdTemp = MessageDigest.getInstance("MD5");
        mdTemp.update(strTemp);
        byte[] md = mdTemp.digest();
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    /**
     * 时间：2015-11-6 下午4:16:02
     * 方法：md5Encode
     * 描述：TODO
     * 参数和返回值：
     *	@param content
     *	@return
     *	@throws Exception String
     */
    public static String md5Encode(byte[] content) throws Exception{
        if (content == null || content.length == 0) {
            return "";
        }
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        MessageDigest mdTemp = MessageDigest.getInstance("MD5");
        mdTemp.update(content);
        byte[] md = mdTemp.digest();
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

} 