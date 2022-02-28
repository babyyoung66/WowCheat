package com.cinle.wowcheat.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cinle.wowcheat.Constants.MyContans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author JunLe
 * @Time 2022/2/24 23:07
 */
@Service
public class JwtTokenService {
    private final  Logger log = LoggerFactory.getLogger(JwtTokenService.class);


    /**
     * 将uuid和role写入token
     * 适应于权限更新不频繁的，
     * 可以加redis，更新权限使token作废
     * @param Username
     * @param role
     * @return
     */
    public  String createToken(String Username, List<String> role) {
        String token = JWT.create()
                .withIssuer(MyContans.TOKEN_ISSUER)
                .withSubject(Username) //签名标题
                .withClaim("role", role)
                .withIssuedAt(new Date())  //签名时间
                .withExpiresAt(new Date(System.currentTimeMillis() + MyContans.TOKEN_Expiration)) //过期时间
                .sign(Algorithm.HMAC256(MyContans.TOKEN_PRIVATE_KEY));
        return token;
    }

    public  Map getUserInfoFromToken(String token) {
        Map map = new HashMap();
        try {
            //只要密钥不变就能转换，密钥改变则报异常
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(MyContans.TOKEN_PRIVATE_KEY))
                    .withIssuer(MyContans.TOKEN_ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String username = decodedJWT.getSubject();
            List roles = decodedJWT.getClaim("role").asList(String.class);
            map.put("username", username);
            map.put("role", roles);
        } catch (Exception e) {
            log.info("Token:{} 解析失败! 原因: {}", token, e.getMessage());
        }
        return map;
    }

    public  boolean CheckTokenSing(String token) {
        try {
            //只要密钥不变就能转换，密钥改变则报异常
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(MyContans.TOKEN_PRIVATE_KEY))
                    .withIssuer(MyContans.TOKEN_ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.info("Token:{} 解析失败! 原因: {}", token, e.getMessage());
        }
        return false;
    }

    /**
     * @param token
     * @return 判断token是否过期
     */
    public  boolean isTokenExpires(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(MyContans.TOKEN_PRIVATE_KEY))
                    .withIssuer(MyContans.TOKEN_ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Date Expires = decodedJWT.getExpiresAt();
            Date now = new Date();
            return now.after(Expires);
        } catch (JWTVerificationException e) {
            log.info("Token:{} 解析失败! 原因: {}", token, e.getMessage());
        }
        return true;
    }


    /**
     * @param token
     * @return 返回新token或空内容
     */
    public  String isNeedFlushToken(String token) {
        String newToken = "";
        try {
            //只要密钥不变就能转换，密钥改变则报异常
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(MyContans.TOKEN_PRIVATE_KEY))
                    .withIssuer(MyContans.TOKEN_ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Date Expires = decodedJWT.getExpiresAt();
            Date now = new Date();
            long hours = (Expires.getTime() - now.getTime()) / (60 * 60 * 1000);
            //剩余时间不足24小时则生成新token返回
            if (hours < 24) {
                String name = decodedJWT.getSubject();
                List roles = (List) decodedJWT.getClaim("role");
                newToken = createToken(name, roles);
                log.info("用户: {} 返回新token......", name);
            }
        } catch (JWTVerificationException e) {
            log.info("Token:{} 解析失败! 原因: {}", token, e.getMessage());
        }
        return newToken;
    }
}
