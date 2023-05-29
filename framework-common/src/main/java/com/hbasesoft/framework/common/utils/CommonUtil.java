/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtil {

    /** UUID_MIN_LENGTH */
    private static final int UUID_MIN_LENGTH = 24;

    /** UNSIGNED_LONG */
    private static final int UNSIGNED_LONG = 16;

    /** UNSIGNED_STRING */
    private static final int UNSIGNED_STRING = 36;

    /** NEXT_INT */
    private static final int NEXT_INT = 10;

    /** NEXT_INT */
    private static final int NEXT_INT_3 = 3;

    /** NEXT_CHAR */
    private static final int NEXT_CHAR = 26;

    /**
     * 消息格式化
     * 
     * @param message message <br>
     * @param params params <br>
     * @return String <br>
     */
    public static String messageFormat(final String message, final Object... params) {
        return (StringUtils.isEmpty(message) || params == null || params.length == 0) ? message
            : MessageFormat.format(message, params);
    }

    /**
     * 获取事务ID
     * 
     * @return 事务ID <br>
     */
    public static String getTransactionID() {
        return UUID.randomUUID().toString().replace("-", GlobalConstants.BLANK);
    }

    /**
     * Description: 根据UUID的hash code生成随机码<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String getRandomCode() {
        return Long.toUnsignedString(
            Long.parseUnsignedLong(UUID.randomUUID().toString().substring(UUID_MIN_LENGTH), UNSIGNED_LONG),
            UNSIGNED_STRING);
    }

    /**
     * Description: 获取指定位数的随机数<br>
     * 
     * @author 王伟 <br>
     * @param size <br>
     * @return <br>
     */
    public static String getRandomNumber(final int size) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            sb.append((char) ('0' + random.nextInt(NEXT_INT)));
        }
        return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param size <br>
     * @return <br>
     */
    public static String getRandomChar(final int size) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            switch (random.nextInt(NEXT_INT) % NEXT_INT_3) {
                case 0:
                    sb.append((char) ('0' + random.nextInt(NEXT_INT)));
                    break;
                case 1:
                    sb.append((char) ('a' + random.nextInt(NEXT_CHAR)));
                    break;
                case 2:
                    sb.append((char) ('A' + random.nextInt(NEXT_CHAR)));
                    break;
                default:
            }
        }
        return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj <br>
     * @return <br>
     */
    public static String getString(final Object obj) {
        String result = null;
        if (obj != null) {
            if (obj instanceof String) {
                result = (String) obj;
            }
            else if (obj instanceof BigDecimal) {
                result = ((BigDecimal) obj).stripTrailingZeros().toPlainString();
            }
            else {
                result = obj.toString();
            }
        }
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @return <br>
     */
    public static String notNullStr(final String str) {
        return StringUtils.isEmpty(str) ? GlobalConstants.BLANK : str;
    }

    /**
     * Description:getDate <br>
     * 
     * @author 王伟 <br>
     * @param time <br
     * @return <br>
     */
    public static Date getDate(final Long time) {
        if (time != null) {
            return new Date(time);
        }
        return null;
    }

    /**
     * Description: 从拼接的字符串中获取id数组<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idStr
     * @param splitor
     * @return <br>
     */
    public static Integer[] splitId(final String idStr, final String splitor) {
        Integer[] ids = null;
        if (StringUtils.isNotEmpty(idStr)) {
            String[] strs = StringUtils.split(idStr, splitor);
            ids = new Integer[strs.length];
            for (int i = 0; i < strs.length; i++) {
                ids[i] = Integer.valueOf(strs[i]);
            }
        }
        return ids;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idStr
     * @param splitor
     * @return <br>
     */
    public static Long[] splitIdsByLong(final String idStr, final String splitor) {
        Long[] ids = null;
        if (StringUtils.isNotEmpty(idStr)) {
            String[] strs = StringUtils.split(idStr, splitor);
            ids = new Long[strs.length];
            for (int i = 0; i < strs.length; i++) {
                ids[i] = Long.valueOf(strs[i]);
            }
        }
        return ids;
    }

    /**
     * Description: 从拼接的字符串中获取id数组<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idStr
     * @return <br>
     */
    public static Integer[] splitId(final String idStr) {
        return splitId(idStr, GlobalConstants.SPLITOR);
    }

    /**
     * Description: 多个通配符匹配<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param rules rule 规则 带正则表达式的
     * @param matchValue
     * @return <br>
     */
    public static boolean wildcardMatch(final Collection<String> rules, final String matchValue) {
        for (String rule : rules) {
            if (wildcardMatch(rule, matchValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通配符匹配
     *
     * @param rule 规则 带正则表达式的
     * @param matchValue 要匹配的值
     * @return 匹配结果
     */
    public static boolean wildcardMatch(final String rule, final String matchValue) {
        String tempRule = org.apache.commons.lang3.StringUtils
            .replaceEach(org.apache.commons.lang3.StringUtils.replaceEach(rule, new String[] {
                "\\", ".", "^", "$"
            }, new String[] {
                "\\\\", "\\.", "\\^", "\\$"
            }), new String[] {
                "*", "?"
            }, new String[] {
                ".*", ".?"
            });
        Pattern pp = Pattern.compile(tempRule);
        Matcher m = pp.matcher(matchValue);
        return m.matches();
    }

    /**
     * Description: 匹配配置项中是否含有matchValue<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param rule
     * @param matchValue
     * @return <br>
     */
    public static boolean match(final String rule, final String matchValue) {
        boolean ismatch = false;
        if (StringUtils.isNotEmpty(rule) && StringUtils.isNotEmpty(matchValue)) {
            // match all
            if (GlobalConstants.ASTERISK.equals(rule)) {
                ismatch = true;
            }
            // not eq match
            else if (rule.startsWith("NOT:")) {
                String tempRule = rule.substring("NOT:".length());
                ismatch = tempRule.indexOf(GlobalConstants.SPLITOR) == -1 ? !tempRule.equals(matchValue)
                    : new StringBuilder().append(GlobalConstants.SPLITOR).append(tempRule)
                        .append(GlobalConstants.SPLITOR).indexOf(new StringBuilder().append(GlobalConstants.SPLITOR)
                            .append(matchValue).append(GlobalConstants.SPLITOR).toString()) == -1;
            }
            // eq match
            else {
                ismatch = rule.indexOf(GlobalConstants.SPLITOR) == -1 ? rule.equals(matchValue)
                    : new StringBuilder().append(GlobalConstants.SPLITOR).append(rule).append(GlobalConstants.SPLITOR)
                        .indexOf(new StringBuilder().append(GlobalConstants.SPLITOR).append(matchValue)
                            .append(GlobalConstants.SPLITOR).toString()) != -1;
            }
        }
        return ismatch;
    }

    /**
     * Description: 除去字符串中的所有符号<br>
     * 
     * @author 何佳文<br>
     * @taskId <br>
     * @param str
     * @return <br>
     */
    public static String removeAllSymbol(final String str) {
        if (StringUtils.isNotEmpty(str)) {
            return str.replaceAll("[\\pP\\p{Punct}]", "");
        }
        return "";
    }

    /**
     * Description: 去除所有的空格和制表符、换行符<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @return <br>
     */
    public static String replaceAllBlank(final String str) {
        String dest = GlobalConstants.BLANK;
        if (StringUtils.isNotEmpty(str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(GlobalConstants.BLANK);
        }
        return dest;
    }

    /**
     * Description: 替换多余的制表符、换行符，只保留一个<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param str
     * @return <br>
     */
    public static String replaceRedundantBlank(final String str) {
        String dest = GlobalConstants.BLANK;
        if (StringUtils.isNotEmpty(str)) {
            Pattern p = Pattern.compile("\\s{1,}|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(" ");
        }
        return StringUtils.trim(dest);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param paramTypes 数组值
     * @param <T> 数据类型
     * @return <br>
     */
    public static <T> boolean isNotEmpty(final T[] paramTypes) {
        return !ArrayUtils.isEmpty(paramTypes);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param paramTypes 数组值
     * @param <T> 数据类型
     * @return <br>
     */
    public static <T> boolean isEmpty(final T[] paramTypes) {
        return ArrayUtils.isEmpty(paramTypes);
    }
}
