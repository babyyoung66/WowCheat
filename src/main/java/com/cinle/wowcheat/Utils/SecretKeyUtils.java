package com.cinle.wowcheat.Utils;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @Author JunLe
 * @Time 2022/2/25 10:24
 * 生成加密 秘钥和公钥
 */
public class SecretKeyUtils {

    //数字签名
    public static final String KEY_ALGORITHM = "RSA";

    //RSA密钥长度
    public static final int KEY_SIZE = 1024;

    //唯一秘钥实例
    private static volatile RSA256key RSA_256_KEY;

    private SecretKeyUtils() {
    }

    /**
     * @return RSA256key
     * @throws NoSuchAlgorithmException 双重校验锁返回唯一实例
     */
    public static RSA256key generateRAS256Keys() throws NoSuchAlgorithmException {
        if (RSA_256_KEY == null) {
            synchronized (RSA256key.class) {
                if (RSA_256_KEY == null) {
                    RSA_256_KEY = new RSA256key();
                    //密钥生成所需的随机数源
                    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
                    keyPairGen.initialize(KEY_SIZE);
                    //通过KeyPairGenerator生成密匙对KeyPair
                    KeyPair keyPair = keyPairGen.generateKeyPair();
                    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
                    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
                    RSA_256_KEY.setPublicKey(publicKey);
                    RSA_256_KEY.setPrivateKey(privateKey);
                }
            }
        }
        return RSA_256_KEY;
    }


    public static class RSA256key {
        private RSAPublicKey publicKey;
        private RSAPrivateKey privateKey;

        public RSAPublicKey getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(RSAPublicKey publicKey) {

            this.publicKey = publicKey;
        }

        public RSAPrivateKey getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(RSAPrivateKey privateKey) {
            this.privateKey = privateKey;
        }
    }
}


