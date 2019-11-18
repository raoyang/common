package com.game.firebase;

import com.game.firebase.base.Message;
import com.game.firebase.base.Notification;
import com.game.firebase.base.Response;
import com.game.firebase.base.FirebaseResult;
import com.game.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class FireBaseSenderTest {

//    @Value("key=" + "${firebase.server_key}")
    String firebaseServerKey = "key=" + "AAAAFf6isjA:APA91bFreFldkuE3TDiokZf5zqrcDWH3rtOxbSsLX67o81quGUwSGqjAokpP15bTOxaWXRkutoGu1979ypbNL-X9D8xkGQmzVgmviFysbikQAUaW2iGS94KtupHVf9hJ8pA00INECfe2";

//    @Value("${firebase.server_http_url}")
    String firebaseUrl = "https://fcm.googleapis.com/fcm/send";


    String clientToken = "c6QxRbdmcYk:APA91bEg_iTw5RPkqKIu0QM8qXADywqntVSgcvt9NCLTu6B5Z5L166eGr_bOzasLlOZQ4_zMnwgh1xc7kiA7p3FUWtFRl34OP_i2DgELgJpw_6YGl4RLWYD3I-eiK7aYhEyc7mi9OctZ";


    public String sendData() {

        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", Constant.PROXY_HOST);
        System.setProperty("http.proxyPort", "" + Constant.PROXY_PORT);
        // 针对https也开启代理
        System.setProperty("https.proxyHost", Constant.PROXY_HOST);
        System.setProperty("https.proxyPort", "" + Constant.PROXY_PORT);

        Message message = new Message();
        message.to = clientToken;
//        message.recipients = new ArrayList<String>();
//        message.recipients.add(clientToken);
//        message.recipients.add("jdaslfkjdaslfkjsadfl;kasjdf;lasjkdf;sldkjfsd;alfkjas;l");
        message.notification = new Notification();
        message.notification.title = "asdfasdfasdf;";

        Gson gson = new GsonBuilder().setLenient().create();
        String json = gson.toJson(message);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", firebaseServerKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(firebaseUrl,entity,String.class);
        String content = response.getBody();

//        FirebaseResult responseResult = new FirebaseResult();
//
////        responseResult.response = gson.fromJson(content, Response.class);

        return content;
    }


    public static void main(String[] args) {
        FireBaseSenderTest test = new FireBaseSenderTest();
        String response = test.sendData();

        System.out.println(response);

    }

}
