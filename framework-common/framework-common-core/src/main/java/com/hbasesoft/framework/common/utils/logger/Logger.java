/**
 * 
 */
package com.hbasesoft.framework.common.utils.logger;

import org.slf4j.LoggerFactory;

import com.hbasesoft.framework.common.utils.CommonUtil;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.hbasesoft.framework.common.utils.logger <br>
 */
public class Logger {

    /**
     * logger
     */
    private org.slf4j.Logger logger;

    /**
     * @param clazz
     */
    public Logger(final Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Logger
     * 
     * @param name <br>
     */
    public Logger(final String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param t <br>
     */
    public void error(final Throwable t) {
        logger.error(LoggerUtil.getErrorMessage(t), t);
    }

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message <br>
     */
    public void error(final String message) {
        logger.error(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public void error(final String message, final Throwable t) {
        logger.error(LoggerUtil.getErrorMessage(message, t), t);
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public void error(final Throwable t, final String message, final Object... params) {
        logger.error(LoggerUtil.getErrorMessage(CommonUtil.messageFormat(message, params), t), t);
    }

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param t <br>
     */
    public void warn(final Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn(LoggerUtil.getErrorMessage(t), t);
        }
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void warn(final String message) {
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
    public void warn(final String message, final Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn(LoggerUtil.getErrorMessage(message, t), t);
        }
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public void warn(final Throwable t, final String message, final Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(LoggerUtil.getErrorMessage(CommonUtil.messageFormat(message, params), t), t);
        }
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void debug(final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * 记录日志信息
     * 
     * @param message 日志信息
     * @param params 日志信息
     */
    public void debug(final String message, final Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(CommonUtil.messageFormat(message, params));
        }
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void info(final String message) {
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
    public void info(final String message, final Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info(CommonUtil.messageFormat(message, params));
        }
    }
}
