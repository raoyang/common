package com.game.util.curl;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class RestClient {
    @Resource
    RestTemplate restTemplate;

    /**
     *      * get
     *      * @param url 请求地址
     *      * @param param  参数
     *      * @param returnClass 返回类型
     *      * @return
     *      
     */
    public <T> T get(String url, Class<T> returnClass, Map<String, ?> param) {
        return restTemplate.getForObject(url, returnClass, param);
    }

    /**
     * GET - 无参
     * @param url
     * @param returnClass
     * @param <T>
     * @return
     */
    public <T> T getDefault(String url, Class<T> returnClass) {
        return restTemplate.getForObject(url, returnClass);
    }

    /**
     *      * post
     *      * @param url 请求地址
     *      * @param param 参数
     *      * @param returnClass 返回类型
     *      * @param header 自定义的头信息
     *      * @return
     *      
     */
    public <E> E post(String url, E param, Class<E> returnClass, Map<String, String> header) {
        HttpHeaders headers = new HttpHeaders();
        header.forEach((o1, o2) -> headers.set(o1, o2));
        HttpEntity<E> httpEntity = new HttpEntity<E>(param, headers);
        return restTemplate.postForObject(url, httpEntity, returnClass);
    }

    /**
     *      * post
     *      * @param url 请求地址
     *      * @param param 参数
     *      * @param returnClass 返回类型
     *      * @return
     *      
     */
    public <E> E postByDefault(String url, E param, Class<E> returnClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        HttpEntity<E> httpEntity = new HttpEntity<E>(param, headers);
        return restTemplate.postForObject(url, httpEntity, returnClass);
    }
}
