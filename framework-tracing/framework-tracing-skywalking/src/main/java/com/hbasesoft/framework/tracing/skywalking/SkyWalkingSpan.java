/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.skywalking;

import java.util.concurrent.TimeUnit;

import org.apache.skywalking.apm.toolkit.trace.SpanRef;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer.SpanInScope;
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
public class SkyWalkingSpan implements Span, SpanInScope {

    /**
     * span
     */
    private final SpanRef span;

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public boolean isNoop() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public TraceContext context() {
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
    public Span start() {
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
    public Span name(final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param value
     * @return <br>
     */
    @Override
    public Span event(final String value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param value
     * @param time
     * @param timeUnit
     * @return <br>
     */
    @Override
    public Span event(final String value, final long time, final TimeUnit timeUnit) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param key
     * @param value
     * @return <br>
     */
    @Override
    public Span tag(final String key, final String value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param throwable
     * @return <br>
     */
    @Override
    public Span error(final Throwable throwable) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void end() {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param time
     * @param timeUnit <br>
     */
    @Override
    public void end(final long time, final TimeUnit timeUnit) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void abandon() {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param remoteServiceName
     * @return <br>
     */
    @Override
    public Span remoteServiceName(final String remoteServiceName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param ip
     * @param port
     * @return <br>
     */
    @Override
    public Span remoteIpAndPort(final String ip, final int port) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
