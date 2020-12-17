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

    /** stack层数 */
    private static final int STACKTRACES = 3;

    /**
     * sql语句日志名称，用于记录日志
     */
    private static final String SQL_LOG_NAME = "framework.sql.log";

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param t <br>
     */
    public static void error(final Throwable t) {
        getLogger().error(getErrorMessage(t), t);
    }

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message <br>
     */
    public static void error(final String message) {
        getLogger().error(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void error(final String message, final Throwable t) {
        getLogger().error(getErrorMessage(message, t), t);
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public static void error(final Throwable t, final String message, final Object... params) {
        getLogger().error(getErrorMessage(CommonUtil.messageFormat(message, params), t), t);
    }

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param t <br>
     */
    public static void warn(final Throwable t) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(getErrorMessage(t), t);
        }
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void warn(final String message) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void warn(final String message, final Throwable t) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(getErrorMessage(message, t), t);
        }
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public static void warn(final Throwable t, final String message, final Object... params) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(getErrorMessage(CommonUtil.messageFormat(message, params), t), t);
        }
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void debug(final String message) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public static void debug(final String message, final Throwable t) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(getErrorMessage(message, t), t);
        }
    }

    /**
     * 记录日志信息
     * 
     * @param message 日志信息
     * @param params 日志信息
     */
    public static void debug(final String message, final Object... params) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(CommonUtil.messageFormat(message, params));
        }
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public static void info(final String message) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    /**
     * 记录日志信息
     * 
     * @param message 日志信息
     * @param params 日志信息
     */
    public static void info(final String message, final Object... params) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(CommonUtil.messageFormat(message, params));
        }
    }

    /**
     * sql日志
     * 
     * @param message 日志信息
     */
    public static void sqlLog(final String message) {
        LoggerFactory.getLogger(SQL_LOG_NAME).info(TransManager.getInstance().peek() + "|" + message);
    }

    /**
     * sql日志
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     */
    public static void sqlLog(final String message, final Throwable t) {
        LoggerFactory.getLogger(SQL_LOG_NAME)
            .error(TransManager.getInstance().peek() + "|" + getErrorMessage(message, t), t);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param t
     * @return <br>
     */
    public static String getErrorMessage(final Throwable t) {
        if (t instanceof FrameworkException) {
            return ((FrameworkException) t).getCode() + GlobalConstants.VERTICAL_LINE + t.getMessage();
        }
        return t.getMessage();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message
     * @param t
     * @return <br>
     */
    public static String getErrorMessage(final String message, final Throwable t) {
        if (t instanceof FrameworkException) {
            return ((FrameworkException) t).getCode() + GlobalConstants.VERTICAL_LINE + message;
        }
        return message;
    }

    private static org.slf4j.Logger getLogger() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        return LoggerFactory.getLogger(stackTraces[STACKTRACES].getClassName());
    }
}
