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

    @Test
    @DisplayName("应返回对象toString结果 - 非null对象")
    void should_returnToStringResult_when_objectIsNotNull() {
        Object obj = new Object();
        String result = CommonUtil.getString(obj);
        assertThat(result).isEqualTo(obj.toString());
    }

    @Test
    @DisplayName("应返回null - null对象")
    void should_returnNull_when_objectIsNull() {
        String result = CommonUtil.getString(null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("应返回空字符串 - null输入")
    void should_returnEmptyString_when_inputIsNull() {
        String result = CommonUtil.notNullStr(null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("应返回原字符串 - 非null输入")
    void should_returnOriginalString_when_inputIsNotNull() {
        String original = "test";
        String result = CommonUtil.notNullStr(original);
        assertThat(result).isEqualTo(original);
    }

    @Test
    @DisplayName("应分割字符串为Integer数组 - 逗号分隔")
    void should_splitStringToIntegerArray_when_commaDelimited() {
        String idStr = "1,2,3,4";
        Integer[] ids = CommonUtil.splitId(idStr);
        assertThat(ids).hasSize(4);
        assertThat(ids[2]).isEqualTo(3);
    }

    @Test
    @DisplayName("应分割字符串为Integer数组 - 竖线分隔")
    void should_splitStringToIntegerArray_when_pipeDelimited() {
        String idStr = "1|2|3|4";
        Integer[] ids = CommonUtil.splitId(idStr, "|");
        assertThat(ids).hasSize(4);
        assertThat(ids[2]).isEqualTo(3);
    }

    @Test
    @DisplayName("应分割字符串为Long数组")
    void should_splitStringToLongArray_when_commaDelimited() {
        String idStr = "1,2,3,4";
        Long[] ids = CommonUtil.splitIdsByLong(idStr, ",");
        assertThat(ids).hasSize(4);
        assertThat(ids[2]).isEqualTo(3L);
    }

    @Test
    @DisplayName("应匹配成功 - 值在规则中")
    void should_matchTrue_when_valueInRules() {
        String rule = "10,100, 110";
        String value = "10";
        assertThat(CommonUtil.match(rule, value)).isTrue();
    }

    @Test
    @DisplayName("应匹配失败 - 值不在规则中")
    void should_matchFalse_when_valueNotInRules() {
        String rule = "10,100, 110";
        String value = "1";
        assertThat(CommonUtil.match(rule, value)).isFalse();
    }

    @Test
    @DisplayName("应匹配成功 - NOT前缀且值不在规则中")
    void should_matchTrue_when_NOTPrefixAndValueNotInRules() {
        String rule = "NOT:10,100,110";
        String value = "1";
        assertThat(CommonUtil.match(rule, value)).isTrue();
    }

    @Test
    @DisplayName("应匹配失败 - NOT前缀但值在规则中")
    void should_matchFalse_when_NOTPrefixAndValueInRules() {
        String rule = "NOT:10,100,110";
        String value = "10";
        assertThat(CommonUtil.match(rule, value)).isFalse();
    }

    @Test
    @DisplayName("应去除多余空白并保留单个空格")
    void should_replaceRedundantBlank_andKeepSingleSpace() {
        String input = "       你好 呀\n       你在干什么\t\n";
        String result = CommonUtil.replaceRedundantBlank(input);
        assertThat(result).isEqualTo("你好 呀 你在干什么");
    }

    @Test
    @DisplayName("应去除首尾空白和多余空白")
    void should_trimAndReplaceRedundantBlank() {
        String input = "  hello    world  ";
        String result = CommonUtil.replaceRedundantBlank(input);
        assertThat(result).isEqualTo("hello world");
    }
}
