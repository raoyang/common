package com.game.util.pic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * http client 工具类，提供get,post工具
 */
public class HttpRequest {
    public static String httpGet(String url, String param, Map<String, String> header) {

        String result = "";
        BufferedReader in = null;

        try {

            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            //填充用户指定的header到
            for (String key : header.keySet()) {
                connection.setRequestProperty(key, header.get(key));
            }
            connection.connect();

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("httpGet failed" + e);
            e.printStackTrace();
        } finally {

            try {
                if (in != null) {
                    in.close();
                }

            } catch (Exception e2) {

                e2.printStackTrace();
            }
        }

        return result;
    }


    public static String httpGet(String url, String param) {

        Map<String, String> defaultMap = new HashMap<String, String>();

        defaultMap.put("Content-Type", "application/x-www-form-urlencoded");

        return httpGet(url, param, defaultMap);
    }


    public static String httpPost(String url, String body, Map<String, String> header) {

        PrintWriter out = null;

        BufferedReader in = null;

        String result = "";

        try {

            URL realUrl = new URL(url);

            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            for (String key : header.keySet()) {
                conn.setRequestProperty(key, header.get(key));
            }

            conn.setDoOutput(true);
            conn.setDoInput(true);

            out = new PrintWriter(conn.getOutputStream());
            out.print(body);
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {

            System.out.println("httpPost failed:" + e);

            e.printStackTrace();
        } finally {

            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

                ex.printStackTrace();
            }
        }

        return result;
    }

    public static String httpPost(String url, String body) {

        Map<String, String> defaultMap = new HashMap<String, String>();
        defaultMap.put("Content-Type", "application/x-www-form-urlencoded");

        return httpPost(url, body, defaultMap);
    }
}
