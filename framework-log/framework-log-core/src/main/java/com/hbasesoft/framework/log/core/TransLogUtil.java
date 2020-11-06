/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.core;

import java.lang.reflect.Method;
import java.util.ServiceLoader;

import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.annotation.NoTransLog;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.TransLoggerService;
import com.hbasesoft.framework.common.utils.logger.TransManager;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月29日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.core <br>
 */
public final class TransLogUtil {

    /** Number */
    private static final long NUM_10L = 10L;

    /** Number */
    private static final int NUM_100 = 100;

    /** framework 的日志是否打印 */
    private static final boolean DEBUG_OPEN_FLAG = PropertyHolder.getBooleanProperty("logservice.framework.show",
        false);

    /** 框架日志的方法 */
    private static final String FRAMEWORK_PACKAGE = "com.hbasesoft.framework.";

    private TransLogUtil() {
    }

    /**
     * transLoggerServices
     */
    private static ServiceLoader<TransLoggerService> transLoggerServices;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param methodName
     * @param args <br>
     */
    public static void before(final String methodName, final Object[] args) {

        // 开始执行时间
        long beginTime = System.currentTimeMillis();

        TransManager manager = TransManager.getInstance();

//        int maxDeepLen = PropertyHolder.getIntProperty("logservice.max.deep.size", NUM_100);

//        // 深度检测
//        if (manager.getStackSize() > maxDeepLen) {
//            throw new FrameworkException(ErrorCodeDef.STACK_OVERFLOW_ERROR_10030);
//        }

        // 父id
        String parentStackId = manager.peek();

        // id
        String stackId = CommonUtil.getTransactionID();
        manager.push(stackId, beginTime);

        // 执行记录
        for (TransLoggerService service : getTransLoggerServices()) {
            service.before(stackId, parentStackId, beginTime, methodName, args);
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param target
     * @param method
     * @param args
     * @throws FrameworkException <br>
     */
    public static void before(final Object target, final Method method, final Object[] args) throws FrameworkException {

        NoTransLog noTransLog = target.getClass().getAnnotation(NoTransLog.class);
        if (noTransLog == null) {
            // 执行方法
            String methodName = getMethodSignature(method);
            if (DEBUG_OPEN_FLAG || !StringUtils.startsWith(methodName, FRAMEWORK_PACKAGE)) {
                before(methodName, args);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param methodName
     * @param returnValue <br>
     */
    public static void afterReturning(final String methodName, final Object returnValue) {
        // 执行完成时间
        long endTime = System.currentTimeMillis();

        TransManager manager = TransManager.getInstance();
        String stackId = manager.pop();
        if (StringUtils.isEmpty(stackId)) {
            return;
        }

        long beginTime = manager.getBeginTime(stackId);
        long consumeTime = endTime - beginTime;

        long maxExcuteTime = PropertyHolder.getLongProperty("logservice.max.execute.time", NUM_10L) * 1000;

        if (consumeTime > maxExcuteTime) {
            manager.setTimeout(true);
        }

        // 执行记录
        for (TransLoggerService service : getTransLoggerServices()) {
            service.afterReturn(stackId, endTime, consumeTime, methodName, returnValue);
        }

        if (manager.getStackSize() <= 0) {
            for (TransLoggerService service : getTransLoggerServices()) {
                service.end(stackId, beginTime, endTime, consumeTime, methodName, returnValue, null);
            }

            for (TransLoggerService service : getTransLoggerServices()) {
                service.clean();
            }

            manager.clean();
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param target
     * @param method
     * @param returnValue <br>
     */
    public static void afterReturning(final Object target, final Method method, final Object returnValue) {
        NoTransLog noTransLog = target.getClass().getAnnotation(NoTransLog.class);
        if (noTransLog == null) {
            // 执行方法
            String methodName = getMethodSignature(method);
            if (DEBUG_OPEN_FLAG || !StringUtils.startsWith(methodName, FRAMEWORK_PACKAGE)) {
                afterReturning(methodName, returnValue);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param methodName
     * @param e <br>
     */
    public static void afterThrowing(final String methodName, final Throwable e) {
        // 执行完成时间
        long endTime = System.currentTimeMillis();

        TransManager manager = TransManager.getInstance();
        String stackId = manager.pop();
        if (StringUtils.isEmpty(stackId)) {
            return;
        }

        long beginTime = manager.getBeginTime(stackId);
        long consumeTime = endTime - beginTime;

        manager.setError(true);

        // 执行记录
        for (TransLoggerService service : getTransLoggerServices()) {
            service.afterThrow(stackId, endTime, consumeTime, methodName, e);
        }

        if (manager.getStackSize() <= 0) {
            for (TransLoggerService service : getTransLoggerServices()) {
                service.end(stackId, beginTime, endTime, consumeTime, methodName, null, e);
            }

            for (TransLoggerService service : getTransLoggerServices()) {
                service.clean();
            }

            manager.clean();
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param target
     * @param method
     * @param e <br>
     */
    public static void afterThrowing(final Object target, final Method method, final Throwable e) {
        NoTransLog noTransLog = target.getClass().getAnnotation(NoTransLog.class);
        if (noTransLog == null) {

            // 执行方法
            String methodName = getMethodSignature(method);
            if (DEBUG_OPEN_FLAG || !StringUtils.startsWith(methodName, FRAMEWORK_PACKAGE)) {
                afterThrowing(methodName, e);
            }
        }
    }

    /**
     * 获取 方法描述
     * 
     * @param method <br>
     * @return <br>
     */
    private static String getMethodSignature(final Method method) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(method.getDeclaringClass().getName()).append('<').append(method.getName()).append('>');
        sbuf.append('(');

        Class<?>[] types = method.getParameterTypes();
        if (CommonUtil.isNotEmpty(types)) {
            for (int i = 0; i < types.length; i++) {
                if (i > 0) {
                    sbuf.append(',');
                }
                sbuf.append(types[i].getName());
            }
        }
        sbuf.append(')');
        return sbuf.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    private static ServiceLoader<TransLoggerService> getTransLoggerServices() {
        if (transLoggerServices == null) {
            boolean alwaysLog = PropertyHolder.getBooleanProperty("logservice.aways.log", true);
            transLoggerServices = ServiceLoader.load(TransLoggerService.class);
            for (TransLoggerService transLoggerService : transLoggerServices) {
                transLoggerService.setAlwaysLog(alwaysLog);
            }
        }
        return transLoggerServices;
    }

}
