package com.cinle.wowcheat.Utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;

/**
 * @Author JunLe
 * @Time 2022/4/9 14:03
 */
public class IpUtils {
    private IpUtils() {
    }

    public static String getRealIp(HttpServletRequest request) {

        String unknown = "unknown";

        //String ip0 = request.getHeader("x-forwarded-for");
        String ip = request.getHeader("X-Real-IP");

        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        //没有使用Nginx转发时，返回原信息
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;

    }
}
