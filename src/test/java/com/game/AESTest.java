package com.game;

import com.game.util.AESUtil;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class AESTest {
    public static void main(String args[]) {
        String encodeString = "0QWAxGzgAqD09ZmC8u7ren5HKdhqEuLyGMsjJ%2F2KgCO8QZmJ35guKUTFm0%2Balb76fppszm%2FmKvPDs8J6j42Evwjqjhwl2TMn26wJrMlSxv2LJGghDdcIWehLK7ukJ7oWUTKMdx%2BkT%2F8PmPEbZ1Knd9c8BJPfFiPLxBl7TqW6%2FV9f%2BZ5gJT%2F6qMZ%2FSnTBjouEoqP0xvJHggsCWsFOe8jYT1Cgkeav1gOnW%2BXiwZ9JcN28MburL7IWm6ak195JOfWyQS85ltQ%3D";
        String urlDecode = URLDecoder.decode(encodeString);
        //AES秘钥
        String key = "1231";
        String result = "";
        try {
            result = AESUtil.decrypt(urlDecode, Long.parseLong(key));
        } catch (Exception $e) {

        }

        System.out.println(result);

        String oriString = "{\"devModel\":\"1501\", \"netType\":0, \"ch\":\"\", \"devMem\":2, \"devMfr\":\"\", \"isAbroad\":1, \"lan\":\"zhCN\"}";
        try {
            String aes = AESUtil.encrypt(oriString);
            String encodeS =  URLEncoder.encode(aes, "UTF-8");
            System.out.println(encodeS);
        } catch (Exception e) {

        }


    }
}
