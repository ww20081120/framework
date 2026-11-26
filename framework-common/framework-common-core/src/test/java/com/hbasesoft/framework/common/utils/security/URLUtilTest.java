/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * URLUtil 工具类测试<br>
 * 测试覆盖率目标：100% 公共方法<br>
 *
 * @author 王伟<br>
 * @version 2.0<br>
 * @CreateDate 2026年3月31日 <br>
 * @see com.hbasesoft.framework.common.utils.security.URLUtil
 */
@DisplayName("URLUtil 工具类测试")
public class URLUtilTest {

    @Test
    @DisplayName("UTF-8 编码测试 - 普通字符串")
    void testEncode() {
        // Given
        String input = "Hello World";

        // When
        String encoded = URLUtil.encode(input);

        // Then
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("Hello+World");
    }

    @Test
    @DisplayName("UTF-8 编码测试 - 中文字符串")
    void testEncodeChinese() {
        // Given
        String input = "你好世界";

        // When
        String encoded = URLUtil.encode(input);

        // Then
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("%E4%BD%A0%E5%A5%BD%E4%B8%96%E7%95%8C");
    }

    @Test
    @DisplayName("UTF-8 编码测试 - 特殊字符")
    void testEncodeSpecialCharacters() {
        // Given
        String input = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        String encoded = URLUtil.encode(input);

        // Then - Java URLEncoder 不对 * 进行编码，+ 编码为 %2B，空格编码为 +
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("%21%40%23%24%25%5E%26*%28%29_%2B-%3D%5B%5D%7B%7D%7C%3B%27%3A%22%2C.%2F%3C%3E%3F");
    }

    @Test
    @DisplayName("UTF-8 编码测试 - URL 参数")
    void testEncodeUrlParameters() {
        // Given
        String input = "name=张三&age=25";

        // When
        String encoded = URLUtil.encode(input);

        // Then
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("name%3D%E5%BC%A0%E4%B8%89%26age%3D25");
    }

