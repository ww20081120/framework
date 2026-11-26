/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JWTUtil 工具类测试<br>
 * 测试覆盖率目标：100% 公共方法<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @CreateDate 2023年3月17日 <br>
 * @see com.hbasesoft.framework.common.utils.security.JWTUtil
 */
@DisplayName("JWTUtil 工具类测试")
public class JWTUtilTest {

    /** 测试用的公钥 */
    private static String testPublicKey;

    /** 测试用的私钥 */
    private static String testPrivateKey;

    @BeforeAll
    static void setUp() {
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();
        testPublicKey = keyPair.getPublicKey();
        testPrivateKey = keyPair.getPrivateKey();
    }

    @Test
    @DisplayName("创建 JWT Token - 成功场景")
    void testCreateToken_Success() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000; // 1小时后过期
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "12345");
        payload.put("username", "testuser");

        // When
        String token = JWTUtil.createToken(expireTime, payload, testPrivateKey);

        // Then
        assertThat(token)
            .isNotNull()
            .isNotEmpty()
            .contains(".");
    }

    @Test
    @DisplayName("创建 JWT Token - 空负载")
    void testCreateToken_EmptyPayload() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000;
        Map<String, Object> payload = new HashMap<>();

        // When
        String token = JWTUtil.createToken(expireTime, payload, testPrivateKey);

        // Then
        assertThat(token)
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    @DisplayName("创建 JWT Token - 包含多种数据类型")
    void testCreateToken_WithVariousDataTypes() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("string", "value");
        payload.put("number", 12345);
        payload.put("boolean", true);
        payload.put("nested", Map.of("key", "value"));

        // When
        String token = JWTUtil.createToken(expireTime, payload, testPrivateKey);

        // Then
        assertThat(token)
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    @DisplayName("验证 Token - 有效 Token")
    void testVerify_ValidToken() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "12345");
        String token = JWTUtil.createToken(expireTime, payload, testPrivateKey);

        // When
        boolean result = JWTUtil.verify(token, testPublicKey);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("验证 Token - 无效 Token")
    void testVerify_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.string";

        // When
        boolean result = JWTUtil.verify(invalidToken, testPublicKey);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("验证 Token - 空字符串")
    void testVerify_NullToken() {
        // When
        boolean result = JWTUtil.verify(null, testPublicKey);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("验证 Token - 格式错误的 Token")
    void testVerify_MalformedToken() {
        // Given
        String malformedToken = "not-a-valid-jwt";

        // When
        boolean result = JWTUtil.verify(malformedToken, testPublicKey);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("验证 Token - 被篡改的 Token")
    void testVerify_TamperedToken() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "12345");
        String token = JWTUtil.createToken(expireTime, payload, testPrivateKey);
        // 篡改签名部分，而不是在末尾添加
        String tamperedToken = token.substring(0, token.length() - 1) + "X";

        // When
        boolean result = JWTUtil.verify(tamperedToken, testPublicKey);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("解析 Token - 不校验 - 有效 Token")
    void testParseToken_NoValidation_ValidToken() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000;
        Map<String, Object> originalPayload = new HashMap<>();
        originalPayload.put("userId", "12345");
        originalPayload.put("username", "testuser");
        String token = JWTUtil.createToken(expireTime, originalPayload, testPrivateKey);

        // When
        Map<String, Object> parsedPayload = JWTUtil.parseToken(token);

        // Then
        assertThat(parsedPayload)
            .isNotNull()
            .isNotEmpty()
            .containsKey("userId")
            .containsKey("username")
            .containsKey("exp");
        assertThat(parsedPayload.get("userId")).isEqualTo("12345");
        assertThat(parsedPayload.get("username")).isEqualTo("testuser");
    }

    @Test
    @DisplayName("解析 Token - 不校验 - 空字符串")
    void testParseToken_NoValidation_NullToken() {
        // When
        Map<String, Object> result = JWTUtil.parseToken(null);

        // Then
        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("解析 Token - 不校验 - 格式错误的 Token")
    void testParseToken_NoValidation_InvalidFormat() {
        // Given
        String invalidToken = "invalid.token";

        // When
        Map<String, Object> result = JWTUtil.parseToken(invalidToken);

        // Then
        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("解析 Token - 不校验 - 空的 Payload 部分")
    void testParseToken_NoValidation_EmptyPayload() {
        // Given
        String token = "header..signature";

        // When
        Map<String, Object> result = JWTUtil.parseToken(token);

        // Then
        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("解析 Token - 带校验 - 有效且未过期的 Token")
    void testParseToken_WithValidation_ValidAndNotExpired() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000; // 1小时后过期
        Map<String, Object> originalPayload = new HashMap<>();
        originalPayload.put("userId", "12345");
        originalPayload.put("username", "testuser");
        String token = JWTUtil.createToken(expireTime, originalPayload, testPrivateKey);

        // When
        Map<String, Object> parsedPayload = JWTUtil.parseToken(token, testPublicKey);

        // Then
        assertThat(parsedPayload)
            .isNotNull()
            .isNotEmpty()
            .containsKey("userId")
            .containsKey("username")
            .containsKey("exp");
        assertThat(parsedPayload.get("userId")).isEqualTo("12345");
        assertThat(parsedPayload.get("username")).isEqualTo("testuser");
    }

    @Test
    @DisplayName("解析 Token - 带校验 - 已过期的 Token")
    void testParseToken_WithValidation_ExpiredToken() {
        // Given
        long expireTime = System.currentTimeMillis() - 1000; // 已经过期
        Map<String, Object> originalPayload = new HashMap<>();
        originalPayload.put("userId", "12345");
        String token = JWTUtil.createToken(expireTime, originalPayload, testPrivateKey);

        // When
        Map<String, Object> parsedPayload = JWTUtil.parseToken(token, testPublicKey);

        // Then
        assertThat(parsedPayload)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("解析 Token - 带校验 - 空字符串")
    void testParseToken_WithValidation_NullToken() {
        // When
        Map<String, Object> result = JWTUtil.parseToken(null, testPublicKey);

        // Then
        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("解析 Token - 带校验 - 签名无效的 Token")
    void testParseToken_WithValidation_InvalidSignature() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000;
        Map<String, Object> originalPayload = new HashMap<>();
        originalPayload.put("userId", "12345");
        String token = JWTUtil.createToken(expireTime, originalPayload, testPrivateKey);
        // 篡改签名部分
        String tamperedToken = token.substring(0, token.lastIndexOf('.')) + ".tamperedSignature";

        // When
        Map<String, Object> result = JWTUtil.parseToken(tamperedToken, testPublicKey);

        // Then
        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("解析 Token - 带校验 - 格式错误的 Token")
    void testParseToken_WithValidation_InvalidFormat() {
        // Given
        String invalidToken = "invalid.token.format";

        // When
        Map<String, Object> result = JWTUtil.parseToken(invalidToken, testPublicKey);

        // Then
        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("解析 Token - 带校验 - 使用错误的公钥")
    void testParseToken_WithValidation_WrongPublicKey() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000;
        Map<String, Object> originalPayload = new HashMap<>();
        originalPayload.put("userId", "12345");
        String token = JWTUtil.createToken(expireTime, originalPayload, testPrivateKey);

        // 生成一个新的密钥对作为错误的公钥
        RSAUtil.StrKeyPair keyPair = RSAUtil.createKeyPair();

        // When
        Map<String, Object> result = JWTUtil.parseToken(token, keyPair.getPublicKey());

        // Then
        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("完整的 Token 生命周期测试")
    void testCompleteTokenLifecycle() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000;
        Map<String, Object> originalPayload = new HashMap<>();
        originalPayload.put("userId", "12345");
        originalPayload.put("username", "testuser");
        originalPayload.put("role", "admin");

        // When - 创建 Token
        String token = JWTUtil.createToken(expireTime, originalPayload, testPrivateKey);

        // Then - 验证 Token
        assertThat(JWTUtil.verify(token, testPublicKey)).isTrue();

        // When - 解析 Token（不校验）
        Map<String, Object> parsedWithoutValidation = JWTUtil.parseToken(token);
        assertThat(parsedWithoutValidation)
            .isNotNull()
            .isNotEmpty()
            .containsKey("userId")
            .containsKey("username")
            .containsKey("role");

        // When - 解析 Token（带校验）
        Map<String, Object> parsedWithValidation = JWTUtil.parseToken(token, testPublicKey);
        assertThat(parsedWithValidation)
            .isNotNull()
            .isNotEmpty()
            .containsKey("userId")
            .containsKey("username")
            .containsKey("role");
    }

    @Test
    @DisplayName("创建和解析包含特殊字符的 Payload")
    void testCreateAndParseToken_SpecialCharacters() {
        // Given
        long expireTime = System.currentTimeMillis() + 3600000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "user@123");
        payload.put("email", "test@example.com");
        payload.put("message", "Hello 世界! 🌍");

        // When
        String token = JWTUtil.createToken(expireTime, payload, testPrivateKey);
        Map<String, Object> parsed = JWTUtil.parseToken(token, testPublicKey);

        // Then
        assertThat(parsed)
            .isNotNull()
            .isNotEmpty();
        assertThat(parsed.get("userId")).isEqualTo("user@123");
        assertThat(parsed.get("email")).isEqualTo("test@example.com");
        assertThat(parsed.get("message")).isEqualTo("Hello 世界! 🌍");
    }
}
