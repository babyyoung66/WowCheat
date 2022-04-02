package com.cinle.wowcheat.WebSocket;

import java.util.Date;

/**
 * @Author JunLe
 * @Time 2022/3/30 14:45
 * 校验消息发送限制,更新当用户发送记录
 * 在Interceptor中重新设置user属性
 */
public class CheckLimitUtils {
    private CheckLimitUtils() {
    }

    public static SocketUserPrincipal checkLimit(SocketUserPrincipal principal) {
        int total = principal.getLimitTotal();
        if (total == 0) {
            principal.setLastSendTime(new Date());
            principal.setLimitTotal(total + 1);
            return principal;
        }
        principal.setLimitTotal(total + 1);
        if (total < SocketConstants.LIMIT_TOTAL){
            return principal;
        }
        Date now = new Date();
        Date last = principal.getLastSendTime();
        //两次间隔超过限制时间则重置记录
        if (now.getTime() - last.getTime() > SocketConstants.LIMIT_SECOND) {
            principal.setLimitTotal(0);
        }
        return principal;
    }
}
