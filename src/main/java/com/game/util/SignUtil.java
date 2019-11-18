package com.game.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;

/**
 * 方法签名认证
 *
 */
public class SignUtil {

    private static final String DEFAULT_KEY = "16ad335611635c1655318e6aa64e9a93";

    public static boolean validateSign(Map<String, String> params) throws UnsupportedEncodingException {
        String sign = params.remove("sign");
        List<Map<String, String>> data = Lists.newArrayList();
        for(String key : params.keySet()) {
            String value = params.get(key);
            value = URLDecoder.decode(value, "UTF-8");
            if(StringUtil.isBlank(value)) {
                continue;
            }
            Map<String, String> m = Maps.newHashMapWithExpectedSize(1);
            data.add(m);
            m.put(key, value);
        }
        //仅对值进行排序
        Ordering<Map<String, String>> ordering = Ordering.from(new Comparator<Map<String, String>>() {

            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                String val1 = o1.keySet().toArray(new String[0])[0];
                String val2 = o2.keySet().toArray(new String[0])[0];
                return val1.compareTo(val2);
            }
        }).reverse();

        List<Map<String, String>> sorted = ordering.sortedCopy(data);

        StringBuilder sb = new StringBuilder();
        //排序后数据进行拼接加密
        for(String key : sorted.get(0).keySet()) {

            sb.append(key);
            sb.append(sorted.get(0).get(key));
        }

        List<Map<String, String>> over = sorted.subList(1, sorted.size());
        for(int i=0; i<over.size(); i++) {

            Map<String, String> sm = over.get(i);
            for(String key : sm.keySet()) {

                sb.append("&");
                sb.append(key);
                sb.append(sm.get(key));
            }
        }
        sb.append("&");
        sb.append(DEFAULT_KEY);

        if(new MD5().toMD5Str(sb.toString()).equalsIgnoreCase(sign)) {
            return true;
        }
        return false;
    }

    public static boolean validateSignByKey(Map<String, String> params, String signKey) throws Exception{
        String sign = params.remove("sign");
        //仅对值进行排序
        Map<String, String> sortBy = sortByValue(params);
        StringBuilder sb = new StringBuilder();
        for (String key : sortBy.keySet()) {
            sb.append(key).append(sortBy.get(key)).append("&");
        }

        String signStr = sb.append(signKey).toString();

        return new MD5().toMD5Str(signStr).equalsIgnoreCase(sign);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    public static String buildSign(Map<String, String> params, String signKey) {
        Map<String, String> sortBy = sortByValue(params);
        System.out.println(sortBy);
        StringBuilder sb = new StringBuilder();
        for (String key : sortBy.keySet()) {
            sb.append(key).append(sortBy.get(key)).append("&");
        }

        String signStr = sb.append(signKey).toString();
        System.out.println(signStr);
        return new MD5().toMD5Str(signStr);
    }
}