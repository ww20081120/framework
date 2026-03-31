package com.hbasesoft.framework.common.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    // ==================== wildcardMatch(String, String) 测试 ====================

    @Test
    @DisplayName("应匹配成功 - 星号通配符匹配任意字符")
    void should_matchSuccess_when_asteriskWildcard() {
        assertThat(CommonUtil.wildcardMatch("*.txt", "test.txt")).isTrue();
        assertThat(CommonUtil.wildcardMatch("abc*", "abcdef")).isTrue();
        assertThat(CommonUtil.wildcardMatch("*abc", "123abc")).isTrue();
        assertThat(CommonUtil.wildcardMatch("a*c", "abc")).isTrue();
    }

    @Test
    @DisplayName("应匹配成功 - 问号通配符匹配单个字符")
    void should_matchSuccess_when_questionMarkWildcard() {
        assertThat(CommonUtil.wildcardMatch("?", "a")).isTrue();
        assertThat(CommonUtil.wildcardMatch("???", "abc")).isTrue();
        assertThat(CommonUtil.wildcardMatch("a?c", "abc")).isTrue();
        assertThat(CommonUtil.wildcardMatch("test?.txt", "test1.txt")).isTrue();
    }

    @Test
    @DisplayName("应匹配成功 - 混合通配符")
    void should_matchSuccess_when_mixedWildcards() {
        assertThat(CommonUtil.wildcardMatch("*.???", "test.txt")).isTrue();
        assertThat(CommonUtil.wildcardMatch("a*b?c", "aXYZbYc")).isTrue();
        assertThat(CommonUtil.wildcardMatch("*_test_?", "prefix_test_1")).isTrue();
    }

    @Test
    @DisplayName("应匹配成功 - 精确匹配")
    void should_matchSuccess_when_exactMatch() {
        assertThat(CommonUtil.wildcardMatch("hello", "hello")).isTrue();
        assertThat(CommonUtil.wildcardMatch("test.txt", "test.txt")).isTrue();
    }

    @Test
    @DisplayName("应匹配失败 - 不符合规则")
    void should_matchFail_when_notMatchRule() {
        assertThat(CommonUtil.wildcardMatch("*.txt", "test.doc")).isFalse();
        assertThat(CommonUtil.wildcardMatch("???", "ab")).isFalse();
        assertThat(CommonUtil.wildcardMatch("abc", "abcd")).isFalse();
    }

    @Test
    @DisplayName("应正确处理特殊字符 - 转义字符")
    void should_handleSpecialCharacters_when_escapedChars() {
        assertThat(CommonUtil.wildcardMatch("test\\.txt", "test.txt")).isTrue();
        assertThat(CommonUtil.wildcardMatch("$100", "$100")).isTrue();
        assertThat(CommonUtil.wildcardMatch("^start", "^start")).isTrue();
    }

    @Test
    @DisplayName("应匹配成功 - 星号匹配所有内容")
    void should_matchAnything_when_onlyAsterisk() {
        assertThat(CommonUtil.wildcardMatch("*", "anything")).isTrue();
        assertThat(CommonUtil.wildcardMatch("*", "")).isTrue();
        assertThat(CommonUtil.wildcardMatch("*", "test.txt")).isTrue();
    }

    // ==================== wildcardMatch(Collection<String>, String) 测试 ====================

    @Test
    @DisplayName("应返回true - 至少一条规则匹配")
    void should_returnTrue_when_atLeastOneRuleMatches() {
        java.util.Collection<String> rules = Arrays.asList("*.txt", "*.doc", "*.pdf");
        assertThat(CommonUtil.wildcardMatch(rules, "test.txt")).isTrue();
        assertThat(CommonUtil.wildcardMatch(rules, "doc.doc")).isTrue();
    }

    @Test
    @DisplayName("应返回false - 所有规则都不匹配")
    void should_returnFalse_when_noRuleMatches() {
        java.util.Collection<String> rules = Arrays.asList("*.txt", "*.doc");
        assertThat(CommonUtil.wildcardMatch(rules, "test.pdf")).isFalse();
        assertThat(CommonUtil.wildcardMatch(rules, "image.jpg")).isFalse();
    }

    @Test
    @DisplayName("应返回false - 规则集合为空")
    void should_returnFalse_when_emptyRules() {
        java.util.Collection<String> rules = Arrays.asList();
        assertThat(CommonUtil.wildcardMatch(rules, "test.txt")).isFalse();
    }

    @Test
    @DisplayName("应正确匹配 - 包含多种通配符规则")
    void should_matchCorrectly_when_mixedWildcardRules() {
        java.util.Collection<String> rules = Arrays.asList("test*", "*.txt", "abc?");
        assertThat(CommonUtil.wildcardMatch(rules, "testing")).isTrue();
        assertThat(CommonUtil.wildcardMatch(rules, "file.txt")).isTrue();
        assertThat(CommonUtil.wildcardMatch(rules, "abcd")).isTrue();
    }

    // ==================== getOrSetDefault(Map, K, Supplier<V>) 测试 ====================

    @Test
    @DisplayName("应返回并设置默认值 - key不存在")
    void should_returnAndSetDefault_when_keyNotExists() {
        Map<String, String> map = new HashMap<>();
        String result = CommonUtil.getOrSetDefault(map, "test", () -> "default");

        assertThat(result).isEqualTo("default");
        assertThat(map).hasSize(1);
        assertThat(map.get("test")).isEqualTo("default");
    }

    @Test
    @DisplayName("应返回已存在的值 - key已存在")
    void should_returnExistingValue_when_keyExists() {
        Map<String, String> map = new HashMap<>();
        map.put("test", "existing");

        String result = CommonUtil.getOrSetDefault(map, "test", () -> "default");

        assertThat(result).isEqualTo("existing");
        assertThat(map).hasSize(1);
        assertThat(map.get("test")).isEqualTo("existing");
    }

    @Test
    @DisplayName("应调用Supplier获取默认值 - 仅在key不存在时")
    void should_invokeSupplierOnly_when_keyNotExists() {
        Map<String, Integer> map = new HashMap<>();
        java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(0);

        Integer result1 = CommonUtil.getOrSetDefault(map, "key", counter::incrementAndGet);
        Integer result2 = CommonUtil.getOrSetDefault(map, "key", counter::incrementAndGet);

        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(1);
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("应支持复杂对象类型")
    void should_supportComplexObjectTypes() {
        Map<String, java.util.List<String>> map = new HashMap<>();

        java.util.List<String> list = CommonUtil.getOrSetDefault(map, "list", java.util.ArrayList::new);
        list.add("item1");

        assertThat(map.get("list")).hasSize(1);
        assertThat(map.get("list")).contains("item1");
    }

    @Test
    @DisplayName("应为不同的key设置不同的默认值")
    void should_setDifferentDefaults_for_differentKeys() {
        Map<String, Integer> map = new HashMap<>();

        Integer value1 = CommonUtil.getOrSetDefault(map, "key1", () -> 100);
        Integer value2 = CommonUtil.getOrSetDefault(map, "key2", () -> 200);

        assertThat(value1).isEqualTo(100);
        assertThat(value2).isEqualTo(200);
        assertThat(map).hasSize(2);
    }

    @Test
    @DisplayName("应支持null值存储")
    void should_handleNullValue() {
        Map<String, String> map = new HashMap<>();

        String result = CommonUtil.getOrSetDefault(map, "test", () -> null);

        assertThat(result).isNull();
        assertThat(map).hasSize(1);
        assertThat(map.containsKey("test")).isTrue();
    }
}
