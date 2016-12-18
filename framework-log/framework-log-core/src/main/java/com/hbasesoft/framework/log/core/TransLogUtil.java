/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.core;

import java.lang.reflect.Method;
import java.util.ServiceLoader;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.TransactionIDManager;
import com.hbasesoft.framework.common.utils.logger.TransLoggerService;
import com.hbasesoft.framework.log.core.annotation.NoTransLog;

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
public class TransLogUtil {

    private TransLogUtil() {
    }

    /**
     * transLoggerServices
     */
    private static ServiceLoader<TransLoggerService> transLoggerServices;

    public static void before(Object target, Method method, Object[] args) throws FrameworkException {

        NoTransLog noTransLog = target.getClass().getAnnotation(NoTransLog.class);
        if (noTransLog == null) {
            // 开始执行时间
            long beginTime = System.currentTimeMillis();

            // 执行方法
            String methodName = getMethodSignature(method);

            TransManager manager = TransManager.getInstance();

            int maxDeepLen = PropertyHolder.getIntProperty("logservice.max.deep.size", 5);

            // 深度检测
            if (manager.getStackSize() > maxDeepLen) {
                throw new FrameworkException(ErrorCodeDef.STACK_OVERFLOW_ERROR_10030, "业务过于复杂，请简化业务");
            }

            // 父id
            String parentStackId = manager.peek();

            if (CommonUtil.isEmpty(parentStackId)) {
                parentStackId = TransactionIDManager.getTransactionId();
            }

            // id
            String stackId = CommonUtil.getTransactionID();
            manager.push(stackId, beginTime);

            // 执行记录
            for (TransLoggerService service : getTransLoggerServices()) {
                service.before(stackId, parentStackId, beginTime, methodName, args);
            }
        }
    }

    public static void afterReturning(Object target, Method method, Object returnValue) {
        NoTransLog noTransLog = target.getClass().getAnnotation(NoTransLog.class);
        if (noTransLog == null) {
            // 执行完成时间
            long endTime = System.currentTimeMillis();

            TransManager manager = TransManager.getInstance();
            String stackId = manager.pop();
            if (CommonUtil.isEmpty(stackId)) {
                return;
            }

            long beginTime = manager.getBeginTime(stackId);
            long consumeTime = endTime - beginTime;

            long maxExcuteTime = PropertyHolder.getLongProperty("logservice.max.execute.time", 10L) * 1000;

            if (consumeTime > maxExcuteTime) {
                manager.setTimeout(true);
            }

            // 执行方法
            String methodName = getMethodSignature(method);

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
    }

    public static void afterThrowing(Object target, Method method, Throwable e) {
        NoTransLog noTransLog = target.getClass().getAnnotation(NoTransLog.class);
        if (noTransLog == null) {
            // 执行完成时间
            long endTime = System.currentTimeMillis();

            TransManager manager = TransManager.getInstance();
            String stackId = manager.pop();
            if (CommonUtil.isEmpty(stackId)) {
                return;
            }

            long beginTime = manager.getBeginTime(stackId);
            long consumeTime = endTime - beginTime;

            manager.setError(true);

            // 执行方法
            String methodName = getMethodSignature(method);

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
    }

    /**
     * 获取 方法描述
     * 
     * @param point <br>
     * @return <br>
     */
    private static String getMethodSignature(Method method) {
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
            boolean alwaysLog = PropertyHolder.getBooleanProperty("logservice.aways.log", false);
            transLoggerServices = ServiceLoader.load(TransLoggerService.class);
            for (TransLoggerService transLoggerService : transLoggerServices) {
                transLoggerService.setAlwaysLog(alwaysLog);
            }
        }
        return transLoggerServices;
    }

}
