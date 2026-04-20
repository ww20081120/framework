/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.UtilException;

/**
 * DataUtil 工具类测试<br>
 * 测试覆盖率目标：100% 公共方法<br>
 *
 * @author 王伟<br>
 * @version 2.0<br>
 * @CreateDate 2018年9月13日 <br>
 * @see com.hbasesoft.framework.common.utils.security.DataUtil
 */
@DisplayName("DataUtil 工具类测试")
public class DataUtilTest {

    @Test
    @DisplayName("MD5 16位加密测试")
    void testMd5For16() {
        // Given
        String input = "123456";

        // When
        String result = DataUtil.md5For16(input);

        // Then
        assertThat(result)
            .isNotNull()
            .hasSize(16)
            .isEqualTo("49BA59ABBE56E057");
    }

    @Test
    @DisplayName("MD5 32位加密测试")
    void testMd5() {
        // Given
        String input = "123456";

        // When
        String result = DataUtil.md5(input);

        // Then
        assertThat(result)
            .isNotNull()
            .hasSize(32)
            .isEqualTo("E10ADC3949BA59ABBE56E057F20F883E");
    }

    @Test
    @DisplayName("SHA256 加密测试")
    void testSha256() {
        // Given
        String input = "123456";

        // When
        String result = DataUtil.sha256(input);

        // Then
        assertThat(result)
            .isNotNull()
            .hasSize(64)
            .isEqualTo("8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92");
    }

