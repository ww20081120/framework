/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.core;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.PropertyHolder;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Tracer.SpanInScope;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年2月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.core <br>
 */
public class MicrometerTracerAgent implements TracerAgent {

    /** tracer */
    private Tracer tracer;

    /**
     * transLoggerServices
     */
    private Iterable<TraceLoggerService> transLoggerServices;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getTraceId() {
        Tracer tc = getTracer();
        if (tc != null) {
            // 使用CurrentTraceContext来获取当前活跃的Span
            return tc.currentTraceContext().context().traceId();
        }
        return "No Trace in context";
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
    @Override
    public Closeable before(final long beginTime, final String methodName, final Object[] args) {

        Tracer tc = getTracer();
        if (tc != null) {

            // 父Span
            Span parentSpan = tc.currentSpan();

            // 当前的Span
            Span span = null;
            if (parentSpan != null) {
                span = tc.spanBuilder().setParent(parentSpan.context()).name(methodName)
                    .startTimestamp(beginTime, TimeUnit.MILLISECONDS).start();
            }
            else {
                span = tc.spanBuilder().name(methodName).startTimestamp(beginTime, TimeUnit.MILLISECONDS).start();
            }

            SpanInScope scope = tc.withSpan(span);

            // 执行记录
            for (TraceLoggerService service : getTransLoggerServices()) {
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
     * @param methodName
     * @param returnValue <br>
     */
    @Override
    public void afterReturning(final long beginTime, final String methodName, final Object returnValue) {
        Tracer tc = getTracer();
        if (tc != null) {
            // 当前的Span
            Span span = tc.currentSpan();
            if (span != null) {
                long endTime = System.currentTimeMillis();
                try {
                    // 执行记录
                    for (TraceLoggerService service : getTransLoggerServices()) {
                        service.afterReturn(span, endTime, endTime - beginTime, methodName, returnValue);
                    }
                }
                finally {
                    span.end(endTime, TimeUnit.MILLISECONDS);
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
     * @param methodName
     * @param e <br>
     */
    @Override
    public void afterThrowing(final long beginTime, final String methodName, final Throwable e) {
        Tracer tc = getTracer();
        if (tc != null) {
            // 当前的Span
            Span span = tc.currentSpan();
            if (span != null) {
                long endTime = System.currentTimeMillis();
                try {
                    // 执行记录
                    for (TraceLoggerService service : getTransLoggerServices()) {
                        service.afterThrow(span, endTime, endTime - beginTime, methodName, e);
                    }
                }
                finally {
                    span.end(endTime, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    private Iterable<TraceLoggerService> getTransLoggerServices() {
        if (transLoggerServices == null) {
            if (PropertyHolder.getBooleanProperty("logservice.aways.log", true)) {
                transLoggerServices = ServiceLoader.load(TraceLoggerService.class);
            }
            else {
                transLoggerServices = new ArrayList<>();
            }
        }
        return transLoggerServices;
    }

    private Tracer getTracer() {
        if (tracer == null) {
            ApplicationContext context = ContextHolder.getContext();
            if (context != null) {
                tracer = context.getBean(Tracer.class);
            }
        }
        return tracer;
    }

}
