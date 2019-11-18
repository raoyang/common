package com.game.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;


/**
 * 远程客户端
 *
 * @author $heyuan$
 * @version $ 1.0 $
 */
public class IpUtil {
    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

    /**
     * unknown
     */
    private static final String UNKNOWN = "unknown";

    //~ Methods ----------------------------------------------------------------

    /**
     * 获取远程访问客户端IP地址
     *
     * @param request 请求对象
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;

        try {
            ip = request.getHeader("x-forwarded-for");

            if ((ip == null) || (ip.length() == 0)
                    || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }

            if ((ip == null) || (ip.length() == 0)
                    || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if ((ip == null) || (ip.length() == 0)
                    || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }

            if ((ip == null) || (ip.length() == 0)
                    || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if ((ip == null) || (ip.length() == 0)
                    || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }

            if ((ip == null) || (ip.length() == 0)
                    || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            if (ip != null) {
                String[] ips = ip.split(",");

                for (int i = 0; i < ips.length; i++) {
                    if ((null != ips[i]) && !"unknown".equalsIgnoreCase(ips[i])) {
                        ip = ips[i];
                        break;
                    }
                }
            }

        } catch (Exception e) {
            logger.warn("get client ip exception, msg:" + e.getMessage());
        }

        if (null == ip) {
            ip = "";
        }

        return ip;
    }
}
