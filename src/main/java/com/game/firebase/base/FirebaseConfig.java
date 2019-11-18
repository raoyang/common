package com.game.firebase.base;


import com.game.util.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Configuration
public class FirebaseConfig {

    @Value("${firebase.server_key}")
    String serverKey;


    @Value("${is.proxy}")
    int isProxy;

    @Bean
    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "key=" + serverKey);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    @Bean
    RestTemplate getRestTemplate() {

        if (isProxy == 1) {
            System.setProperty("http.proxySet", "true");
            System.setProperty("http.proxyHost", Constant.PROXY_HOST);
            System.setProperty("http.proxyPort", "" + Constant.PROXY_PORT);
            // 针对https也开启代理
            System.setProperty("https.proxyHost", Constant.PROXY_HOST);
            System.setProperty("https.proxyPort", "" + Constant.PROXY_PORT);
        }

        RestTemplate restTemplate =  new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            public void handleError(ClientHttpResponse response) throws IOException {

            }
        });
        return restTemplate;
    }
}