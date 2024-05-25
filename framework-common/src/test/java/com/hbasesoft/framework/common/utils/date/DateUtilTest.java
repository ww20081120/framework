/**************************************************************************************** 
22 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.date;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;

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
public class DateUtilTest {

    /** number */
    private static final int NUM_14 = 14;

    /** number */
    private static final int NUM_100 = 100;

    /** number */
    private static final long NUM_A = 1536681600000L;

    /** number */
    private static final long NUM_B = 1536720835000L;

    /** number */
    private static final long NUM_C = 1536720835123L;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void string2date() {
        long t1 = NUM_A;
        long t2 = NUM_B;
        long t3 = NUM_C;
        String d8 = "20180912";
        Date date = DateUtil.parse(d8);
        Assert.isTrue(date.getTime() == t1, ErrorCodeDef.FAILURE);

        String d11 = "2018年09月12日";
        date = DateUtil.parse(d11);
        Assert.isTrue(date.getTime() == t1, ErrorCodeDef.FAILURE);

        String d10 = "2018-09-12";
        date = DateUtil.parse(d10);
        Assert.isTrue(date.getTime() == t1, ErrorCodeDef.FAILURE);

        String d102 = "2018/09/12";
        date = DateUtil.parse(d102);
        Assert.isTrue(date.getTime() == t1, ErrorCodeDef.FAILURE);

        String d14 = "20180912105355";
        date = DateUtil.parse(d14);
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.FAILURE);

        String d17 = "20180912105355123";
        date = DateUtil.parse(d17);
        Assert.isTrue(date.getTime() == t3, ErrorCodeDef.FAILURE);

        String d19 = "2018-09-12 10:53:55";
        date = DateUtil.parse(d19);
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.FAILURE);

        String d192 = "2018/09/12 10:53:55";
        date = DateUtil.parse(d192);
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.FAILURE);

        String d21 = "2018年09月12日 10时53分55秒";
        date = DateUtil.parse(d21);
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.FAILURE);

        String d23 = "2018-09-12 10:53:55.123";
        date = DateUtil.parse(d23);
        Assert.isTrue(date.getTime() == t3, ErrorCodeDef.FAILURE);

        String d232 = "2018/09/12 10:53:55.123";
        date = DateUtil.parse(d232);
        Assert.isTrue(date.getTime() == t3, ErrorCodeDef.FAILURE);

        String str = "18年9月12号10点53分55秒";
        date = DateUtil.parse(str, "yy年M月dd号hh点mm分ss秒");
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void date2string() {
        Date date = new Date(NUM_C);
        String d8 = DateUtil.format(date, DateUtil.DATE_FORMAT_8);
        Assert.equals(d8, "20180912", ErrorCodeDef.FAILURE);

        String d11 = DateUtil.format(date, DateUtil.DATE_FORMAT_11);
        Assert.equals(d11, "2018年09月12日", ErrorCodeDef.FAILURE);

        String d10 = DateUtil.format(date, DateUtil.DATE_FORMAT_10);
        Assert.equals(d10, "2018-09-12", ErrorCodeDef.FAILURE);

        String d102 = DateUtil.format(date, DateUtil.DATE_FORMAT_10_2);
        Assert.equals(d102, "2018/09/12", ErrorCodeDef.FAILURE);

        String d14 = DateUtil.format(date, DateUtil.DATETIME_FORMAT_14);
        Assert.equals(d14, "20180912105355", ErrorCodeDef.FAILURE);

        String d17 = DateUtil.format(date, DateUtil.DATETIME_FORMAT_17);
        Assert.equals(d17, "20180912105355123", ErrorCodeDef.FAILURE);

        String d19 = DateUtil.format(date);
        Assert.equals(d19, "2018-09-12 10:53:55", ErrorCodeDef.FAILURE);

        String d192 = DateUtil.format(date, DateUtil.DATETIME_FORMAT_19_2);
        Assert.equals(d192, "2018/09/12 10:53:55", ErrorCodeDef.FAILURE);

        String d21 = DateUtil.format(date, DateUtil.DATETIME_FORMAT_21);
        Assert.equals(d21, "2018年09月12日 10时53分55秒", ErrorCodeDef.FAILURE);

        String d23 = DateUtil.format(date, DateUtil.DATETIME_FORMAT_23);
        Assert.equals(d23, "2018-09-12 10:53:55.123", ErrorCodeDef.FAILURE);

        String d232 = DateUtil.format(date, DateUtil.DATETIME_FORMAT_23_2);
        Assert.equals(d232, "2018/09/12 10:53:55.123", ErrorCodeDef.FAILURE);

        String str = DateUtil.format(date, "yy年M月dd号hh点mm分ss秒");
        Assert.equals(str, "18年9月12号10点53分55秒", ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getCurrentTimestamp() {
        String timestamp = DateUtil.getCurrentTimestamp();
        Assert.isTrue(timestamp.length() == NUM_14, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getCurrentTime() {
        long s1 = DateUtil.getCurrentTime();
        long s2 = System.currentTimeMillis();
        Assert.isTrue(s1 - s2 < NUM_100, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getCurrentDate() {
        Date t = DateUtil.getCurrentDate();
        long s1 = t.getTime();
        long s2 = System.currentTimeMillis();
        Assert.isTrue(s1 - s2 < NUM_100, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void daysBetween() {
        Date t1 = DateUtil.parse("2018-09-12 11:53:55");
        Date t2 = DateUtil.parse("2018-09-13 10:53:55");
        int d = DateUtil.betweenDay(t1, t2);
        Assert.isTrue(d == 1, ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getYrMonthLastDay() {
        Date t1 = DateUtil.parse("2018-02-20");
        Date t2 = DateUtil.monthLastDay(t1);
        String str = DateUtil.format(t2, DateUtil.DATE_FORMAT_10);
        Assert.equals(str, "2018-02-28", ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getYrMonthFirstDay() {
        Date t1 = DateUtil.parse("2018-02-20");
        Date t2 = DateUtil.monthFirstDay(t1);
        String str = DateUtil.format(t2, DateUtil.DATE_FORMAT_10);
        Assert.equals(str, "2018-02-01", ErrorCodeDef.FAILURE);
    }
}
