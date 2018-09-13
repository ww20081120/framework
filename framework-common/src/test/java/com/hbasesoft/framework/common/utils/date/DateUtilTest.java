/**************************************************************************************** 
22 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.date;

import java.util.Date;

import org.junit.Test;

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

    @Test
    public void string2Date() {
        long t1 = 1536681600000l;
        long t2 = 1536720835000l;
        long t3 = 1536720835123l;
        String DATE_FORMAT_8 = "20180912";
        Date date = DateUtil.string2Date(DATE_FORMAT_8);
        Assert.isTrue(date.getTime() == t1, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATE_FORMAT_11 = "2018年09月12日";
        date = DateUtil.string2Date(DATE_FORMAT_11);
        Assert.isTrue(date.getTime() == t1, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATE_FORMAT_10 = "2018-09-12";
        date = DateUtil.string2Date(DATE_FORMAT_10);
        Assert.isTrue(date.getTime() == t1, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATE_FORMAT_10_2 = "2018/09/12";
        date = DateUtil.string2Date(DATE_FORMAT_10_2);
        Assert.isTrue(date.getTime() == t1, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_14 = "20180912105355";
        date = DateUtil.string2Date(DATETIME_FORMAT_14);
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_17 = "20180912105355123";
        date = DateUtil.string2Date(DATETIME_FORMAT_17);
        Assert.isTrue(date.getTime() == t3, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_19 = "2018-09-12 10:53:55";
        date = DateUtil.string2Date(DATETIME_FORMAT_19);
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_19_2 = "2018/09/12 10:53:55";
        date = DateUtil.string2Date(DATETIME_FORMAT_19_2);
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_21 = "2018年09月12日 10时53分55秒";
        date = DateUtil.string2Date(DATETIME_FORMAT_21);
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_23 = "2018-09-12 10:53:55.123";
        date = DateUtil.string2Date(DATETIME_FORMAT_23);
        Assert.isTrue(date.getTime() == t3, ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_23_2 = "2018/09/12 10:53:55.123";
        date = DateUtil.string2Date(DATETIME_FORMAT_23_2);
        Assert.isTrue(date.getTime() == t3, ErrorCodeDef.SYSTEM_ERROR_10001);

        String str = "18年9月12号10点53分55秒";
        date = DateUtil.string2Date(str, "yy年M月dd号hh点mm分ss秒");
        Assert.isTrue(date.getTime() == t2, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void date2String() {
        Date date = new Date(1536720835123l);
        String DATE_FORMAT_8 = DateUtil.date2String(date, DateConstants.DATE_FORMAT_8);
        Assert.equals(DATE_FORMAT_8, "20180912", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATE_FORMAT_11 = DateUtil.date2String(date, DateConstants.DATE_FORMAT_11);
        Assert.equals(DATE_FORMAT_11, "2018年09月12日", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATE_FORMAT_10 = DateUtil.date2String(date, DateConstants.DATE_FORMAT_10);
        Assert.equals(DATE_FORMAT_10, "2018-09-12", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATE_FORMAT_10_2 = DateUtil.date2String(date, DateConstants.DATE_FORMAT_10_2);
        Assert.equals(DATE_FORMAT_10_2, "2018/09/12", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_14 = DateUtil.date2String(date, DateConstants.DATETIME_FORMAT_14);
        Assert.equals(DATETIME_FORMAT_14, "20180912105355", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_17 = DateUtil.date2String(date, DateConstants.DATETIME_FORMAT_17);
        Assert.equals(DATETIME_FORMAT_17, "20180912105355123", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_19 = DateUtil.date2String(date);
        Assert.equals(DATETIME_FORMAT_19, "2018-09-12 10:53:55", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_19_2 = DateUtil.date2String(date, DateConstants.DATETIME_FORMAT_19_2);
        Assert.equals(DATETIME_FORMAT_19_2, "2018/09/12 10:53:55", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_21 = DateUtil.date2String(date, DateConstants.DATETIME_FORMAT_21);
        Assert.equals(DATETIME_FORMAT_21, "2018年09月12日 10时53分55秒", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_23 = DateUtil.date2String(date, DateConstants.DATETIME_FORMAT_23);
        Assert.equals(DATETIME_FORMAT_23, "2018-09-12 10:53:55.123", ErrorCodeDef.SYSTEM_ERROR_10001);

        String DATETIME_FORMAT_23_2 = DateUtil.date2String(date, DateConstants.DATETIME_FORMAT_23_2);
        Assert.equals(DATETIME_FORMAT_23_2, "2018/09/12 10:53:55.123", ErrorCodeDef.SYSTEM_ERROR_10001);

        String str = DateUtil.date2String(date, "yy年M月dd号hh点mm分ss秒");
        Assert.equals(str, "18年9月12号10点53分55秒", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void getCurrentTimestamp() {
        String timestamp = DateUtil.getCurrentTimestamp();
        Assert.isTrue(timestamp.length() == 14, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void getCurrentTime() {
        long s1 = DateUtil.getCurrentTime();
        long s2 = System.currentTimeMillis();
        Assert.isTrue(s1 - s2 < 100, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void getCurrentDate() {
        Date t = DateUtil.getCurrentDate();
        long s1 = t.getTime();
        long s2 = System.currentTimeMillis();
        Assert.isTrue(s1 - s2 < 100, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void daysBetween() {
        Date t1 = DateUtil.string2Date("2018-09-12 11:53:55");
        Date t2 = DateUtil.string2Date("2018-09-13 10:53:55");
        int d = DateUtil.daysBetween(t2, t1);
        Assert.isTrue(d == 1, ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void getYrMonthLastDay() {
        Date t1 = DateUtil.string2Date("2018-02-20");
        Date t2 = DateUtil.getYrMonthLastDay(t1);
        String str = DateUtil.date2String(t2, DateConstants.DATE_FORMAT_10);
        Assert.equals(str, "2018-02-28", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void getYrMonthFirstDay() {
        Date t1 = DateUtil.string2Date("2018-02-20");
        Date t2 = DateUtil.getYrMonthFirstDay(t1);
        String str = DateUtil.date2String(t2, DateConstants.DATE_FORMAT_10);
        Assert.equals(str, "2018-02-01", ErrorCodeDef.SYSTEM_ERROR_10001);
    }
}
