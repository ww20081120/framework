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
     * Logger
     * 
     * @param clazz <br>
     */
    public Logger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Logger
     * 
     * @param name <br>
     */
    public Logger(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void error(Throwable t) {
        logger.error(LoggerUtil.getErrorMessage(t), t);
    }

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message <br>
     */
    public void error(String message) {
        logger.error(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public void error(String message, Throwable t) {
        logger.error(LoggerUtil.getErrorMessage(message, t), t);
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public void error(Throwable t, String message, Object... params) {
        logger.error(LoggerUtil.getErrorMessage(CommonUtil.messageFormat(message, params), t), t);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void warn(Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn(LoggerUtil.getErrorMessage(t), t);
        }
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void warn(String message) {
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
    public void warn(String message, Throwable t) {
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
    public void warn(Throwable t, String message, Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(LoggerUtil.getErrorMessage(CommonUtil.messageFormat(message, params), t), t);
        }
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void debug(String message) {
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
    public void debug(String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(CommonUtil.messageFormat(message, params));
        }
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void info(String message) {
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
    public void info(String message, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info(CommonUtil.messageFormat(message, params));
        }
    }
}
