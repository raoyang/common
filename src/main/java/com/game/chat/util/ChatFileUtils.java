package com.game.chat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.LongAdder;

public class ChatFileUtils {
    private static final LongAdder longAddr = new LongAdder();

    /***
     * 上传文件
     * @param file
     * @param filePath
     * @param fileName
     * @throws Exception
     */
    public static String uploadFile(byte[] file, String filePath, String fileName)
            throws  Exception{
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        String path = "";
        StringBuilder sb = new StringBuilder();
        if(filePath.endsWith("/")){
            sb.append(filePath)
                    .append(fileName);
            path = sb.toString();
        }else{
            sb.append(filePath)
                    .append("/")
                    .append(fileName);
            path = sb.toString();
        }
        FileOutputStream out = new FileOutputStream(path);
        out.write(file);
        out.flush();
        out.close();
        return path;
    }

    /***
     * 文件名生成器
     * @return
     */
    public static String fileNameCreator(){
        long time = System.currentTimeMillis();
        if(longAddr.sum() >= Long.MAX_VALUE){
            longAddr.reset();
        }
        longAddr.increment();
        long value = longAddr.sum();
        StringBuilder sb = new StringBuilder();
        sb.append(value)
                .append(time)
                .append(".")
                .append("ext");
        return sb.toString();
    }
}
