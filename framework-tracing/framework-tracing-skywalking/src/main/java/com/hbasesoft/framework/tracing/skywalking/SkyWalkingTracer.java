/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.skywalking;

import java.util.Map;

import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Span.Builder;
import io.micrometer.tracing.SpanCustomizer;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年8月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.skywalking <br>
 */
@RequiredArgsConstructor
public class SkyWalkingTracer implements Tracer {

    /** 代理的tracer */
    private final org.apache.skywalking.apm.toolkit.trace.Tracer tracer;

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Map<String, String> getAllBaggage() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    @Override
    public Baggage getBaggage(final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param traceContext
     * @param name
     * @return <br>
     */
    @Override
    public Baggage getBaggage(final TraceContext traceContext, final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param name
     * @return
     * @deprecated <br>
     */
    @Override
    public Baggage createBaggage(final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param name
     * @param value
     * @return
     * @deprecated <br>
     */
    @Override
    public Baggage createBaggage(final String name, final String value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Span nextSpan() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param parent
     * @return <br>
     */
    @Override
    public Span nextSpan(final Span parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param span
     * @return <br>
     */
    @Override
    public SpanInScope withSpan(final Span span) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    @Override
    public ScopedSpan startScopedSpan(final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Builder spanBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public io.micrometer.tracing.TraceContext.Builder traceContextBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public CurrentTraceContext currentTraceContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public SpanCustomizer currentSpanCustomizer() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Span currentSpan() {
        // TODO Auto-generated method stub
        return null;
    }

}