    @Test
    @DisplayName("Base64 解码测试")
    void testBase64Decode() {
        // Given
        String encoded = "MTIzNDU2";

        // When
        byte[] decoded = DataUtil.base64Decode(encoded);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("123456".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Base64 编码测试")
    void testBase64Encode() {
        // Given
        byte[] input = "123456".getBytes(StandardCharsets.UTF_8);

        // When
        String encoded = DataUtil.base64Encode(input);

        // Then
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("MTIzNDU2");
    }

    @Test
    @DisplayName("Base64 URL安全编码测试")
    void testBase64UrlEncode() {
        // Given
        byte[] input = "test@example.com".getBytes(StandardCharsets.UTF_8);

        // When
        String encoded = DataUtil.base64UrlEncode(input);

        // Then
        assertThat(encoded)
            .isNotNull()
            .doesNotContain("+")
            .doesNotContain("/")
            .doesNotContain("=");
    }

    @Test
    @DisplayName("密码加密测试 - 验证盐值机制随机性")
    void testEncryptPassword() {
        // Given
        String rawPassword = "123456";

        // When - 多次加密相同密码
        String encrypted1 = DataUtil.encryptPassowrd(rawPassword);
        String encrypted2 = DataUtil.encryptPassowrd(rawPassword);

        // Then - 验证加密结果不同（盐值随机性）
        assertThat(encrypted1)
            .isNotNull()
            .isNotEqualTo(encrypted2);
    }

    @Test
    @DisplayName("密码匹配测试 - 相同密码应匹配成功")
    void testMatchPasswordSuccess() {
        // Given
        String rawPassword = "123456";
        String encryptedPassword = DataUtil.encryptPassowrd(rawPassword);

        // When & Then
        assertThat(DataUtil.matchPassword(rawPassword, encryptedPassword)).isTrue();
    }

    @Test
    @DisplayName("密码匹配测试 - 不同密码应匹配失败")
    void testMatchPasswordFailure() {
        // Given
        String rawPassword = "123456";
        String wrongPassword = "654321";
        String encryptedPassword = DataUtil.encryptPassowrd(rawPassword);

        // When & Then
        assertThat(DataUtil.matchPassword(wrongPassword, encryptedPassword)).isFalse();
    }

    @Test
    @DisplayName("可逆加密测试")
    void testEncrypt() {
        // Given
        String password = "123456";

        // When
        String encrypted = DataUtil.encrypt(password);

        // Then
        assertThat(encrypted)
            .isNotNull()
            .isNotEmpty()
            .isNotEqualTo(password);
    }

    @Test
    @DisplayName("可逆解密测试 - 加密后解密应得到原文")
    void testDecrypt() {
        // Given
        String original = "123456";
        String encrypted = DataUtil.encrypt(original);

        // When
        String decrypted = DataUtil.decrypt(encrypted);

        // Then
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("可逆加密解密测试 - 多次加密结果不同")
    void testEncryptDecryptRandomness() {
        // Given
        String password = "123456";

        // When - 多次加密相同内容
        String encrypted1 = DataUtil.encrypt(password);
        String encrypted2 = DataUtil.encrypt(password);

        // Then - 验证加密结果具有随机性
        assertThat(encrypted1).isNotEqualTo(encrypted2);

        // But - 解密后都能得到原文
        assertThat(DataUtil.decrypt(encrypted1)).isEqualTo(password);
        assertThat(DataUtil.decrypt(encrypted2)).isEqualTo(password);
    }

    @Test
    @DisplayName("字节数组转16进制字符串测试")
    void testByte2HexStr() {
        // Given
        byte[] input = "abcdefg".getBytes(StandardCharsets.UTF_8);

        // When
        String hex = DataUtil.byte2HexStr(input);

        // Then
        assertThat(hex)
            .isNotNull()
            .isEqualTo("61626364656667");
    }

    @Test
    @DisplayName("字节数组转16进制字符串测试 - 空数组")
    void testByte2HexStrEmpty() {
        // Given
        byte[] input = new byte[0];

        // When
        String hex = DataUtil.byte2HexStr(input);

        // Then
        assertThat(hex)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("16进制字符串转字节数组测试")
    void testHexStr2Byte() {
        // Given
        String hex = "61626364656667";

        // When
        byte[] bytes = DataUtil.hexStr2Byte(hex);

        // Then
        assertThat(bytes)
            .isNotNull()
            .isEqualTo("abcdefg".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("16进制字符串转字节数组测试 - 空字符串")
    void testHexStr2ByteEmpty() {
        // Given
        String hex = "";

        // When
        byte[] bytes = DataUtil.hexStr2Byte(hex);

        // Then
        assertThat(bytes)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("字节数组与16进制字符串相互转换测试 - 往返转换一致性")
    void testByteHexConversion() {
        // Given
        byte[] original = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        // When - 字节数组 -> 16进制字符串 -> 字节数组
        String hex = DataUtil.byte2HexStr(original);
        byte[] converted = DataUtil.hexStr2Byte(hex);

        // Then
        assertThat(converted).isEqualTo(original);
    }

    @Test
    @DisplayName("Base64 编码解码往返测试")
    void testBase64RoundTrip() {
        // Given
        String original = "Hello, 世界!";
        byte[] originalBytes = original.getBytes(StandardCharsets.UTF_8);

        // When - 编码后解码
        String encoded = DataUtil.base64Encode(originalBytes);
        byte[] decoded = DataUtil.base64Decode(encoded);

        // Then
        assertThat(decoded).isEqualTo(originalBytes);
    }

    @Test
    @DisplayName("MD5 加密相同输入应产生相同输出")
    void testMd5Deterministic() {
        // Given
        String input = "test123";

        // When - 多次加密
        String hash1 = DataUtil.md5(input);
        String hash2 = DataUtil.md5(input);

        // Then - MD5 是确定性算法
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("SHA256 加密相同输入应产生相同输出")
    void testSha256Deterministic() {
        // Given
        String input = "test123";

        // When - 多次加密
        String hash1 = DataUtil.sha256(input);
        String hash2 = DataUtil.sha256(input);

        // Then - SHA256 是确定性算法
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("密码加密不同密码应产生不同结果")
    void testEncryptDifferentPasswords() {
        // Given
        String password1 = "password123";
        String password2 = "password456";

        // When
        String encrypted1 = DataUtil.encryptPassowrd(password1);
        String encrypted2 = DataUtil.encryptPassowrd(password2);

        // Then
        assertThat(encrypted1).isNotEqualTo(encrypted2);
    }

    @Test
    @DisplayName("特殊字符MD5加密测试")
    void testMd5WithSpecialCharacters() {
        // Given
        String input = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        String result = DataUtil.md5(input);

        // Then
        assertThat(result)
            .isNotNull()
            .hasSize(32);
    }

    @Test
    @DisplayName("中文字符串SHA256加密测试")
    void testSha256WithChineseCharacters() {
        // Given
        String input = "你好，世界！";

        // When
        String result = DataUtil.sha256(input);

        // Then
        assertThat(result)
            .isNotNull()
            .hasSize(64);
    }

    @Test
    @DisplayName("空字符串MD5加密测试")
    void testMd5EmptyString() {
        // Given
        String input = "";

        // When
        String result = DataUtil.md5(input);

        // Then
        assertThat(result)
            .isNotNull()
            .hasSize(32)
            .isEqualTo("D41D8CD98F00B204E9800998ECF8427E");
    }

    @Test
    @DisplayName("可逆加密解密中文测试")
    void testEncryptDecryptChinese() {
        // Given
        String original = "你好，世界！";

        // When
        String encrypted = DataUtil.encrypt(original);
        String decrypted = DataUtil.decrypt(encrypted);

        // Then
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("密码加密后应包含前缀标识")
    void testEncryptPasswordPrefix() {
        // Given
        String password = "test123";

        // When
        String encrypted = DataUtil.encryptPassowrd(password);

        // Then - Spring Security 的 DelegatingPasswordEncoder 会添加算法前缀
        assertThat(encrypted)
            .isNotNull()
            .contains("{");
    }

    @Test
    @DisplayName("Base64 URL安全编码不应包含特殊字符")
    void testBase64UrlEncodeNoSpecialChars() {
        // Given - 包含会产生 + 和 / 的字节数组
        byte[] input = new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0xFD};

        // When
        String encoded = DataUtil.base64UrlEncode(input);

        // Then
        assertThat(encoded)
            .doesNotContain("+")
            .doesNotContain("/")
            .doesNotContain("=");
    }
}