    @Test
    @DisplayName("UTF-8 编码测试 - 空字符串")
    void testEncodeEmptyString() {
        // Given
        String input = "";

        // When
        String encoded = URLUtil.encode(input);

        // Then
        assertThat(encoded)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("UTF-8 编码测试 - null 输入")
    void testEncodeNull() {
        // Given
        String input = null;

        // When
        String encoded = URLUtil.encode(input);

        // Then
        assertThat(encoded)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("指定编码编码测试 - GBK 编码")
    void testEncodeWithCharset() {
        // Given
        String input = "测试";
        Charset charset = Charset.forName("GBK");

        // When
        String encoded = URLUtil.encode(input, charset);

        // Then
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("%B2%E2%CA%D4");
    }

    @Test
    @DisplayName("指定编码编码测试 - ISO-8859-1 编码")
    void testEncodeWithISO88591() {
        // Given
        String input = "Hello";
        Charset charset = StandardCharsets.ISO_8859_1;

        // When
        String encoded = URLUtil.encode(input, charset);

        // Then
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("Hello");
    }

    @Test
    @DisplayName("UTF-8 解码测试 - 普通字符串")
    void testDecode() {
        // Given
        String input = "Hello+World";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("Hello World");
    }

    @Test
    @DisplayName("UTF-8 解码测试 - 中文字符串")
    void testDecodeChinese() {
        // Given
        String input = "%E4%BD%A0%E5%A5%BD%E4%B8%96%E7%95%8C";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("你好世界");
    }

    @Test
    @DisplayName("UTF-8 解码测试 - 特殊字符")
    void testDecodeSpecialCharacters() {
        // Given - * 不需要编码，所以直接写 *；+ 在 URL 中表示空格，+ 的编码是 %2B
        String input = "%21%40%23%24%25%5E%26*%28%29_%2B-%3D%5B%5D%7B%7D%7C%3B%27%3A%22%2C.%2F%3C%3E%3F";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("!@#$%^&*()_+-=[]{}|;':\",./<>?");
    }

    @Test
    @DisplayName("UTF-8 解码测试 - URL 参数")
    void testDecodeUrlParameters() {
        // Given
        String input = "name%3D%E5%BC%A0%E4%B8%89%26age%3D25";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("name=张三&age=25");
    }

    @Test
    @DisplayName("UTF-8 解码测试 - 空字符串")
    void testDecodeEmptyString() {
        // Given
        String input = "";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("UTF-8 解码测试 - null 输入")
    void testDecodeNull() {
        // Given
        String input = null;

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("指定编码解码测试 - GBK 编码")
    void testDecodeWithCharset() {
        // Given
        String input = "%B2%E2%CA%D4";
        Charset charset = Charset.forName("GBK");

        // When
        String decoded = URLUtil.decode(input, charset);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("测试");
    }

    @Test
    @DisplayName("指定编码解码测试 - ISO-8859-1 编码")
    void testDecodeWithISO88591() {
        // Given
        String input = "Hello";
        Charset charset = StandardCharsets.ISO_8859_1;

        // When
        String decoded = URLUtil.decode(input, charset);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("Hello");
    }

    @Test
    @DisplayName("编码解码往返测试 - 普通字符串")
    void testEncodeDecodeRoundTrip() {
        // Given
        String original = "Hello World! 你好世界！";

        // When - 先编码后解码
        String encoded = URLUtil.encode(original);
        String decoded = URLUtil.decode(encoded);

        // Then - 往返转换应得到原文
        assertThat(decoded).isEqualTo(original);
    }

    @Test
    @DisplayName("编码解码往返测试 - URL 完整地址")
    void testEncodeDecodeRoundTripUrl() {
        // Given
        String original = "https://example.com/search?q=测试&page=1";

        // When - 先编码后解码
        String encoded = URLUtil.encode(original);
        String decoded = URLUtil.decode(encoded);

        // Then - 往返转换应得到原文
        assertThat(decoded).isEqualTo(original);
    }

    @Test
    @DisplayName("编码解码往返测试 - 特殊字符组合")
    void testEncodeDecodeRoundTripSpecialChars() {
        // Given
        String original = "a+b=c&d=e%f=g[h]i{j}k|l;m:n\"o,p.f/g";

        // When - 先编码后解码
        String encoded = URLUtil.encode(original);
        String decoded = URLUtil.decode(encoded);

        // Then - 往返转换应得到原文
        assertThat(decoded).isEqualTo(original);
    }

    @Test
    @DisplayName("多次编码解码往返测试")
    void testEncodeDecodeMultipleRoundTrips() {
        // Given
        String original = "测试数据123!@#";

        // When - 多次编码解码往返
        String encoded1 = URLUtil.encode(original);
        String decoded1 = URLUtil.decode(encoded1);

        String encoded2 = URLUtil.encode(decoded1);
        String decoded2 = URLUtil.decode(encoded2);

        // Then - 每次往返都应得到原文
        assertThat(decoded1).isEqualTo(original);
        assertThat(decoded2).isEqualTo(original);
    }

    @Test
    @DisplayName("空格编码测试")
    void testEncodeSpace() {
        // Given
        String input = "Hello World Test";

        // When
        String encoded = URLUtil.encode(input);

        // Then - 空格应被编码为 +
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("Hello+World+Test");
    }

    @Test
    @DisplayName("空格解码测试 - 使用 + 号")
    void testDecodeSpaceWithPlus() {
        // Given
        String input = "Hello+World+Test";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("Hello World Test");
    }

    @Test
    @DisplayName("空格解码测试 - 使用 %20")
    void testDecodeSpaceWithPercent20() {
        // Given
        String input = "Hello%20World%20Test";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("Hello World Test");
    }

    @Test
    @DisplayName("编码相同输入应产生相同输出")
    void testEncodeDeterministic() {
        // Given
        String input = "测试字符串";

        // When - 多次编码
        String encoded1 = URLUtil.encode(input);
        String encoded2 = URLUtil.encode(input);

        // Then - 编码是确定性操作
        assertThat(encoded1).isEqualTo(encoded2);
    }

    @Test
    @DisplayName("解码相同输入应产生相同输出")
    void testDecodeDeterministic() {
        // Given
        String input = "%E6%B5%8B%E8%AF%95%E5%AD%97%E7%AC%A6%E4%B8%B2";

        // When - 多次解码
        String decoded1 = URLUtil.decode(input);
        String decoded2 = URLUtil.decode(input);

        // Then - 解码是确定性操作
        assertThat(decoded1).isEqualTo(decoded2);
    }

    @Test
    @DisplayName("编码表情符号测试")
    void testEncodeEmoji() {
        // Given
        String input = "😀😃😄😁😆";

        // When
        String encoded = URLUtil.encode(input);

        // Then
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("%F0%9F%98%80%F0%9F%98%83%F0%9F%98%84%F0%9F%98%81%F0%9F%98%86");
    }

    @Test
    @DisplayName("解码表情符号测试")
    void testDecodeEmoji() {
        // Given
        String input = "%F0%9F%98%80%F0%9F%98%83%F0%9F%98%84%F0%9F%98%81%F0%9F%98%86";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("😀😃😄😁😆");
    }

    @Test
    @DisplayName("编码解码往返测试 - 表情符号")
    void testEncodeDecodeRoundTripEmoji() {
        // Given
        String original = "你好 😀 世界";

        // When
        String encoded = URLUtil.encode(original);
        String decoded = URLUtil.decode(encoded);

        // Then
        assertThat(decoded).isEqualTo(original);
    }

    @Test
    @DisplayName("指定编码编码解码往返测试 - GBK")
    void testEncodeDecodeRoundTripWithGBK() {
        // Given
        String original = "测试数据";
        Charset charset = Charset.forName("GBK");

        // When
        String encoded = URLUtil.encode(original, charset);
        String decoded = URLUtil.decode(encoded, charset);

        // Then
        assertThat(decoded).isEqualTo(original);
    }

    @Test
    @DisplayName("编码包含百分号的字符串")
    void testEncodePercentSign() {
        // Given
        String input = "100%";

        // When
        String encoded = URLUtil.encode(input);

        // Then - % 应被编码为 %25
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("100%25");
    }

    @Test
    @DisplayName("解码包含百分号的字符串")
    void testDecodePercentSign() {
        // Given
        String input = "100%25";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("100%");
    }

    @Test
    @DisplayName("编码包含加号的字符串")
    void testEncodePlusSign() {
        // Given
        String input = "1+1=2";

        // When
        String encoded = URLUtil.encode(input);

        // Then - + 应被编码为 %2B
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("1%2B1%3D2");
    }

    @Test
    @DisplayName("解码包含加号的字符串")
    void testDecodePlusSign() {
        // Given
        String input = "1%2B1%3D2";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("1+1=2");
    }

    @Test
    @DisplayName("编码包含等号的字符串")
    void testEncodeEqualsSign() {
        // Given
        String input = "key=value";

        // When
        String encoded = URLUtil.encode(input);

        // Then - = 应被编码为 %3D
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("key%3Dvalue");
    }

    @Test
    @DisplayName("解码包含等号的字符串")
    void testDecodeEqualsSign() {
        // Given
        String input = "key%3Dvalue";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("key=value");
    }

    @Test
    @DisplayName("编码包含和号的字符串")
    void testEncodeAmpersand() {
        // Given
        String input = "a&b&c";

        // When
        String encoded = URLUtil.encode(input);

        // Then - & 应被编码为 %26
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("a%26b%26c");
    }

    @Test
    @DisplayName("解码包含和号的字符串")
    void testDecodeAmpersand() {
        // Given
        String input = "a%26b%26c";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("a&b&c");
    }

    @Test
    @DisplayName("编码包含井号的字符串")
    void testEncodeHashSign() {
        // Given
        String input = "#标题";

        // When
        String encoded = URLUtil.encode(input);

        // Then - # 应被编码为 %23
        assertThat(encoded)
            .isNotNull()
            .isEqualTo("%23%E6%A0%87%E9%A2%98");
    }

    @Test
    @DisplayName("解码包含井号的字符串")
    void testDecodeHashSign() {
        // Given
        String input = "%23%E6%A0%87%E9%A2%98";

        // When
        String decoded = URLUtil.decode(input);

        // Then
        assertThat(decoded)
            .isNotNull()
            .isEqualTo("#标题");
    }
}
