/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.UtilException;

/**
 * DESUtil 测试类
 * 使用 JUnit 5 + AssertJ 进行测试
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.security.DESUtil
 */
@DisplayName("DESUtil 工具类测试")
public class DESUtilTest {

    @Test
    @DisplayName("encryption - 使用默认密钥加密普通字符串")
    void testEncryption_DefaultKey() {
        // 准备测试数据
        String plainText = "Hello World";

        // 执行加密
        String encrypted = DESUtil.encryption(plainText);

        // 验证结果
        assertThat(encrypted)
            .as("加密后的字符串不应为空")
            .isNotEmpty();
        assertThat(encrypted)
            .as("加密后的字符串不应与原文相同")
            .isNotEqualTo(plainText);
    }

    @Test
    @DisplayName("encryption - 使用默认密钥加密中文字符串")
    void testEncryption_DefaultKey_Chinese() {
        // 准备测试数据
        String plainText = "你好世界";

        // 执行加密
        String encrypted = DESUtil.encryption(plainText);

        // 验证结果
        assertThat(encrypted)
            .as("加密中文字符串后的结果不应为空")
            .isNotEmpty();
        assertThat(encrypted)
            .as("加密后的字符串不应与原文相同")
            .isNotEqualTo(plainText);
    }

    @Test
    @DisplayName("encryption - 使用自定义密钥加密字符串")
    void testEncryption_CustomKey() {
        // 准备测试数据
        String plainText = "Test Data";
        String customKey = "mysecret";

        // 执行加密
        String encrypted = DESUtil.encryption(plainText, customKey);

        // 验证结果
        assertThat(encrypted)
            .as("使用自定义密钥加密后的结果不应为空")
            .isNotEmpty();
        assertThat(encrypted)
            .as("加密后的字符串不应与原文相同")
            .isNotEqualTo(plainText);
    }

    @Test
    @DisplayName("encryption - 相同明文使用不同密钥加密结果不同")
    void testEncryption_DifferentKeys() {
        // 准备测试数据
        String plainText = "Same Plain Text";
        String key1 = "12345678"; // DES 密钥必须是 8 字节
        String key2 = "87654321";

        // 使用不同密钥加密
        String encrypted1 = DESUtil.encryption(plainText, key1);
        String encrypted2 = DESUtil.encryption(plainText, key2);

        // 验证结果
        assertThat(encrypted1)
            .as("使用不同密钥加密应该产生不同的密文")
            .isNotEqualTo(encrypted2);
    }

    @Test
    @DisplayName("encryption - 加密特殊字符")
    void testEncryption_SpecialCharacters() {
        // 准备测试数据 - 包含特殊字符
        String plainText = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        // 执行加密
        String encrypted = DESUtil.encryption(plainText);

        // 验证结果
        assertThat(encrypted)
            .as("加密特殊字符后的结果不应为空")
            .isNotEmpty();
        assertThat(encrypted)
            .as("加密后的字符串不应与原文相同")
            .isNotEqualTo(plainText);
    }

    @Test
    @DisplayName("encryption - 加密空字符串")
    void testEncryption_EmptyString() {
        // 准备测试数据
        String plainText = "";

        // 执行加密
        String encrypted = DESUtil.encryption(plainText);

        // 验证结果
        assertThat(encrypted)
            .as("加密空字符串后的结果不应为空")
            .isNotEmpty();
    }

    @Test
    @DisplayName("encryption - 加密数字字符串")
    void testEncryption_NumericString() {
        // 准备测试数据
        String plainText = "1234567890";

        // 执行加密
        String encrypted = DESUtil.encryption(plainText);

        // 验证结果
        assertThat(encrypted)
            .as("加密数字字符串后的结果不应为空")
            .isNotEmpty();
        assertThat(encrypted)
            .as("加密后的字符串不应与原文相同")
            .isNotEqualTo(plainText);
    }

    @Test
    @DisplayName("decryption - 使用默认密钥解密（加密解密对称性验证）")
    void testDecryption_DefaultKey() {
        // 准备测试数据
        String plainText = "Hello World";

        // 先加密
        String encrypted = DESUtil.encryption(plainText);

        // 再解密
        String decrypted = DESUtil.decryption(encrypted);

        // 验证对称性
        assertThat(decrypted)
            .as("解密后的字符串应与原文相同")
            .isEqualTo(plainText);
    }

    @Test
    @DisplayName("decryption - 使用默认密钥解密中文字符串")
    void testDecryption_DefaultKey_Chinese() {
        // 准备测试数据
        String plainText = "你好世界";

        // 先加密
        String encrypted = DESUtil.encryption(plainText);

        // 再解密
        String decrypted = DESUtil.decryption(encrypted);

        // 验证对称性
        assertThat(decrypted)
            .as("解密中文字符串后应与原文相同")
            .isEqualTo(plainText);
    }

    @Test
    @DisplayName("decryption - 使用自定义密钥解密（加密解密对称性验证）")
    void testDecryption_CustomKey() {
        // 准备测试数据
        String plainText = "Test Data";
        String customKey = "mysecret";

        // 先加密
        String encrypted = DESUtil.encryption(plainText, customKey);

        // 再解密
        String decrypted = DESUtil.decryption(encrypted, customKey);

        // 验证对称性
        assertThat(decrypted)
            .as("使用自定义密钥解密后的字符串应与原文相同")
            .isEqualTo(plainText);
    }

