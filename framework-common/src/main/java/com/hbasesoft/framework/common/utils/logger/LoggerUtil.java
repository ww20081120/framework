/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbasesoft.framework.common.utils.CommonUtil;

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
public final class LoggerUtil {

    /**
     * sql语句日志名称，用于记录日志
     */
    private static final String SQL_LOG_NAME = "framework.sql.log";

    private LoggerUtil() {
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void error(Throwable t) {
        getLogger().error(t.getMessage(), t);
    }

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message <br>
     */
    public static void error(Object message) {
        getLogger().error(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void error(Object message, Throwable t) {
        getLogger().error(message, t);
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public static void error(Throwable t, String message, Object... params) {
        getLogger().error(CommonUtil.messageFormat(message, params));
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void warn(Throwable t) {
        getLogger().warn(t.getMessage(), t);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void warn(Object message) {
        getLogger().warn(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void warn(Object message, Throwable t) {
        getLogger().warn(message, t);
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public static void warn(Throwable t, String message, Object... params) {
        getLogger().warn(CommonUtil.messageFormat(message, params), t);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void debug(Object message) {
        getLogger().debug(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void debug(Object message, Throwable t) {
        getLogger().debug(message, t);
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
    public static void info(Object message) {
        getLogger().info(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void info(Object message, Throwable t) {
        getLogger().info(message, t);
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
        LogManager.getLogger(SQL_LOG_NAME).info(message);
    }

    /**
     * sql日志
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     */
    public static void sqlLog(String message, Throwable t) {
        LogManager.getLogger(SQL_LOG_NAME).error(message, t);
    }

    private static Logger getLogger() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        return LogManager.getLogger(stackTraces[3].getClassName());
    }
}
