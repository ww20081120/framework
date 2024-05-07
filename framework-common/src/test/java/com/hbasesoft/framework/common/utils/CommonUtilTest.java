/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class CommonUtilTest {

    /** number */
    private static final int NUM_3 = 3;

    /** number */
    private static final long NUM_3L = 3L;

    /** number */
    private static final int NUM_5 = 5;

    /** number */
    private static final int NUM_8 = 8;

    /** number */
    private static final int NUM_100 = 100;

    /** number */
    private static final int NUM_10000 = 10000;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void messageFormat() {
        String str = "你好，我叫{0}，我今年{1}岁了";
        str = CommonUtil.messageFormat(str, "小红", NUM_8);

        Assert.equals(str, "你好，我叫小红，我今年8岁了", ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("message format success.");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getTransactionID() {
        String str1 = CommonUtil.getTransactionID();
        String str2 = CommonUtil.getTransactionID();
        Assert.notEquals(str1, str2, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("生成了两个不一样的串码");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getRandomNumber() {
        String str1 = CommonUtil.getRandomNumber(NUM_5);
        Assert.isTrue(str1.length() == NUM_5, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("生成了一个长度为5的随机数字");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getRandomChar() {
        String str1 = CommonUtil.getRandomChar(NUM_100);
        String str2 = CommonUtil.getRandomChar(NUM_100);
        Assert.isTrue(str1.length() == NUM_100, ErrorCodeDef.SYSTEM_ERROR);
        Assert.notEquals(str1, str2, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("生成了两个不一样的随机字符串");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getString() {
        Object obj = new Object();
        String str1 = CommonUtil.getString(obj);
        Assert.equals(str1, obj.toString(), ErrorCodeDef.SYSTEM_ERROR);

        System.out.println("CommonUtil.getString调用了Object的toString方法");

        obj = null;
        str1 = CommonUtil.getString(obj);
        Assert.isNull(str1, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("null对象的toString还是null");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void notNullStr() {
        String obj = null;
        String str1 = CommonUtil.notNullStr(obj);
        Assert.equals(str1, "", ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("null字符串的notNullStr得到的是空字符串");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getDate() {
        long time = NUM_10000;
        Date d = CommonUtil.getDate(time);
        Assert.equals(time, d.getTime(), ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("将long类型转化为日期类型");

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void splitId() {
        String idStr = "1,2,3,4";
        Integer[] ids = CommonUtil.splitId(idStr);
        Assert.equals(ids[2], NUM_3, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("将逗号分割的数字转化为int[]");

        idStr = "1|2|3|4";
        ids = CommonUtil.splitId(idStr, GlobalConstants.VERTICAL_LINE);
        Assert.equals(ids[2], NUM_3, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("将竖线分割的数字转化为int[]");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void splitIdsByLong() {
        String idStr = "1,2,3,4";
        Long[] ids = CommonUtil.splitIdsByLong(idStr, GlobalConstants.SPLITOR);
        Assert.equals(ids[2], NUM_3L, ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("将逗号分割的数字转化为long[]");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void match() {
        String a = "10,100, 110";
        String b = "10";

        Assert.isTrue(CommonUtil.match(a, b), ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("字符串b在字符串a内可以找到");

        b = "1";
        Assert.isFalse(CommonUtil.match(a, b), ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("字符串b在字符串a内不能找到");

        a = "NOT:10,100,110";
        Assert.isTrue(CommonUtil.match(a, b), ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("字符串b不在字符串a内");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void removeAllSymbol() {
        String str1 = "你好!什么\"#$额%&'()*+,-./:;<=天呐>?@[\\]^_`{好吧|}~";
        String str2 = CommonUtil.removeAllSymbol(str1);
        Assert.equals(str2, "你好什么额天呐好吧", ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("字符串str1中\"!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\"这些符号都被去掉了");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void replaceAllBlank() {
        String str1 = "       你好呀\n       你在干什么\t\n";
        String str2 = CommonUtil.replaceAllBlank(str1);
        Assert.equals(str2, "你好呀你在干什么", ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("去掉字符串str1中的空格、换行、制表符号");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void replaceRedundantBlank() {
        String str1 = "       你好 呀\n       你在干什么\t\n";
        String str2 = CommonUtil.replaceRedundantBlank(str1);
        Assert.equals(str2, "你好 呀 你在干什么", ErrorCodeDef.SYSTEM_ERROR);
        System.out.println("去掉字符串str1中的首尾空格，以及多余的空格、换行、制表符号");
    }
}
