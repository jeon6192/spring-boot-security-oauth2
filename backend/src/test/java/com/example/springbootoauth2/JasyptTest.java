package com.example.springbootoauth2;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JasyptTest {
    @Test
    void jasyptTest() {
        String password = System.getenv("JASYPT_KEY");

        String googleId = "";
        String googleSecret = "";

        String naverId = "";
        String naverSecret = "";

        String kakaoId = "";
        String kakaoSecret = "";

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        // google
        String encryptGoogleId = encryptor.encrypt(googleId);
        System.out.println("encryptGoogleId : " + encryptGoogleId);
        String decryptGoogleId = encryptor.decrypt(encryptGoogleId);
        System.out.println("decryptGoogleId : " + decryptGoogleId);

        org.junit.jupiter.api.Assertions.assertEquals(googleId, decryptGoogleId);

        String encryptGoogleSecrect = encryptor.encrypt(googleSecret);
        System.out.println("encryptGoogleSecret : " + encryptGoogleSecrect);
        String decryptGoogleSecret = encryptor.decrypt(encryptGoogleSecrect);
        System.out.println("decryptGoogleSecret : " + decryptGoogleSecret);

        org.junit.jupiter.api.Assertions.assertEquals(googleSecret, decryptGoogleSecret);

        // naver
        String encryptNaverId = encryptor.encrypt(naverId);
        System.out.println("encryptNaverId : " + encryptNaverId);
        String decryptNaverId = encryptor.decrypt(encryptNaverId);
        System.out.println("decryptNaverId : " + decryptNaverId);

        org.junit.jupiter.api.Assertions.assertEquals(naverId, decryptNaverId);

        String encryptNaverSecrect = encryptor.encrypt(naverSecret);
        System.out.println("encryptNaverSecret : " + encryptNaverSecrect);
        String decryptNaverSecret = encryptor.decrypt(encryptNaverSecrect);
        System.out.println("decryptNaverSecret : " + decryptNaverSecret);

        org.junit.jupiter.api.Assertions.assertEquals(naverSecret, decryptNaverSecret);

        // kakao
        String encryptKakaoId = encryptor.encrypt(kakaoId);
        System.out.println("encryptKakaoId : " + encryptKakaoId);
        String decryptKakaoId = encryptor.decrypt(encryptKakaoId);
        System.out.println("decryptKakaoId : " + decryptKakaoId);

        org.junit.jupiter.api.Assertions.assertEquals(kakaoId, decryptKakaoId);

        String encryptKakaoSecrect = encryptor.encrypt(kakaoSecret);
        System.out.println("encryptKakaoSecret : " + encryptKakaoSecrect);
        String decryptKakaoSecret = encryptor.decrypt(encryptKakaoSecrect);
        System.out.println("decryptKakaoSecret : " + decryptKakaoSecret);

        org.junit.jupiter.api.Assertions.assertEquals(kakaoSecret, decryptKakaoSecret);
    }
}
