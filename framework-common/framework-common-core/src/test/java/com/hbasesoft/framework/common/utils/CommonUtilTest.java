package com.hbasesoft.framework.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("CommonUtil 单元测试")
class CommonUtilTest {

    @Test
    @DisplayName("应正确格式化消息 - 多参数场景")
    void should_formatMessage_when_multipleParams() {
        String result = CommonUtil.messageFormat("Hello {0}, age {1}", "Alice", 18);
        assertThat(result).isEqualTo("Hello Alice, age 18");
    }

    @Test
    @DisplayName("应返回原消息 - 无参数场景")
    void should_returnOriginalMessage_when_noParams() {
        String result = CommonUtil.messageFormat("No params");
        assertThat(result).isEqualTo("No params");
    }

    @Test
    @DisplayName("应生成唯一事务ID")
    void should_generateUniqueTransactionId() {
        String id1 = CommonUtil.getTransactionID();
        String id2 = CommonUtil.getTransactionID();
        assertThat(id1).isNotEmpty().isNotEqualTo(id2);
        assertThat(id1).hasSize(32); // UUID去除横线后32位
    }

    @Test
    @DisplayName("应生成随机码")
    void should_generateRandomCode() {
        String code = CommonUtil.getRandomCode();
        assertThat(code).isNotEmpty();
    }

    @Test
    @DisplayName("应生成指定位数随机数")
    void should_generateRandomNumber_withLength() {
        String num = CommonUtil.getRandomNumber(5);
        assertThat(num).hasSize(5);
        assertThat(num).containsOnlyDigits();
    }

    @Test
    @DisplayName("应生成指定长度随机字符")
    void should_generateRandomChar_withLength() {
        String str = CommonUtil.getRandomChar(10);
        assertThat(str).hasSize(10);
    }

    @Test
    @DisplayName("应移除所有符号")
    void should_removeAllSymbols() {
        String result = CommonUtil.removeAllSymbol("hello!@#world");
        assertThat(result).isEqualTo("helloworld");
    }

    @Test
    @DisplayName("应移除所有空白字符")
    void should_removeAllBlanks() {
        String result = CommonUtil.replaceAllBlank("  a  b\t\nc  ");
        assertThat(result).isEqualTo("abc");
    }
}
