/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.logger;

import org.slf4j.LoggerFactory;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年12月16日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.logger <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoggerUtil {

    /**
     * sql语句日志名称，用于记录日志
     */
    private static final String SQL_LOG_NAME = "framework.sql.log";

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void error(Throwable t) {
        getLogger().error(getErrorMessage(t), t);
    }

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message <br>
     */
    public static void error(String message) {
        getLogger().error(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void error(String message, Throwable t) {
        getLogger().error(getErrorMessage(message, t), t);
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public static void error(Throwable t, String message, Object... params) {
        getLogger().error(getErrorMessage(CommonUtil.messageFormat(message, params), t), t);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void warn(Throwable t) {
        getLogger().warn(getErrorMessage(t), t);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void warn(String message) {
        getLogger().warn(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void warn(String message, Throwable t) {
        getLogger().warn(getErrorMessage(message, t), t);
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public static void warn(Throwable t, String message, Object... params) {
        getLogger().warn(getErrorMessage(CommonUtil.messageFormat(message, params), t), t);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void debug(String message) {
        getLogger().debug(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void debug(String message, Throwable t) {
        getLogger().debug(getErrorMessage(message, t), t);
    }

    /**
     * 记录日志信息
     * 
     * @param message 日志信息
     * @param params 日志信息
     */
    public static void debug(String message, Object... params) {
        getLogger().debug(CommonUtil.messageFormat(message, params));
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void info(String message) {
        getLogger().info(message);
    }

    /**
     * 记录日志信息
     * 
     * @param message 日志信息
     * @param params 日志信息
     */
    public static void info(String message, Object... params) {
        getLogger().info(CommonUtil.messageFormat(message, params));
    }

    /**
     * sql日志
     * 
     * @param message 日志信息
     */
    public static void sqlLog(String message) {
        LoggerFactory.getLogger(SQL_LOG_NAME).info(message);
    }

    /**
     * sql日志
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     */
    public static void sqlLog(String message, Throwable t) {
        LoggerFactory.getLogger(SQL_LOG_NAME).error(getErrorMessage(message, t), t);
    }

    public static String getErrorMessage(Throwable t) {
        if (t instanceof FrameworkException) {
            return ((FrameworkException) t).getCode() + GlobalConstants.VERTICAL_LINE + t.getMessage();
        }
        return t.getMessage();
    }

    public static String getErrorMessage(String message, Throwable t) {
        if (t instanceof FrameworkException) {
            return ((FrameworkException) t).getCode() + GlobalConstants.VERTICAL_LINE + message;
        }
        return message;
    }

    private static org.slf4j.Logger getLogger() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        return LoggerFactory.getLogger(stackTraces[3].getClassName());
    }
}
