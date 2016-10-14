/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
public final class CommonUtil {

    /**
     * random
     */
    private static final Random RANDOM = new Random();

    /**
     * Description: 字符串是为NULL或为空<br>
     * 
     * @author 王伟 <br>
     * @param str
     *            <br>
     * @return <br>
     */
    public static boolean isEmpty(String str) {
	return StringUtils.isEmpty(str);
    }
    
    /**
     * 
     * Description: 去除首尾空格，并将null字符串转化为null对象<br> 
     *  
     * @author liuxianan<br>
     * @taskId <br>
     * @param str
     * @return <br>
     */
    public static String trim(String str) {
    	str = str == null ? null : str.trim();
    	if ("null".equals(str)) {
    		return null;
    	}
    	return str;
	}
    
    /**
     * Description:字符串不为NULL也不为空 <br>
     * 
     * @author 王伟 <br>
     * @param str
     *            <br>
     * @return <br>
     */
    public static boolean isNotEmpty(String str) {
	return !isEmpty(str);
    }

    /**
     * Description: 判断数组是否为NULL或为空<br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param t
     *            <br>
     * @param <T>
     *            <br>
     * @return <br>
     */
    public static <T> boolean isEmpty(T[] t) {
	return t == null || t.length == 0;
    }

    /**
     * 判断数组不为NULL也不为空
     * 
     * @param t
     *            <br>
     * @param <T>
     *            <br>
     * @return <br>
     */
    public static <T> boolean isNotEmpty(T[] t) {
	return !isEmpty(t);
    }

    /**
     * Description: 集合是否为NULL或为空<br>
     * 
     * @author 王伟 <br>
     * @param col
     *            <br>
     * @return <br>
     */
    public static boolean isEmpty(Collection<?> col) {
	return col == null || col.isEmpty();
    }

    /**
     * Description:集合不为NULL也不为空 <br>
     * 
     * @author 王伟 <br>
     * @param col
     *            <br>
     * @return <br>
     */
    public static boolean isNotEmpty(Collection<?> col) {
	return !isEmpty(col);
    }

    /**
     * Description: map是否为NULL或为空<br>
     * 
     * @author 王伟 <br>
     * @param map
     *            <br>
     * @return <br>
     */
    public static boolean isEmpty(Map<?, ?> map) {
	return map == null || map.isEmpty();
    }

    /**
     * Description:map不为NULL也不为空 <br>
     * 
     * @author 王伟 <br>
     * @param map
     *            <br>
     * @return <br>
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
	return !isEmpty(map);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param str
     *            <br>
     * @return <br>
     */
    public static String lowerCaseFirstChar(String str) {
	if (isNotEmpty(str)) {
	    char firstChar = str.charAt(0);
	    if (Character.isUpperCase(firstChar)) {
		StringBuilder sb = new StringBuilder(str);
		sb.setCharAt(0, Character.toLowerCase(firstChar));
		str = sb.toString();
	    }
	}
	return str;
    }

    /**
     * 消息格式化
     * 
     * @param message
     *            message <br>
     * @param params
     *            params <br>
     * @return String <br>
     */
    public static String messageFormat(String message, Object... params) {
	return isNotEmpty(params) ? MessageFormat.format(message, params) : message;
    }

