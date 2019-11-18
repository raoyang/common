package com.game.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一日志输出格式
 *
 * @author heyuan
 */
public class LogFormat {

    private static final Logger LOG = LoggerFactory.getLogger(LogFormat.class);

    //接口访问出入口日志格式
    public static String ACCESS_FORMAT = "[{}] finish, time:{}ms, res:{}, urlParams:{}, body:{}, ip:{}";

    //WEB接口访问出入口日志格式
    public static String ACCESS_WEB_FORMAT = "[{}] finish, time:{}ms, res:{}, params:{}, ip:{}";

    /**
     * 接口出入参数日志打印
     *
     * @param apiUrl    接口地址
     * @param time      接口耗时
     * @param res       返回值
     * @param urlParams url参数
     * @param body      包体参数
     * @param ip        访问IP
     */
    public static void accessLog(String apiUrl, long time, String res, String urlParams, String body, String ip) {
        LOG.info(ACCESS_FORMAT, apiUrl, time, res, urlParams, body, ip);
    }

    /**
     * 接口出入参数日志打印
     *
     * @param apiUrl 接口地址
     * @param time   接口耗时
     * @param res    返回值
     * @param params 参数
     * @param ip     访问IP
     */
    public static void accessWebLog(String apiUrl, long time, String res, String params, String ip) {
        LOG.info(ACCESS_WEB_FORMAT, apiUrl, time, res, params, ip);
    }
}
