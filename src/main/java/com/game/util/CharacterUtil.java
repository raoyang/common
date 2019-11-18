package com.game.util;


import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * 字符处理
 */
public class CharacterUtil {

    //~ Methods ----------------------------------------------------------------

    /**
     * BASE64加密
     *
     * @param s 源字符串
     *
     * @return 加密后字符串
     * @throws UnsupportedEncodingException
     */
//    public static String encodeBase64(String s) throws UnsupportedEncodingException {
//        return new String(Base64.encodeBase64(s.getBytes("UTF-8")), "UTF-8");
//    }

    /**
     * BASE64解密
     *
     * @param s 源字符串
     *
     * @return 解密后字符串
     * @throws UnsupportedEncodingException
     */
//    public static String decodeBase64(String s) throws UnsupportedEncodingException {
//        return new String(Base64.decodeBase64(s.getBytes("UTF-8")), "UTF-8");
//    }

    /**
     * 压缩GZip
     *
     * @param data 待压缩字节流数组
     * @return byte[] 压缩后的数据
     * @throws IOException
     */
    public static byte[] gZip(byte[] data) throws IOException {
        byte[] b = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gos = null;

        try {
            gos = new GZIPOutputStream(bos);
            gos.write(data);
            gos.finish();
            b = bos.toByteArray();
        } finally {
            if (gos != null) {
                gos.close();
            }
            bos.close();
        }

        return b;
    }

    /**
     * 解压GZip
     *
     * @param data 待解压字节流数组
     * @return byte[] 解压后的数据
     * @throws IOException
     */
    public static byte[] unGZip(byte[] data) throws IOException {
        byte[] b = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPInputStream gis = null;

        try {
            byte[] buf = new byte[1024];
            int num = -1;

            gis = new GZIPInputStream(bis);
            while ((num = gis.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }

            b = baos.toByteArray();
        } finally {
            if (gis != null) {
                gis.close();
            }

            baos.flush();
            baos.close();
            bis.close();
        }

        return b;
    }

    /**
     * 解压GZip
     *
     * @param is 输入流
     * @return String 解压后的数据
     * @throws IOException
     */
    public static String unGZip2Str(InputStream is) throws IOException {
        String result = "";
        GZIPInputStream gis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            gis = new GZIPInputStream(is);
            byte[] buf = new byte[1024];
            int num = -1;
            while ((num = gis.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }

            result = baos.toString("UTF-8");
        } finally {
            if (gis != null) {
                gis.close();
            }

            baos.flush();
            baos.close();
        }

        return result;
    }

    /**
     * 解压GZip
     *
     * @param is 输入流
     * @return String 解压后的数据
     * @throws IOException
     */
    public static byte[] unGZip2Byte(InputStream is) throws IOException {
        byte[] b = null;
        GZIPInputStream gis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            gis = new GZIPInputStream(is);
            byte[] buf = new byte[1024];
            int num = -1;
            while ((num = gis.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }

            b = baos.toByteArray();
        } finally {
            if (gis != null) {
                gis.close();
            }

            baos.flush();
            baos.close();
        }

        return b;
    }

    /**
     * 从输入流中获取内容
     *
     * @param is 输入流
     * @throws IOException
     */
    public static String getInput2Str(InputStream is) throws IOException {
        String res = "";
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bis = new BufferedInputStream(is);
            int len = -1;
            byte[] buf = new byte[4 * 1024];
            while ((len = bis.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            res = baos.toString("UTF-8");
        } finally {
            if (bis != null) {
                bis.close();
            }

            baos.flush();
            baos.close();
        }

        return res;
    }

    /**
     * 从输入流中获取内容
     *
     * @param is 输入流
     * @throws IOException
     */
    public static byte[] getInput2Byte(InputStream is) throws IOException {
        byte[] b = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bis = new BufferedInputStream(is);
            int len = -1;
            byte[] buf = new byte[4 * 1024];
            while ((len = bis.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            b = baos.toByteArray();
        } finally {
            if (bis != null) {
                bis.close();
            }

            baos.flush();
            baos.close();
        }

        return b;
    }

    /**
     * 是否为乱码
     *
     * @param strName
     * @return
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {

                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否为中文字符
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
}
