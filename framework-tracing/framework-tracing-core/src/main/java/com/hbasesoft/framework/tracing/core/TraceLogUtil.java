/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.core;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.util.ServiceLoader;

import org.apache.commons.lang3.ArrayUtils;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月29日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.core <br>
 */
public final class TraceLogUtil {

    /**
     * 
     */
    private static TraceLogUtil instance = new TraceLogUtil();

    /** tracerAgent */
    private final TracerAgent tracerAgent;

    /**
     * 
     */
    private TraceLogUtil() {
        ServiceLoader<TracerAgent> tracerAgents = ServiceLoader.load(TracerAgent.class);
        if (tracerAgents.iterator().hasNext()) {
            tracerAgent = tracerAgents.iterator().next();
        }
        else {
            tracerAgent = new MicrometerTracerAgent();
        }
    }

    /**
     * Description: 获取traceId <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String getTraceId() {
        return instance.tracerAgent.getTraceId();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime
     * @param method
     * @param args
     * @return <br>
     */
    public static Closeable before(final long beginTime, final Method method, final Object[] args) {
        return before(beginTime, getMethodSignature(method), args);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime
     * @param methodName
     * @param args
     * @return <br>
     */
    public static Closeable before(final long beginTime, final String methodName, final Object[] args) {
        return instance.tracerAgent.before(beginTime, methodName, args);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime
     * @param method
     * @param returnValue <br>
     */
    public static void afterReturning(final long beginTime, final Method method, final Object returnValue) {
        afterReturning(beginTime, getMethodSignature(method), returnValue);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime
     * @param methodName
     * @param returnValue <br>
     */
    public static void afterReturning(final long beginTime, final String methodName, final Object returnValue) {
        instance.tracerAgent.afterReturning(beginTime, methodName, returnValue);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime
     * @param method
     * @param e <br>
     */
    public static void afterThrowing(final long beginTime, final Method method, final Throwable e) {
        afterThrowing(beginTime, getMethodSignature(method), e);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime
     * @param methodName
     * @param e <br>
     */
    public static void afterThrowing(final long beginTime, final String methodName, final Throwable e) {
        instance.tracerAgent.afterThrowing(beginTime, methodName, e);
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
        if (ArrayUtils.isNotEmpty(types)) {
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

}
