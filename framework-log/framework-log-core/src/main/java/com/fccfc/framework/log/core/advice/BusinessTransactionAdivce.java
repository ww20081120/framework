/**
 * 
 */
package com.fccfc.framework.log.core.advice;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.log.core.TransManager;
import com.fccfc.framework.log.core.bean.TransLogPojo;
import com.fccfc.framework.log.core.bean.TransLogStackPojo;
import com.fccfc.framework.log.core.service.TransLogService;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月6日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.log.advice <br>
 */

public class BusinessTransactionAdivce {

    private static Logger logger = new Logger(BusinessTransactionAdivce.class);

    @Resource
    private TransLogService transLogService;

    /**
     * 栈最大深度
     */
    private int maxDeepLen;

    /** 最大执行时间 */
    private long maxExcuteTime;

    /**
     * 是否保存正常的流程日志
     */
    private boolean saveLog;

    /**
     * 方法执行前拦截
     * 
     * @param point 参数
     * @throws FrameworkException
     */
    public void before(JoinPoint point) throws FrameworkException {
        Date currentDate = new Date();
        String method = getMethodSignature(point);
        StringBuilder paramsSb = new StringBuilder();
        Object[] args = point.getArgs();
        for (Object obj : args) {
            paramsSb.append(obj).append(',');
        }
        String inputParam = paramsSb.toString();
        try {
            TransManager manager = TransManager.getInstance();
            if (manager.getStackSize() > maxDeepLen) {
                throw new FrameworkException(ErrorCodeDef.STACK_OVERFLOW_ERROR_10030, "业务过于复杂，请简化业务");
            }

            // 设置transLog
            TransLogPojo transLog = manager.getTransLog();
            if (transLog == null) {
                transLog = new TransLogPojo();
                transLog.setBeginTime(currentDate);
                transLog.setModuleCode(Configuration.getLocalModuleCode());
                transLog.setTransId(CommonUtil.getTransactionID());
                manager.setTransLog(transLog);
            }

            // 设置transLogStack
            TransLogStackPojo logStack = new TransLogStackPojo();
            logStack.setStackId(CommonUtil.getTransactionID());
            logStack.setBeginTime(currentDate);
            CacheHelper.getStringCache().putValue(CacheConstant.CACHE_LOGS, logStack.getStackId() + "_inputparam",
                inputParam);
            logStack.setMethod(method);

            TransLogStackPojo parentTransLogStatck = manager.getLastTransLogStack();
            logStack.setParentStackId(parentTransLogStatck == null ? null : parentTransLogStatck.getStackId());
            logStack.setSeq(manager.getSeq());
            logStack.setTransId(transLog.getTransId());
            manager.addTransLogStack(logStack);
        }
        catch (CacheException e) {
            logger.error(e);
        }
    }

    private String getMethodSignature(JoinPoint point) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(point.getTarget().getClass().getName()).append('<').append(point.getSignature().getName())
            .append('>');
        sbuf.append('(');
        Object[] args = point.getArgs();

