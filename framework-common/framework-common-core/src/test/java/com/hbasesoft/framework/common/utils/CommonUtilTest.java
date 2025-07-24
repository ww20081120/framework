/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <Description> CommonUtil Test <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @CreateDate 2025年8月15日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils <br>
 */
public class CommonUtilTest {

    @Test
    public void testMessageFormat() {
        // 测试消息格式化
        String result = CommonUtil.messageFormat("Hello {0}, welcome to {1}!", "John", "Framework");
        assertEquals("Hello John, welcome to Framework!", result);

        // 测试空消息
        String emptyResult = CommonUtil.messageFormat(null, "param");
        assertNull(emptyResult);

        // 测试无参数
        String noParamResult = CommonUtil.messageFormat("Hello World");
        assertEquals("Hello World", noParamResult);
    }

    @Test
    public void testGetTransactionID() {
        // 测试获取事务ID
        String transactionId = CommonUtil.getTransactionID();
        assertNotNull(transactionId);
        assertFalse(transactionId.contains("-"));
        assertEquals(32, transactionId.length());
    }

    @Test
    public void testGetRandomCode() {
        // 测试获取随机码
        String randomCode = CommonUtil.getRandomCode();
        assertNotNull(randomCode);
        assertTrue(randomCode.length() > 0);
    }

    @Test
    public void testGetRandomNumber() {
        // 测试获取指定长度的随机数字
        String randomNumber = CommonUtil.getRandomNumber(6);
        assertNotNull(randomNumber);
        assertEquals(6, randomNumber.length());
        assertTrue(randomNumber.matches("\\d{6}"));
    }

    @Test
    public void testGetRandomChar() {
        // 测试获取指定长度的随机字符
        String randomChar = CommonUtil.getRandomChar(8);
        assertNotNull(randomChar);
        assertEquals(8, randomChar.length());
    }

    @Test
    public void testGetString() {
        // 测试对象转字符串
        assertEquals("test", CommonUtil.getString("test"));
        assertEquals("123", CommonUtil.getString(123));
        assertEquals("123.45", CommonUtil.getString(123.45));
        assertNull(CommonUtil.getString(null));
    }

    @Test
    public void testNotNullStr() {
        // 测试非空字符串
        assertEquals("test", CommonUtil.notNullStr("test"));
        assertEquals("", CommonUtil.notNullStr(null));
        assertEquals("", CommonUtil.notNullStr(""));
    }

    @Test
    public void testSplitId() {
        // 测试分割ID字符串
        Integer[] ids = CommonUtil.splitId("1,2,3");
        assertNotNull(ids);
        assertEquals(3, ids.length);
        assertEquals(Integer.valueOf(1), ids[0]);
        assertEquals(Integer.valueOf(2), ids[1]);
        assertEquals(Integer.valueOf(3), ids[2]);

        // 测试空ID字符串
        Integer[] emptyIds = CommonUtil.splitId(null);
        assertNull(emptyIds);
    }

    @Test
    public void testSplitIdsByLong() {
        // 测试分割长整型ID字符串
        Long[] ids = CommonUtil.splitIdsByLong("1,2,3", ",");
        assertNotNull(ids);
        assertEquals(3, ids.length);
        assertEquals(Long.valueOf(1), ids[0]);
        assertEquals(Long.valueOf(2), ids[1]);
        assertEquals(Long.valueOf(3), ids[2]);
    }

    @Test
    public void testWildcardMatch() {
        // 测试通配符匹配
        assertTrue(CommonUtil.wildcardMatch("test*", "test123"));
        assertTrue(CommonUtil.wildcardMatch("*test", "123test"));
        assertTrue(CommonUtil.wildcardMatch("test?23", "test123"));
        assertFalse(CommonUtil.wildcardMatch("test", "test123"));

        // 测试集合通配符匹配
        List<String> rules = Arrays.asList("test*", "*abc");
        assertTrue(CommonUtil.wildcardMatch(rules, "test123"));
        assertTrue(CommonUtil.wildcardMatch(rules, "123abc"));
        assertFalse(CommonUtil.wildcardMatch(rules, "xyz"));
    }

    @Test
    public void testMatch() {
        // 测试匹配
        assertTrue(CommonUtil.match("*", "anything"));
        assertTrue(CommonUtil.match("test", "test"));
        assertFalse(CommonUtil.match("test", "testing"));

        // 测试NOT匹配
        assertTrue(CommonUtil.match("NOT:abc", "def"));
        assertFalse(CommonUtil.match("NOT:abc", "abc"));

        // 测试分隔符匹配
        assertTrue(CommonUtil.match("a,b,c", "b"));
        assertFalse(CommonUtil.match("a,b,c", "d"));
    }

    @Test
    public void testRemoveAllSymbol() {
        // 测试移除所有符号
        String result = CommonUtil.removeAllSymbol("Hello, World! 123");
        assertEquals("Hello World 123", result);

        // 测试空字符串
        String emptyResult = CommonUtil.removeAllSymbol(null);
        assertEquals("", emptyResult);
    }

    @Test
    public void testReplaceAllBlank() {
        // 测试移除所有空白字符
        String result = CommonUtil.replaceAllBlank("Hello\t\n World  \r\n");
        assertEquals("HelloWorld", result);

        // 测试空字符串
        String emptyResult = CommonUtil.replaceAllBlank(null);
        assertEquals("", emptyResult);
    }

    @Test
    public void testReplaceRedundantBlank() {
        // 测试替换多余空白字符
        String result = CommonUtil.replaceRedundantBlank("Hello\t\n  World  \r\n");
        assertEquals("Hello World", result);

        // 测试空字符串
        String emptyResult = CommonUtil.replaceRedundantBlank(null);
        assertEquals("", emptyResult);
    }
}