    /**
     * 判断是否是空对象
     * 
     * @param obj
     *            obj <br>
     * @return boolean <br>
     */
    public static boolean isNull(Object obj) {
	return null == obj;
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
     * Description: 获取指定位数的随机数<br>
     * 
     * @author 王伟 <br>
     * @param size
     *            <br>
     * @return <br>
     */
    public static String getRandomNumber(int size) {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < size; i++) {
	    sb.append((char) ('0' + RANDOM.nextInt(10)));
	}
	return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param size
     *            <br>
     * @return <br>
     */
    public static String getRandomChar(int size) {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < size; i++) {
	    switch (RANDOM.nextInt(10) % 3) {
	    case 0:
		sb.append((char) ('0' + RANDOM.nextInt(10)));
		break;
	    case 1:
		sb.append((char) ('a' + RANDOM.nextInt(26)));
		break;
	    case 2:
		sb.append((char) ('A' + RANDOM.nextInt(26)));
		break;
	    default:
		;
	    }
	}
	return sb.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param obj
     *            <br>
     * @return <br>
     */
    public static String getString(Object obj) {
	String result = null;
	if (obj != null) {
	    result = obj instanceof String ? (String) obj : obj.toString();
	}
	return result;
    }

    /**
     * Description:getDate <br>
     * 
     * @author 王伟 <br>
     * @param time
     *            <br
     * @return <br>
     */
    public static Date getDate(Long time) {
	if (time != null) {
	    return new Date(time);
	}
	return null;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param s
     *            <br>
     * @return <br>
     * @throws UtilException
     *             <br>
     */
    public static final String md5(String s) throws UtilException {
	char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	try {
	    byte[] btInput = s.getBytes();
	    // 获得MD5摘要算法的 MessageDigest 对象
	    MessageDigest mdInst = MessageDigest.getInstance("MD5");
	    // 使用指定的字节更新摘要
	    mdInst.update(btInput);
	    // 获得密文
	    byte[] md = mdInst.digest();
	    // 把密文转换成十六进制的字符串形式
	    int j = md.length;
	    char str[] = new char[j * 2];
	    int k = 0;
	    for (int i = 0; i < j; i++) {
		byte byte0 = md[i];
		str[k++] = hexDigits[byte0 >>> 4 & 0xf];
		str[k++] = hexDigits[byte0 & 0xf];
	    }
	    return new String(str);
	} catch (Exception e) {
	    throw new UtilException(ErrorCodeDef.SYSTEM_ERROR_10001);
	}
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
    public static final Integer[] splitId(String idStr, String splitor) {
	Integer[] ids = null;
	if (CommonUtil.isNotEmpty(idStr)) {
	    String[] strs = StringUtils.split(idStr, splitor);
	    ids = new Integer[strs.length];
	    for (int i = 0; i < strs.length; i++) {
		ids[i] = Integer.valueOf(strs[i]);
	    }
	}
	return ids;
    }

    public static final Long[] splitIdsByLong(String idStr, String splitor) {
	Long[] ids = null;
	if (CommonUtil.isNotEmpty(idStr)) {
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
    public static final Integer[] splitId(String idStr) {
	return splitId(idStr, GlobalConstants.SPLITOR);
    }

    /**
     * Description: 匹配配置项中是否含有matchValue<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     *            <br>
     * @param matchValue
     *            <br>
     * @return <br>
     */
    public static boolean match(String key, String matchValue) {
	boolean ismatch = false;
	String value = getString(key);
	if (CommonUtil.isNotEmpty(value) && CommonUtil.isNotEmpty(matchValue)) {
	    ismatch = new StringBuilder().append(GlobalConstants.SPLITOR).append(value).append(GlobalConstants.SPLITOR)
		    .indexOf(new StringBuilder().append(GlobalConstants.SPLITOR).append(matchValue)
			    .append(GlobalConstants.SPLITOR).toString()) != -1;
	}
	return ismatch;
    }

    /**
     * 
     * Description: 除去字符串中的所有符号<br>
     * 
     * @author 何佳文<br>
     * @taskId <br>
     * @param str
     * @return <br>
     */
    public static String removeAllSymbol(String str) {
	if (isNotEmpty(str)) {
	    return str.replaceAll("[\\pP\\p{Punct}]", "");
	}
	return "";
    }

    /**
     * 
     * Description: 除去字符串中symbol意外的所有符号<br>
     * 
     * @author 何佳文<br>
     * @taskId <br>
     * @param str
     * @param symbol
     * @return <br>
     */
    public static String removeAllSymbol(String str, String symbol) {
	if (isNotEmpty(str)) {
	    String random = getRandomChar(10);
	    return str.replace(symbol, random).replaceAll("[\\pP\\p{Punct}]", "").replace(random, symbol);
	}
	return "";
    }
}