        if (CommonUtil.isNotEmpty(args)) {
            for (Object obj : args) {
                sbuf.append(obj == null ? "NULL" : obj.getClass().getName()).append(',');
            }
        }
        sbuf.append(')');
        return sbuf.toString();
    }

    /**
     * 方法正常执行结束拦截
     * 
     * @param point 参数
     * @param returnValue 方法返回值
     * @throws ServiceException
     */
    public void afterReturning(JoinPoint point, Object returnValue) {
        Date currentDate = new Date();

        TransManager manager = TransManager.getInstance();
        TransLogStackPojo logStack = manager.removeLast();
        if (CommonUtil.isNull(logStack)) {
            return;
        }
        logStack.setEndTime(currentDate);
        logStack.setConsumeTime(new Long(logStack.getEndTime().getTime() - logStack.getBeginTime().getTime())
            .intValue());
        logStack.setIsSuccess("Y");

        if (logStack.getConsumeTime() >= maxExcuteTime) {
            manager.setError(true);
        }

        try {
            if (returnValue != null) {
                CacheHelper.getStringCache().putValue(CacheConstant.CACHE_LOGS, logStack.getStackId() + "_outputparam",
                    returnValue.toString());
            }
            manager.getLogList().add(logStack);

            // 设置transLog
            TransLogPojo transLog = manager.getTransLog();

            if (manager.getStackSize() <= 0) {
                if (saveLog || manager.isError()) {
                    transLog.setEndTime(currentDate);
                    transLog.setConsumeTime(new Long(transLog.getEndTime().getTime()
                        - transLog.getBeginTime().getTime()).intValue());
                    transLog.setInputParam(CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS,
                        logStack.getStackId() + "_inputparam"));
                    transLog.setOutputParam(CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS,
                        logStack.getStackId() + "_outputparam"));
                    transLog.setSqlLog(manager.getSqlLog());

                    transLogService.addTransactionLog(transLog);
                    saveLogList(manager.getLogList());
                }
                cleanCache(manager.getLogList());
            }
        }
        catch (FrameworkException e) {
            logger.error(e);
        }

    }

    /**
     * 方法异常执行结束拦截
     * 
     * @param point 参数
     * @param ex 异常信息
     * @throws ServiceException
     */
    public void afterThrowing(JoinPoint point, Exception ex) {
        Date currentDate = new Date();
        TransManager manager = TransManager.getInstance();

        TransLogStackPojo logStack = manager.removeLast();
        if (CommonUtil.isNull(logStack)) {
            return;
        }

        logStack.setEndTime(currentDate);
        logStack.setConsumeTime(new Long(logStack.getEndTime().getTime() - logStack.getBeginTime().getTime())
            .intValue());
        logStack.setIsSuccess("N");
        manager.setError(true);
        manager.getLogList().add(logStack);
        try {
            // 设置transLog
            TransLogPojo transLog = manager.getTransLog();
            if (manager.getStackSize() <= 0) {
                String inpuKey = logStack.getStackId() + "_inputparam";
                transLog.setEndTime(currentDate);
                transLog.setConsumeTime(new Long(transLog.getEndTime().getTime() - transLog.getBeginTime().getTime())
                    .intValue());
                transLog.setInputParam(CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS, inpuKey));
                transLog.setExceptionLog(getExceptionMsg(ex));
                transLog.setSqlLog(manager.getSqlLog());

                transLogService.addTransactionLog(transLog);
                saveLogList(manager.getLogList());
                cleanCache(manager.getLogList());
            }
        }
        catch (FrameworkException e) {
            logger.error(e);
        }
    }

    /**
     * 获取异常栈信息
     * 
     * @param ex 异常
     * @return 结果
     */
    private String getExceptionMsg(Exception ex) {
        StringBuilder exceptionMsg = new StringBuilder();
        StackTraceElement[] messages = ex.getStackTrace();
        StackTraceElement element = null;

        for (int i = 0; i < messages.length; i++) {
            element = messages[i];
            exceptionMsg.append("File: ").append(element.getFileName()).append("Class: ")
                .append(element.getClassName()).append("Method: ").append(element.getMethodName()).append("Line: ")
                .append(element.getLineNumber()).append("<br/>");
        }

        return exceptionMsg.toString();
    }

    /**
     * 保存流程日志
     * 
     * @param logList 日志集合
     * @throws ServiceException 异常
     * @throws CacheException 异常
     */
    private void saveLogList(List<TransLogStackPojo> logList) throws ServiceException, CacheException {
        for (TransLogStackPojo pojo : logList) {
            pojo.setInputParam(CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS,
                pojo.getStackId() + "_inputparam"));
            pojo.setOutputParam(CacheHelper.getStringCache().getValue(CacheConstant.CACHE_LOGS,
                pojo.getStackId() + "_outputparam"));
            transLogService.addTransactionStackLog(pojo);
        }
    }

    /**
     * 清除缓存数据
     * 
     * @param logList 日志集合
     * @throws CacheException 异常
     */
    private void cleanCache(List<TransLogStackPojo> logList) throws CacheException {
        for (TransLogStackPojo pojo : logList) {
            CacheHelper.getStringCache().removeValue(CacheConstant.CACHE_LOGS, pojo.getStackId() + "_inputparam");
            CacheHelper.getStringCache().removeValue(CacheConstant.CACHE_LOGS, pojo.getStackId() + "_outputparam");
        }
        TransManager.getInstance().getLogList().clear();
        TransManager.getInstance().setError(false);
        TransManager.getInstance().setSeq(0);
        TransManager.getInstance().removeSqlLog();
        TransManager.getInstance().setTransLog(null);
    }

    public void setMaxDeepLen(int maxDeepLen) {
        this.maxDeepLen = maxDeepLen;
    }

    public void setSaveLog(boolean saveLog) {
        this.saveLog = saveLog;
    }

    public void setMaxExcuteTime(long maxExcuteTime) {
        this.maxExcuteTime = maxExcuteTime;
    }
}
