package com.game.chat.util;

import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class JsonUtils {

    private static final Gson gson = new Gson();

    private static final Charset charset = Charset.forName("utf-8");

    public static String objectToString(Object obj){
        return gson.toJson(obj);
    }

    public static <T> T stringToObject(String src, Class<T> clazz){
        return gson.fromJson(src, clazz);
    }

    public static <T> List<T> stringToList(String src, Class<T[]> clazz){
        T[] array = gson.fromJson(src, clazz);
        return Arrays.asList(array);
    }

    public static <T> T bytesToObject(byte[] bytes, Class<T> clazz){
        String src = new String(bytes, charset);
        return stringToObject(src, clazz);
    }
}
