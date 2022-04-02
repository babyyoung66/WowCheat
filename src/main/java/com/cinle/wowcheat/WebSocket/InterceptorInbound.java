package com.cinle.wowcheat.WebSocket;

import com.cinle.wowcheat.Exception.TokenException;
import com.cinle.wowcheat.Security.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author JunLe
 * @Time 2022/3/22 20:20
 * 获取socket相关信息做判断
 * 执行顺序preSend(入站时) => postSend(发送消息时) => afterSendCompletion(处理完成时)
 */
@Component
public class InterceptorInbound implements ChannelInterceptor {

    @Autowired
    JwtTokenService tokenService;


    private Logger log = LoggerFactory.getLogger(InterceptorInbound.class);

    /**
     * preSend里当连接方式为CONNECT的时候获取用户认证信息
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //连接时
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                SocketUserPrincipal user = getUserPrincipal(accessor);
                accessor.setUser(user);
                return message;
            } catch (TokenException e) {
                //token解析失败
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        //断开时
        if (accessor != null && accessor.getUser() != null  && StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            //log.info(accessor.getUser().getName() + "已断开websocket服务..." );
            //移除在线信息
            return null;
        }

        //判断订阅
        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            //订阅是否合法
            String userName = accessor.getUser().getName();
            String Destination = accessor.getDestination();
            String userDes = SocketConstants.USER_SUBSCRIBE_Prefix + userName + SocketConstants.USER_SUBSCRIBE_Suffix;
            //不能订阅除自己id和topic外的链接
            if (userDes.equals(Destination) || SocketConstants.TOPIC_SUBSCRIBE.equals(Destination)) {
                //log.info("用户: {} 成功订阅: {}", accessor.getUser().getName(), accessor.getDestination());
                return message;
            } else {
                // 如果该用户订阅的频道不合法直接返回null前端用户就接受不到该频道信息
                log.warn("用户: {} 尝试订阅: {} 失败,订阅的内容不符合条件！", accessor.getUser().getName(), accessor.getDestination());
                throw new IllegalArgumentException("订阅的内容不符合条件！");
            }

        }
        //发送时，检查时间间隔
        if (accessor != null && StompCommand.SEND.equals(accessor.getCommand())) {
            SocketUserPrincipal userPrincipal = (SocketUserPrincipal) accessor.getUser();
            //更新消息
            SocketUserPrincipal user = CheckLimitUtils.checkLimit(userPrincipal);
            accessor.setUser(user);
        }

        return message;
    }

    /**
     * 从token中获取用户信息
     *
     * @param accessor
     * @return
     * @throws TokenException
     */
    private SocketUserPrincipal getUserPrincipal(StompHeaderAccessor accessor) throws TokenException {
        //获取token
        List<String> nativeHeader = accessor.getNativeHeader("token");
        if (nativeHeader != null && !nativeHeader.isEmpty()) {
            String token = nativeHeader.get(0);
            if (StringUtils.hasText(token)) {
                tokenService.CheckToken(token);
                String uuid = tokenService.getUserId(token);
                SocketUserPrincipal user = new SocketUserPrincipal();
                user.setName(uuid);
                //获取权限保存在当前Principal中
                List<String> roles = tokenService.getUserRoles(token);
                user.setRoles(roles);
                //token存入请求体
                user.setToken(token);
                return user;
            }
        } else if (accessor.getUser() != null) {
            //没有token请求头则从验证体principal获取token
            SocketUserPrincipal principal = (SocketUserPrincipal) accessor.getUser();
            tokenService.CheckToken(principal.getToken());
            return (SocketUserPrincipal) accessor.getUser();
        }

        return null;
    }

    /**
     * 放行的连接类型
     */
    private List ignoreCommand = new ArrayList(Arrays.asList(StompCommand.CONNECT,StompCommand.ERROR,StompCommand.DISCONNECT,StompCommand.UNSUBSCRIBE));

    /**
     * @param message
     * @param channel
     * @param sent
     */
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //不是连接请求时验证token
        if (!ignoreCommand.contains(accessor.getCommand())) {
            try {
                //发送前再次判断token是否正常
                SocketUserPrincipal principal = getUserPrincipal(accessor);
            } catch (TokenException e) {
                accessor.setUser(null);
                //throw new IllegalArgumentException(e.getMessage());
            }
        }

    }
}
