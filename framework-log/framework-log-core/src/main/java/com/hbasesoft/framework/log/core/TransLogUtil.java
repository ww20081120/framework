/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.core;

import java.util.ArrayList;
import java.util.ServiceLoader;

import com.hbasesoft.framework.common.utils.PropertyHolder;

import brave.Span;

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

    private TransLogUtil() {
    }

    /**
     * transLoggerServices
     */
    private static Iterable<TransLoggerService> transLoggerServices;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param span
     * @param parentSpan
     * @param beginTime
     * @param methodName
     * @param args <br>
     */
    public static void before(final Span span, final Span parentSpan, final long beginTime, final String methodName,
        final Object[] args) {
        // 执行记录
        for (TransLoggerService service : getTransLoggerServices()) {
            service.before(span, parentSpan, beginTime, methodName, args);
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param span
     * @param endTime
     * @param consumeTime
     * @param methodName
     * @param returnValue <br>
     */
    public static void afterReturning(final Span span, final long endTime, final long consumeTime,
        final String methodName, final Object returnValue) {

        // 执行记录
        for (TransLoggerService service : getTransLoggerServices()) {
            service.afterReturn(span, endTime, consumeTime, methodName, returnValue);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param span
     * @param endTime
     * @param consumeTime
     * @param methodName
     * @param e <br>
     */
    public static void afterThrowing(final Span span, final long endTime, final long consumeTime,
        final String methodName, final Throwable e) {
        // 执行记录
        for (TransLoggerService service : getTransLoggerServices()) {
            service.afterThrow(span, endTime, consumeTime, methodName, e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    private static Iterable<TransLoggerService> getTransLoggerServices() {
        if (transLoggerServices == null) {
            if (PropertyHolder.getBooleanProperty("logservice.aways.log", true)) {
                transLoggerServices = ServiceLoader.load(TransLoggerService.class);
            }
            else {
                transLoggerServices = new ArrayList<>();
            }
        }
        return transLoggerServices;
    }

}
