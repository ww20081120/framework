/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class AssertTest {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void notNull() {
        Object obj = new Object();
        Assert.notNull(obj, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("obj 不是null");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void isNull() {
        Object obj = null;
        Assert.isNull(obj, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("obj 是null对象");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void notEmpty() {
        String str = "hello world";
        Assert.notEmpty(str, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("str 不是空字符串");

        List<String> strList = new ArrayList<>();
        strList.add(str);
        Assert.notEmpty(strList, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("strList 不是空集合");

        String[] strs = new String[] {
            str
        };
        Assert.notEmpty(strs, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("strs 不是空数组");

        Map<String, String> strMap = new HashMap<>();
        strMap.put("a", str);
        Assert.notEmpty(strMap, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("strMap 不是空Map");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void isEmpty() {
        String str = null;
        Assert.isEmpty(str, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("str 是null字符串");

        str = "";
        Assert.isEmpty(str, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("str 是空字符串");

        List<String> strList = null;
        Assert.isEmpty(strList, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("strList 是null集合");

        strList = new ArrayList<>();
        Assert.isEmpty(strList, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("strList 是空集合");

        String[] strs = null;
        Assert.isEmpty(strs, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("strs 是null数组");

        strs = new String[0];
        Assert.isEmpty(strs, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("strs 是空数组");

        Map<String, String> strMap = null;
        Assert.isEmpty(strMap, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("strMap 是null Map");

        strMap = new HashMap<>();
        Assert.isEmpty(strMap, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("strMap 是空 Map");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void equals() {
        String a = "abc";
        String b = "abc";
        Assert.equals(a, b, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("字符串a equals 字符串b");

        Object c = new Object();
        Object d = c;
        Assert.equals(c, d, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("对象c equals 对象d");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void notEquals() {
        String a = "abc";
        String b = "abd";
        Assert.notEquals(a, b, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("字符串a not equals 字符串b");

        Object c = new Object();
        Object d = new Object();
        Assert.notEquals(c, d, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("对象c not equals 对象d");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void isTrue() {
        String a = "abc";
        String b = new String("abc");
        Assert.isTrue(a.equals(b), ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("字符串a equals 字符串b");

        Assert.isTrue(a != b, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("字符串a != 字符串b");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void isFalse() {
        int a = 1;
        int b = 2;
        Assert.isFalse(a > b, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("数字a > 数字b 是错误的");
    }
}
