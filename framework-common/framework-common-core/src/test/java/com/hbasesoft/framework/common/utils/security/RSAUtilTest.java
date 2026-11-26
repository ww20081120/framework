/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.UtilException;

/**
 * RSAUtil 工具类测试<br>
 * 测试覆盖率目标：100% 公共方法<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @CreateDate 2023年3月17日 <br>
 * @see com.hbasesoft.framework.common.utils.security.RSAUtil
 */
@DisplayName("RSAUtil 工具类测试")
public class RSAUtilTest {

    @Test
    @DisplayName("获取密钥对 - KeyPair对象")
    void testGetKeyPair() {
        // When
        KeyPair keyPair = RSAUtil.getKeyPair();

        // Then
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getPublic()).isNotNull();
        assertThat(keyPair.getPrivate()).isNotNull();
    }

    @Test
    @DisplayName("创建字符串密钥对 - StrKeyPair对象")
    void testCreateKeyPair() {
        // When
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();

        // Then
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getPublicKey()).isNotNull().isNotEmpty();
        assertThat(keyPair.getPrivateKey()).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("获取私钥对象 - 从Base64字符串")
    void testGetPrivateKey() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();

        // When
        PrivateKey privateKey = RSAUtil.getPrivateKey(keyPair.getPrivateKey());

        // Then
        assertThat(privateKey).isNotNull();
        assertThat(privateKey.getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    @DisplayName("获取公钥对象 - 从Base64字符串")
    void testGetPublicKey() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();

        // When
        PublicKey publicKey = RSAUtil.getPublicKey(keyPair.getPublicKey());

        // Then
        assertThat(publicKey).isNotNull();
        assertThat(publicKey.getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    @DisplayName("RSA加密 - 使用字符串公钥")
    void testEncryptWithPublicKeyString() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String originalData = "Hello RSA!";

        // When
        String encrypted = RSAUtil.encrypt(originalData, keyPair.getPublicKey());

        // Then
        assertThat(encrypted).isNotNull().isNotEmpty();
        assertThat(encrypted).isNotEqualTo(originalData);
    }

    @Test
    @DisplayName("RSA加密 - 使用PublicKey对象")
    void testEncryptWithPublicKeyObject() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String originalData = "Hello RSA!";
        PublicKey publicKey = RSAUtil.getPublicKey(keyPair.getPublicKey());

        // When
        String encrypted = RSAUtil.encrypt(originalData, publicKey);

        // Then
        assertThat(encrypted).isNotNull().isNotEmpty();
        assertThat(encrypted).isNotEqualTo(originalData);
    }

    @Test
    @DisplayName("RSA解密 - 使用字符串私钥")
    void testDecryptWithPrivateKeyString() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String originalData = "Hello RSA!";
        String encrypted = RSAUtil.encrypt(originalData, keyPair.getPublicKey());

        // When
        String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivateKey());

        // Then
        assertThat(decrypted).isEqualTo(originalData);
    }

    @Test
    @DisplayName("RSA解密 - 使用PrivateKey对象")
    void testDecryptWithPrivateKeyObject() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String originalData = "Hello RSA!";
        String encrypted = RSAUtil.encrypt(originalData, keyPair.getPublicKey());
        PrivateKey privateKey = RSAUtil.getPrivateKey(keyPair.getPrivateKey());

        // When
        String decrypted = RSAUtil.decrypt(encrypted, privateKey);

        // Then
        assertThat(decrypted).isEqualTo(originalData);
    }

    @Test
    @DisplayName("RSA加密解密完整流程 - 短文本")
    void testEncryptDecryptShortText() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String originalData = "Short text";

        // When
        String encrypted = RSAUtil.encrypt(originalData, keyPair.getPublicKey());
        String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivateKey());

        // Then
        assertThat(decrypted).isEqualTo(originalData);
    }

    @Test
    @DisplayName("RSA加密解密完整流程 - 长文本（分段处理）")
    void testEncryptDecryptLongText() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("This is a long text for testing RSA encryption. ");
        }
        String originalData = sb.toString();

        // When
        String encrypted = RSAUtil.encrypt(originalData, keyPair.getPublicKey());
        String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivateKey());

        // Then
        assertThat(decrypted).isEqualTo(originalData);
    }

    @Test
    @DisplayName("RSA加密解密 - 中文字符")
    void testEncryptDecryptChineseCharacters() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String originalData = "你好，RSA加密测试！这是中文字符。";

        // When
        String encrypted = RSAUtil.encrypt(originalData, keyPair.getPublicKey());
        String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivateKey());

        // Then
        assertThat(decrypted).isEqualTo(originalData);
    }

    @Test
    @DisplayName("RSA签名 - 使用字符串私钥")
    void testSignWithPrivateKeyString() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String data = "Data to be signed";

        // When
        String signature = RSAUtil.sign(data, keyPair.getPrivateKey());

        // Then
        assertThat(signature).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("RSA签名 - 使用PrivateKey对象")
    void testSignWithPrivateKeyObject() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String data = "Data to be signed";
        PrivateKey privateKey = RSAUtil.getPrivateKey(keyPair.getPrivateKey());

        // When
        String signature = RSAUtil.sign(data, privateKey);

        // Then
        assertThat(signature).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("RSA验签 - 使用字符串公钥")
    void testVerifyWithPublicKeyString() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String data = "Data to be signed";
        String signature = RSAUtil.sign(data, keyPair.getPrivateKey());

        // When
        boolean verified = RSAUtil.verify(data, keyPair.getPublicKey(), signature);

        // Then
        assertThat(verified).isTrue();
    }

    @Test
    @DisplayName("RSA验签 - 使用PublicKey对象")
    void testVerifyWithPublicKeyObject() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String data = "Data to be signed";
        String signature = RSAUtil.sign(data, keyPair.getPrivateKey());
        PublicKey publicKey = RSAUtil.getPublicKey(keyPair.getPublicKey());

        // When
        boolean verified = RSAUtil.verify(data, publicKey, signature);

        // Then
        assertThat(verified).isTrue();
    }

    @Test
    @DisplayName("RSA签名验签完整流程 - 验证成功")
    void testSignAndVerifySuccess() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String data = "Important data that needs signature";

        // When
        String signature = RSAUtil.sign(data, keyPair.getPrivateKey());
        boolean verified = RSAUtil.verify(data, keyPair.getPublicKey(), signature);

        // Then
        assertThat(verified).isTrue();
    }

    @Test
    @DisplayName("RSA签名验签 - 数据被篡改应验签失败")
    void testSignAndVerifyTamperedData() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String originalData = "Original data";
        String tamperedData = "Tampered data";
        String signature = RSAUtil.sign(originalData, keyPair.getPrivateKey());

        // When
        boolean verified = RSAUtil.verify(tamperedData, keyPair.getPublicKey(), signature);

        // Then
        assertThat(verified).isFalse();
    }

    @Test
    @DisplayName("RSA签名验签 - 不同数据签名应不同")
    void testSignDifferentData() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String data1 = "First data";
        String data2 = "Second data";

        // When
        String signature1 = RSAUtil.sign(data1, keyPair.getPrivateKey());
        String signature2 = RSAUtil.sign(data2, keyPair.getPrivateKey());

        // Then
        assertThat(signature1).isNotEqualTo(signature2);
    }

    @Test
    @DisplayName("使用公钥加密私钥解密 - 完整流程")
    void testPublicEncryptPrivateDecrypt() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String originalData = "Confidential message";

        // When
        String encrypted = RSAUtil.encrypt(originalData, keyPair.getPublicKey());
        String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivateKey());

        // Then
        assertThat(decrypted).isEqualTo(originalData);
    }

    @Test
    @DisplayName("获取私钥 - 无效Base64应抛出异常")
    void testGetPrivateKeyWithInvalidBase64() {
        // Given
        String invalidPrivateKey = "invalid-base64-string";

        // When & Then
        assertThatThrownBy(() -> RSAUtil.getPrivateKey(invalidPrivateKey))
            .isInstanceOf(UtilException.class);
    }

    @Test
    @DisplayName("获取公钥 - 无效Base64应抛出异常")
    void testGetPublicKeyWithInvalidBase64() {
        // Given
        String invalidPublicKey = "invalid-base64-string";

        // When & Then
        assertThatThrownBy(() -> RSAUtil.getPublicKey(invalidPublicKey))
            .isInstanceOf(UtilException.class);
    }

    @Test
    @DisplayName("RSA加密 - 空字符串")
    void testEncryptEmptyString() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String emptyData = "";

        // When
        String encrypted = RSAUtil.encrypt(emptyData, keyPair.getPublicKey());
        String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivateKey());

        // Then
        assertThat(decrypted).isEqualTo(emptyData);
    }

    @Test
    @DisplayName("RSA签名 - 空字符串")
    void testSignEmptyString() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String emptyData = "";

        // When
        String signature = RSAUtil.sign(emptyData, keyPair.getPrivateKey());
        boolean verified = RSAUtil.verify(emptyData, keyPair.getPublicKey(), signature);

        // Then
        assertThat(verified).isTrue();
    }

    @Test
    @DisplayName("RSA加密解密 - 特殊字符")
    void testEncryptDecryptSpecialCharacters() {
        // Given
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        String specialData = "!@#$%^&*()_+-=[]{}|;':\",./<>?~`\n\r\t";

        // When
        String encrypted = RSAUtil.encrypt(specialData, keyPair.getPublicKey());
        String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivateKey());

        // Then
        assertThat(decrypted).isEqualTo(specialData);
    }

    @Test
    @DisplayName("多次创建密钥对 - 每次应不同")
    void testMultipleKeyPairsAreDifferent() {
        // When
        RSAUtil.StrKeyPair keyPair1 = RSAUtil.createKeyPair();
        RSAUtil.StrKeyPair keyPair2 = RSAUtil.createKeyPair();

        // Then
        assertThat(keyPair1.getPublicKey()).isNotEqualTo(keyPair2.getPublicKey());
        assertThat(keyPair1.getPrivateKey()).isNotEqualTo(keyPair2.getPrivateKey());
    }

    @Test
    @DisplayName("RSA加密解密 - 使用不同密钥对应失败")
    void testEncryptDecryptWithDifferentKeyPairs() {
        // Given
        RSAUtil.StrKeyPair keyPair1 = RSAUtil.createKeyPair();
        RSAUtil.StrKeyPair keyPair2 = RSAUtil.createKeyPair();
        String originalData = "Test data";

        // When
        String encrypted = RSAUtil.encrypt(originalData, keyPair1.getPublicKey());

        // Then - 使用keyPair2的私钥解密应该失败
        assertThatThrownBy(() -> RSAUtil.decrypt(encrypted, keyPair2.getPrivateKey()))
            .isInstanceOf(UtilException.class);
    }

    @Test
    @DisplayName("RSA签名验签 - 使用不同密钥对应失败")
    void testSignAndVerifyWithDifferentKeyPairs() {
        // Given
        RSAUtil.StrKeyPair keyPair1 = RSAUtil.createKeyPair();
        RSAUtil.StrKeyPair keyPair2 = RSAUtil.createKeyPair();
        String data = "Test data";

        // When
        String signature = RSAUtil.sign(data, keyPair1.getPrivateKey());

        // Then - 使用keyPair2的公钥验签应该失败
        boolean verified = RSAUtil.verify(data, keyPair2.getPublicKey(), signature);
        assertThat(verified).isFalse();
    }
}
