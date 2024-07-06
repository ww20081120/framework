/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年11月6日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtil {

    /** yyyy-MM-dd */
    public static final String DATE_FORMAT_10 = "yyyy-MM-dd";

    /** yyyy/MM/dd */
    public static final String DATE_FORMAT_10_2 = "yyyy/MM/dd";

    /** yyyy年MM月dd日 */
    public static final String DATE_FORMAT_11 = "yyyy年MM月dd日";

    /** yyyyMMdd */
    public static final String DATE_FORMAT_8 = "yyyyMMdd";

    /** DATE LENGTH */
    private static final int DATE_LENGTH_10 = 10;

    /** DATE LENGTH */
    private static final int DATE_LENGTH_11 = 11;

    /** DATE LENGTH */
    private static final int DATE_LENGTH_14 = 14;

    /** DATE LENGTH */
    private static final int DATE_LENGTH_17 = 17;

    /** DATE LENGTH */
    private static final int DATE_LENGTH_19 = 19;

    /** DATE LENGTH */
    private static final int DATE_LENGTH_21 = 21;

    /** DATE LENGTH */
    private static final int DATE_LENGTH_23 = 23;

    /** DATE LENGTH */
    private static final int DATE_LENGTH_8 = 8;

    /** yyyyMMddHHmmss */
    public static final String DATETIME_FORMAT_14 = "yyyyMMddHHmmss";

    /** yyyyMMddHHmmssSSS */
    public static final String DATETIME_FORMAT_17 = "yyyyMMddHHmmssSSS";

    /** yyyy-MM-dd HH:mm:ss */
    public static final String DATETIME_FORMAT_19 = "yyyy-MM-dd HH:mm:ss";

    /** yyyy-MM-dd HH:mm:ss */
    public static final String DATETIME_FORMAT_19_2 = "yyyy/MM/dd HH:mm:ss";

    /** yyyy年MM月dd日 HH时mm分ss秒 */
    public static final String DATETIME_FORMAT_21 = "yyyy年MM月dd日 HH时mm分ss秒";

    /** yyyy-MM-dd HH:mm:ss.SSS */
    public static final String DATETIME_FORMAT_23 = "yyyy-MM-dd HH:mm:ss.SSS";

    /** yyyy/MM/dd HH:mm:ss.SSS */
    public static final String DATETIME_FORMAT_23_2 = "yyyy/MM/dd HH:mm:ss.SSS";

    /** 天 */
    public static final int DAY = Calendar.DAY_OF_YEAR;

    /** 当天的秒数 */
    private static final long DAYTIME = 86400000L;

    /** 小时 */
    public static final int HOUR = Calendar.HOUR;

    /** 分钟 */
    public static final int MINUTE = Calendar.MINUTE;

    /** 月 */
    public static final int MONTH = Calendar.MONTH;

    /** 秒 */
    public static final int SECOND = Calendar.SECOND;

    /** 时区 */
    private static final long TIMEAREA = 28800000L;

    /** WEEK_LENGTH_7 */
    private static final int WEEK_LENGTH = 7;

    /** 年 */
    public static final int YEAR = Calendar.YEAR;

    /**
     * 获取一年内，两个日期之间间隔
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param field 相差类型
     * @return 相差天数
     */
    public static int between(final Date startDate, final Date endDate, final int field) {
        Calendar clendar = Calendar.getInstance();
        clendar.setTime(startDate);
        int start = clendar.get(field);
        clendar.setTime(endDate);
        int end = clendar.get(field);
        return end - start;
    }

    /**
     * 两个日期之间间隔的天数
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 相差天数
     */
    public static int betweenDay(final Date startDate, final Date endDate) {
        return between(startDate, endDate, DAY);
    }

    /**
     * 两个日期之间间隔的小时数
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 相差小时数
     */
    public static int betweenHour(final Date startDate, final Date endDate) {
        return between(startDate, endDate, HOUR);
    }

    /**
     * 两个日期之间间隔的分钟
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 相差分钟
     */
    public static int betweenMinute(final Date startDate, final Date endDate) {
        return between(startDate, endDate, MINUTE);
    }

    /**
     * 两个日期之间间隔的月
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 相差月
     */
    public static int betweenMonth(final Date startDate, final Date endDate) {
        return between(startDate, endDate, DAY);
    }

    /**
     * 两个日期之间间隔的秒
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 相差秒
     */
    public static int betweenSecond(final Date startDate, final Date endDate) {
        return between(startDate, endDate, SECOND);
    }

    /**
     * 两个日期之间间隔的年
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 相差年
     */
    public static int betweenYear(final Date startDate, final Date endDate) {
        return between(startDate, endDate, YEAR);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param date <br>
     * @return <br>
     */
    public static String format(final Date date) {
        return format(date, DateUtil.DATETIME_FORMAT_19);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param date <br>
     * @param format <br>
     * @return <br>
     */
    public static String format(final Date date, final String format) {
        String result = null;
        if (date != null) {
            DateFormat sdf = new SimpleDateFormat(format);
            result = sdf.format(date);
        }
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * Description: getCurrentTime<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Description: getCurrentTimestamp<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String getCurrentTimestamp() {
        return format(getCurrentDate(), DateUtil.DATETIME_FORMAT_14);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date lastMonth() {
        return lastMonth(getCurrentDate());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date lastMonth(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date lastWeek() {
        return lastWeek(getCurrentDate());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date lastWeek(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_MONTH, 0 - WEEK_LENGTH);
        return calendar.getTime();
    }

    /**
     * Description: 获取午夜<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date midnight() {
        return midnight(getCurrentDate());
    }

    /**
     * Description: 获取午夜 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date midnight(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME)) - 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time); // 将给定的Date设置到Calendar中
        calendar.add(DAY, 1);
        return calendar.getTime();
    }

    /**
     * Description: 获取星期一<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date monday() {
        return monday(getCurrentDate());
    }

    /**
     * Description: 获取星期一<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date monday(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time); // 将给定的Date设置到Calendar中

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 获取当前日期是一周中的哪一天
        // 注意：Calendar.SUNDAY = 1, Calendar.MONDAY = 2, ..., 因此需要校正偏移量
        if (dayOfWeek == Calendar.MONDAY) {
            return calendar.getTime(); // 如果已经是星期一，直接返回原日期
        }
        calendar.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - Calendar.MONDAY)); // 调整到星期一
        return calendar.getTime(); // 返回调整后的星期一日期
    }

    /**
     * Description:获取本月第一天 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date monthFirstDay() {
        return monthFirstDay(getCurrentDate());
    }

    /**
     * Description:获取月份第一天 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date monthFirstDay(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime(); // 返回该月份第一天的Date对象
    }

    /**
     * Description:获取本月最后一天 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date monthLastDay() {
        return monthLastDay(getCurrentDate());
    }

    /**
     * Description: 获取月份最后一天<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date monthLastDay(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        // 将日期设置为下个月的第一天
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 再减去一天，得到本月的最后一天
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime(); // 返回该月份最后一天的Date对象
    }

    /**
     * Description: 获取凌晨<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date morning() {
        return morning(getCurrentDate());
    }

    /**
     * Description: 获取凌晨<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date morning(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time); // 将给定的Date设置到Calendar中
        return calendar.getTime();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date nextMonth() {
        return nextMonth(getCurrentDate());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date nextMonth(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date nextWeek() {
        return nextWeek(getCurrentDate());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date nextWeek(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_MONTH, WEEK_LENGTH);
        return calendar.getTime();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @param field
     * @param amount
     * @return <br>
     */
    public static Date offset(final Date date, final int field, final int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @param amount
     * @return <br>
     */
    public static Date offset(final int field, final int amount) {
        return offset(DateUtil.getCurrentDate(), field, amount);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @param amount
     * @return <br>
     */
    public static Date offsetDay(final Date date, final int amount) {
        return offset(date, amount, DAY);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param amount
     * @return <br>
     */
    public static Date offsetDay(final int amount) {
        return offset(DateUtil.getCurrentDate(), amount, DAY);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @param amount
     * @return <br>
     */
    public static Date offsetHour(final Date date, final int amount) {
        return offset(date, amount, HOUR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param amount
     * @return <br>
     */
    public static Date offsetHour(final int amount) {
        return offset(DateUtil.getCurrentDate(), amount, HOUR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @param amount
     * @return <br>
     */
    public static Date offsetMinute(final Date date, final int amount) {
        return offset(date, amount, MINUTE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param amount
     * @return <br>
     */
    public static Date offsetMinute(final int amount) {
        return offset(DateUtil.getCurrentDate(), amount, MINUTE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @param amount
     * @return <br>
     */
    public static Date offsetMonth(final Date date, final int amount) {
        return offset(date, amount, MONTH);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param amount
     * @return <br>
     */
    public static Date offsetMonth(final int amount) {
        return offset(DateUtil.getCurrentDate(), amount, MONTH);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @param amount
     * @return <br>
     */
    public static Date offsetSecond(final Date date, final int amount) {
        return offset(date, amount, SECOND);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param amount
     * @return <br>
     */
    public static Date offsetSecond(final int amount) {
        return offset(DateUtil.getCurrentDate(), amount, SECOND);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @param amount
     * @return <br>
     */
    public static Date offsetYear(final Date date, final int amount) {
        return offset(date, amount, YEAR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param amount
     * @return <br>
     */
    public static Date offsetYear(final int amount) {
        return offset(DateUtil.getCurrentDate(), amount, YEAR);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param ds <br>
     * @return <br>
     */
    public static Date parse(final String ds) {
        if (StringUtils.isEmpty(ds)) {
            return null;
        }
        String dateStr = ds.trim();
        Date date = null;
        switch (dateStr.length()) {
            case DATE_LENGTH_8:
                date = parse(dateStr, DateUtil.DATE_FORMAT_8);
                break;
            case DATE_LENGTH_10:
                date = parse(dateStr, dateStr.indexOf("/") == -1 ? DateUtil.DATE_FORMAT_10 : DateUtil.DATE_FORMAT_10_2);
                break;
            case DATE_LENGTH_11:
                date = parse(dateStr, DateUtil.DATE_FORMAT_11);
                break;
            case DATE_LENGTH_14:
                date = parse(dateStr, DateUtil.DATETIME_FORMAT_14);
                break;
            case DATE_LENGTH_17:
                date = parse(dateStr, DateUtil.DATETIME_FORMAT_17);
                break;
            case DATE_LENGTH_19:
                date = parse(dateStr,
                    dateStr.indexOf("/") == -1 ? DateUtil.DATETIME_FORMAT_19 : DateUtil.DATETIME_FORMAT_19_2);
                break;
            case DATE_LENGTH_21:
                date = parse(dateStr, DateUtil.DATETIME_FORMAT_21);
                break;
            case DATE_LENGTH_23:
                date = parse(dateStr,
                    dateStr.indexOf("/") == -1 ? DateUtil.DATETIME_FORMAT_23 : DateUtil.DATETIME_FORMAT_23_2);
                break;
            default:
                throw new IllegalArgumentException(dateStr + "不支持的时间格式");
        }
        return date;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param date <br>
     * @param format <br>
     * @return <br>
     */
    public static Date parse(final String date, final String format) {
        if (StringUtils.isEmpty(format)) {
            throw new IllegalArgumentException("the date format string is null!");
        }
        DateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date.trim());
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("the date string " + date + " is not matching format: " + format, e);
        }
    }

    /**
     * Description: 获取星期天<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date sunday() {
        return sunday(getCurrentDate());
    }

    /**
     * Description: 获取星期天<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date sunday(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time); // 将给定的Date设置到Calendar中

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 获取当前日期是一周中的哪一天
        // 注意：Calendar.SUNDAY = 1, Calendar.MONDAY = 2, ..., 因此需要校正偏移量
        if (dayOfWeek == Calendar.SUNDAY) {
            return calendar.getTime(); // 如果已经是星期天，直接返回原日期
        }
        calendar.add(Calendar.DAY_OF_MONTH, Calendar.SUNDAY - dayOfWeek); // 调整到星期一
        return calendar.getTime(); // 返回调整后的星期一日期
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date tomorrow() {
        return tomorrow(getCurrentDate());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date tomorrow(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Date yesterday() {
        return yesterday(getCurrentDate());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param date
     * @return <br>
     */
    public static Date yesterday(final Date date) {
        long createTime = date.getTime();
        long time = createTime - ((createTime + TIMEAREA) % (DAYTIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }
}
