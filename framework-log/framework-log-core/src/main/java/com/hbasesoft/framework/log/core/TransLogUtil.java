/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ServiceLoader;

import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.PropertyHolder;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;

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

    /** tracer */
    private static Tracer tracer;

    /**
     * transLoggerServices
     */
    private static Iterable<TransLoggerService> transLoggerServices;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String getTracerId() {
        Tracer tc = getTracer();
        if (tc != null) {
            Span span = tracer.currentSpan();
            if (span != null) {
                return span.context().traceIdString();
            }
        }
        return "No Tracer found!";
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
    public static SpanInScope before(final long beginTime, final Method method, final Object[] args) {
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
    public static SpanInScope before(final long beginTime, final String methodName, final Object[] args) {
        Tracer tc = getTracer();
        if (tc != null) {

            // 父Span
            Span parentSpan = tc.currentSpan();

            // 当前的Span
            Span span = null;
            if (parentSpan != null) {
                span = tracer.newChild(parentSpan.context()).name(methodName);
            }
            else {
                span = tracer.newTrace().name(methodName);
            }

            span.start(beginTime);

            SpanInScope scope = tc.withSpanInScope(span);

            // 执行记录
            for (TransLoggerService service : getTransLoggerServices()) {
                service.before(span, parentSpan, beginTime, methodName, args);
            }

            return scope;
        }
        return null;
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
        Tracer tc = getTracer();
        if (tc != null) {
            // 当前的Span
            Span span = tc.currentSpan();
            if (span != null) {
                long endTime = System.currentTimeMillis();
                try {
                    // 执行记录
                    for (TransLoggerService service : getTransLoggerServices()) {
                        service.afterReturn(span, endTime, endTime - beginTime, methodName, returnValue);
                    }
                }
                finally {
                    span.finish(endTime);
                }
            }
        }
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
        Tracer tc = getTracer();
        if (tc != null) {
            // 当前的Span
            Span span = tc.currentSpan();
            if (span != null) {
                long endTime = System.currentTimeMillis();
                try {
                    // 执行记录
                    for (TransLoggerService service : getTransLoggerServices()) {
                        service.afterThrow(span, endTime, endTime - beginTime, methodName, e);
                    }
                }
                finally {
                    span.finish(endTime);
                }
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
        sbuf.append(method.getDeclaringClass().getSimpleName()).append('<').append(method.getName()).append('>');
        sbuf.append('(');

        Class<?>[] types = method.getParameterTypes();
        if (CommonUtil.isNotEmpty(types)) {
            for (int i = 0; i < types.length; i++) {
                if (i > 0) {
                    sbuf.append(',');
                }
                sbuf.append(types[i].getSimpleName());
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

    private static Tracer getTracer() {
        if (tracer == null) {
            ApplicationContext context = ContextHolder.getContext();
            if (context != null) {
                tracer = context.getBean(Tracer.class);
            }
        }
        return tracer;
    }
}