    @Test
    @DisplayName("decryption - 使用错误密钥解密应该抛出异常")
    void testDecryption_WrongKey() {
        // 准备测试数据
        String plainText = "Test Data";
        String key1 = "12345678"; // DES 密钥必须是 8 字节
        String key2 = "87654321";

        // 使用 key1 加密
        String encrypted = DESUtil.encryption(plainText, key1);

        // 使用 key2 解密应该抛出异常
        assertThatThrownBy(() -> DESUtil.decryption(encrypted, key2))
            .as("使用错误密钥解密应该抛出异常")
            .isInstanceOf(UtilException.class)
            .hasMessageContaining("DECRYPTION_ERROR");
    }

    @Test
    @DisplayName("decryption - 解密特殊字符")
    void testDecryption_SpecialCharacters() {
        // 准备测试数据
        String plainText = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        // 先加密
        String encrypted = DESUtil.encryption(plainText);

        // 再解密
        String decrypted = DESUtil.decryption(encrypted);

        // 验证对称性
        assertThat(decrypted)
            .as("解密特殊字符后应与原文相同")
            .isEqualTo(plainText);
    }

    @Test
    @DisplayName("decryption - 解密空字符串")
    void testDecryption_EmptyString() {
        // 准备测试数据
        String plainText = "";

        // 先加密
        String encrypted = DESUtil.encryption(plainText);

        // 再解密
        String decrypted = DESUtil.decryption(encrypted);

        // 验证对称性
        assertThat(decrypted)
            .as("解密空字符串后应与原文相同")
            .isEqualTo(plainText);
    }

    @Test
    @DisplayName("decryption - 解密数字字符串")
    void testDecryption_NumericString() {
        // 准备测试数据
        String plainText = "1234567890";

        // 先加密
        String encrypted = DESUtil.encryption(plainText);

        // 再解密
        String decrypted = DESUtil.decryption(encrypted);

        // 验证对称性
        assertThat(decrypted)
            .as("解密数字字符串后应与原文相同")
            .isEqualTo(plainText);
    }

    @Test
    @DisplayName("decryption - 解密无效数据应该抛出异常")
    void testDecryption_InvalidData() {
        // 准备无效的加密数据
        String invalidEncrypted = "InvalidEncryptedData123";

        // 解密应该抛出异常
        assertThatThrownBy(() -> DESUtil.decryption(invalidEncrypted))
            .as("解密无效数据应该抛出异常")
            .isInstanceOf(UtilException.class)
            .hasMessageContaining("DECRYPTION_ERROR");
    }

    @Test
    @DisplayName("加密解密对称性 - 长文本")
    void testSymmetricEncryption_LongText() {
        // 准备测试数据 - 较长的文本
        String plainText = "This is a longer text that contains multiple sentences. "
            + "It is used to test if the DES encryption and decryption "
            + "can handle longer strings correctly.";

        // 先加密
        String encrypted = DESUtil.encryption(plainText);

        // 再解密
        String decrypted = DESUtil.decryption(encrypted);

        // 验证对称性
        assertThat(decrypted)
            .as("解密长文本后应与原文相同")
            .isEqualTo(plainText);
    }

    @Test
    @DisplayName("加密解密对称性 - 包含换行符的文本")
    void testSymmetricEncryption_WithNewlines() {
        // 准备测试数据 - 包含换行符
        String plainText = "Line 1\nLine 2\r\nLine 3";

        // 先加密
        String encrypted = DESUtil.encryption(plainText);

        // 再解密
        String decrypted = DESUtil.decryption(encrypted);

        // 验证对称性
        assertThat(decrypted)
            .as("解密包含换行符的文本后应与原文相同")
            .isEqualTo(plainText);
    }

    @Test
    @DisplayName("加密解密对称性 - 多次加密解密结果一致")
    void testSymmetricEncryption_MultipleRounds() {
        // 准备测试数据
        String plainText = "Multiple Rounds Test";

        // 第一次加密解密
        String encrypted1 = DESUtil.encryption(plainText);
        String decrypted1 = DESUtil.decryption(encrypted1);

        // 第二次加密解密
        String encrypted2 = DESUtil.encryption(plainText);
        String decrypted2 = DESUtil.decryption(encrypted2);

        // 验证解密结果一致
        assertThat(decrypted1)
            .as("多次解密结果应该一致")
            .isEqualTo(decrypted2)
            .isEqualTo(plainText);

        // 验证加密结果也一致
        assertThat(encrypted1)
            .as("相同输入多次加密结果应该一致")
            .isEqualTo(encrypted2);
    }

    @Test
    @DisplayName("加密解密对称性 - 使用自定义密钥的完整流程")
    void testSymmetricEncryption_CompleteFlowWithCustomKey() {
        // 准备测试数据
        String customKey = "customKey123";
        String plainText = "Complete Flow Test";

        // 加密
        String encrypted = DESUtil.encryption(plainText, customKey);

        // 验证加密结果
        assertThat(encrypted)
            .as("加密结果不应为空")
            .isNotEmpty();
        assertThat(encrypted)
            .as("加密结果不应与原文相同")
            .isNotEqualTo(plainText);

        // 解密
        String decrypted = DESUtil.decryption(encrypted, customKey);

        // 验证解密结果
        assertThat(decrypted)
            .as("解密结果应与原文相同")
            .isEqualTo(plainText);
    }

    @Test
    @DisplayName("私有构造器 - 防止实例化")
    void testPrivateConstructor() {
        // 尝试通过反射创建实例应该抛出异常
        assertThatThrownBy(() -> {
            var constructor = DESUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        })
            .as("私有构造器应该抛出 UnsupportedOperationException")
            .hasCauseExactlyInstanceOf(UnsupportedOperationException.class)
            .hasMessageContaining("Utility class cannot be instantiated");
    }
}
