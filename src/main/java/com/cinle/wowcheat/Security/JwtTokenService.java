package com.cinle.wowcheat.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cinle.wowcheat.Constants.MyConst;
import com.cinle.wowcheat.Enum.RoleEnum;
import com.cinle.wowcheat.Model.Role;
import com.cinle.wowcheat.Service.RoleServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author JunLe
 * @Time 2022/2/24 23:07
 */
@Service
public class JwtTokenService {
    private final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RoleServices roleServices;

    private static final String KEY_HEAD = "TOKEN:";

    /**
     * 将uuid和role写入token
     * 适应于权限更新不频繁的，
     * 可以加redis，更新权限使token作废
     *
     * @param uuid
     * @param role
     * @return
     */
    public String createToken(String uuid, List<String> role) {
        String key = KEY_HEAD + uuid;
        String token = JWT.create()
                .withIssuer(MyConst.TOKEN_ISSUER)
                .withSubject(uuid) //签名标题
                .withClaim("role", role)
                .withIssuedAt(new Date())  //签名时间
                .withExpiresAt(new Date(System.currentTimeMillis() + MyConst.TOKEN_Expiration * 1000)) //过期时间
                .sign(Algorithm.HMAC256(MyConst.TOKEN_PRIVATE_KEY));
        //存入redis 7天
        setTokenToRedis(key,token);
        return token;
    }

    @Async
    void setTokenToRedis(String key, String token){
        redisTemplate.opsForValue().set(key, token, MyConst.TOKEN_Expiration, TimeUnit.SECONDS);
    }

    public Map getUserInfoFromToken(String token) {
        Map map = new HashMap();
        try {
            //只要密钥不变就能转换，密钥改变则报异常
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(MyConst.TOKEN_PRIVATE_KEY))
                    .withIssuer(MyConst.TOKEN_ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String uuid = decodedJWT.getSubject();
            List roles = decodedJWT.getClaim("role").asList(String.class);
            map.put("uuid", uuid);
            map.put("role", roles);
        } catch (Exception e) {
            log.info("Token:{} 解析失败! 原因: {}", token, e.getMessage());
            throw new JWTVerificationException("token解析失败，请尝试重新登录！");
        }
        return map;
    }

    public boolean CheckTokenByRedis(String uuid, String token) {
        String key = KEY_HEAD + uuid;
        String sign = (String) redisTemplate.opsForValue().get(key);
        String tt = sign == null?"_":sign;
        return tt.equals(token);
    }

    @Async
    public void RemoveTokenOnRedis(String uuid){
        String key = KEY_HEAD + uuid;
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    public boolean CheckToken(String token) {
        try {
            //只要密钥不变就能转换，密钥改变则报异常
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(MyConst.TOKEN_PRIVATE_KEY))
                    .withIssuer(MyConst.TOKEN_ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.info("Token:{} 解析失败! 原因: {}", token, e.getMessage());
            throw new JWTVerificationException("token解析失败，请尝试重新登录！");
        }
    }

    /**
     * @param token
     * @return 判断token是否过期
     */
    public boolean isTokenExpires(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(MyConst.TOKEN_PRIVATE_KEY))
                    .withIssuer(MyConst.TOKEN_ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Date Expires = decodedJWT.getExpiresAt();
            Date now = new Date();
            //当前时间是否大于过期时间
            return now.after(Expires);
        } catch (JWTVerificationException e) {
            log.info("Token:{} 解析失败! 原因: {}", token, e.getMessage());
            throw new JWTVerificationException("token解析失败，请尝试重新登录！");
        }
    }


    /**
     * @param token
     * @return 返回新token或空内容
     */
    public String isNeedFlushToken(String token) {
        String newToken = "";
        try {
            //只要密钥不变就能转换，密钥改变则报异常
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(MyConst.TOKEN_PRIVATE_KEY))
                    .withIssuer(MyConst.TOKEN_ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Date Expires = decodedJWT.getExpiresAt();
            Date now = new Date();
            long hours = (Expires.getTime() - now.getTime()) / (60 * 60 * 1000);
            //剩余时间不足24小时则生成新token返回
            if (hours < 24) {
                String uuid = decodedJWT.getSubject();
                //List roles = (List) decodedJWT.getClaim("role");
                //查找用户权限
                List<String> roles = new ArrayList<>();
                List<Role> r = roleServices.selectByUseruid(decodedJWT.getSubject());
                if (r == null || r.isEmpty()) {
                    roles.add(RoleEnum.NORMAL.getName());
                } else {
                    for (Role rs : r) {
                        roles.add(RoleEnum.getNameByIndex(rs.getRole()));
                    }
                }
                newToken = createToken(uuid, roles);
                log.info("用户: {} 返回新token......", uuid);
            }
        } catch (Exception e) {
            log.info("Token:{} 解析失败! 原因: {}", token, e.getMessage());
            throw new JWTVerificationException("token解析失败，请尝试重新登录！");
        }
        return newToken;
    }
}
