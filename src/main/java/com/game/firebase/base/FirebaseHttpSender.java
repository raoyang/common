package com.game.firebase.base;

import com.game.util.CollectionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class FirebaseHttpSender {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseHttpSender.class);

    @Value("${firebase.server_http_url}")
    String firebaseUrl;

    @Autowired
    HttpHeaders httpHeaders;

    @Autowired
    RestTemplate restTemplate;


    public FirebaseResult send(Message message) {
        FirebaseResult firebaseResult = sendMessage(message);
        return analysisResult(message, firebaseResult);
    }

    FirebaseResult analysisResult(Message message, FirebaseResult responseResult) {
        Map<String, Result> resultMap = new HashMap<>();
        if(responseResult.response.failure > 0) {
            if (!CollectionUtil.isEmpty(message.recipients)) {

                Iterator<String> recipientsIter = message.recipients.iterator();
                Iterator<Result> resultIter = responseResult.response.results.iterator();

                while (recipientsIter.hasNext() && resultIter.hasNext()) {
                    String recipient = recipientsIter.next();
                    Result result = resultIter.next();

                    // find an error
                    if (result != null && !StringUtils.isEmpty(result.error)) {
                        //TODO storage the error and recipient into on pair
                        //TODO like <recipient, error>
                        resultMap.put(recipient, result);
                    }
                }

            } else if (!StringUtils.isEmpty(message.to)){

                Result errorResult = responseResult.response.results.get(0);
                //TODO storage the error and to into on pair
                //TODO like <to, error>
                if (errorResult != null) {
                    resultMap.put(message.to,errorResult);
                }

            } else {
                // there is something wrong ,the recipients and to are null at same time!
            }

        }
        responseResult.errorMessage = resultMap;
        return responseResult;
    }


    FirebaseResult sendMessage(Message message) {
        logger.debug("sendmessage target " + message.to);
        Gson gson = new GsonBuilder().setLenient().create();
        String json = gson.toJson(message);
        HttpEntity<String> entity = new HttpEntity<>(json, httpHeaders);
        ResponseEntity<String> fcmResponse = restTemplate.postForEntity(firebaseUrl, entity, String.class);
        int status = fcmResponse.getStatusCodeValue();
        String responseBody = fcmResponse.getBody();

        FirebaseResult responseResult = new FirebaseResult();
        responseResult.responseCode = status;
        responseResult.response = gson.fromJson(responseBody, Response.class);
        logger.debug("sendmessage status " + status);
        return responseResult;
    }
}
