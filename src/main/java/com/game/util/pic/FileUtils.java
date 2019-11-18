package com.game.util.pic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.CRC32;

/**
 * 文件工具类
 */
public class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 读取文件内容到字节数组
     *
     * @param fileName
     * @return
     */
    public static byte[] getFileContext(String fileName) {
        FileInputStream in = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                logger.error("file not exist, file:" + fileName);
                return null;
            }

            Long fileLength = file.length();
            byte[] fileContent = new byte[fileLength.intValue()];
            in = new FileInputStream(file);
            in.read(fileContent);

            return fileContent;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 获取文件的crc32 码
     *
     * @param contents 文件的字节
     * @return
     */
    public static String getCRC32(byte[] contents) {
        CRC32 crc32 = new CRC32();
        crc32.update(contents);
        return crc32.getValue() + "";
    }

    /**
     * 获取文件的crc32码
     *
     * @param file 文件
     * @return
     */
    public static String getCRC32(File file) {
        CRC32 crc32 = new CRC32();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                crc32.update(buffer, 0, length);
            }
            return crc32.getValue() + "";
        } catch (Exception ex) {
            logger.error("get file crc32 error!", ex);
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

    public static void saveFile(String localFileName, InputStream inputStream) {
        FileOutputStream out = null;
        try {

            String filepath = localFileName.substring(0, localFileName.lastIndexOf(File.separator));
            File file = new File(filepath);
            if (!file.exists()) {
                file.mkdirs();
            }

            file = new File(localFileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            out = new FileOutputStream(localFileName);
            byte[] readbuff = new byte[8 * 1024];
            int readlen = 0;
            long downlen = 0;
            while ((readlen = inputStream.read(readbuff, 0, 8 * 1024)) != -1) {
                out.write(readbuff, 0, readlen);
                downlen += readlen;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String genImgUrl(String url, String type) {
        String host = "http://p0.qhimg.com";
        int width = 0;
        int height = 0;
        int quality = 95;
        StringBuilder bd = new StringBuilder();
        switch (type) {
            case "org":
                bd.append(host).append("/").append(url);
                break;
            case "mid":
                quality = 80;
                break;
            case "wallpaper_small":
                quality = 80;
                width = 540;
                height = 480;
                break;
            case "single_small":
                quality = 80;
                width = 270;
                height = 480;
                break;
        }
        bd.append(host).append("/dm/").append(width).append("_").append(height).append("_").append(quality).append("/").append(url);
        return bd.toString();
    }
}
