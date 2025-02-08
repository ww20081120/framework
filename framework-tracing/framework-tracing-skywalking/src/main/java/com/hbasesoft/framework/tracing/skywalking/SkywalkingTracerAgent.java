/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.skywalking;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.SpanRef;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.apache.skywalking.apm.toolkit.trace.Tracer;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.tracing.core.TracerAgent;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年2月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.skywalking <br>
 */
public class SkywalkingTracerAgent implements TracerAgent {

    /** 最大长度 */
    private static final int MAX_LENGTH = 1000;

    /** 允许展示的头 */
    private static final List<String> ACCEPT_HEADERS = Arrays.asList(
        StringUtils.split(PropertyHolder.getProperty("logservice.httpHeaders", "Authorization,cookie").toUpperCase(),
            GlobalConstants.SPLITOR));

    /** http header 的前缀 */
    private static final String PREFIX = "http.header.";

    /** Closeable */
    private static final Closeable CLOSEABLE = new Closeable() {
        @Override
        public void close() throws IOException {
            Tracer.stopSpan();
        }
    };

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getTraceId() {
        return TraceContext.traceId();
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

        SpanRef spanRef = Tracer.createLocalSpan(methodName);
        if (args != null) {
            String objStr = Arrays.toString(args);
            spanRef.tag("params", objStr.length() > MAX_LENGTH ? objStr.substring(0, MAX_LENGTH) + "..." : objStr);
        }
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null && attributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes sa = (ServletRequestAttributes) attributes;
            HttpServletRequest request = sa.getRequest();
            if (request != null) {
                Enumeration<String> names = request.getHeaderNames();
                if (names != null) {
                    while (names.hasMoreElements()) {
                        String name = names.nextElement();
                        if (ACCEPT_HEADERS.contains(name.toUpperCase()) || name.toUpperCase().startsWith("H_")) {
                            spanRef.tag(PREFIX + name, request.getHeader(name));
                        }
                    }
                }
            }
        }
        return CLOSEABLE;
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
        if (returnValue != null) {
            String objStr = CommonUtil.getString(returnValue);
            ActiveSpan.tag("returnValue",
                objStr.length() > MAX_LENGTH ? objStr.substring(0, MAX_LENGTH) + "..." : objStr);
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
        ActiveSpan.tag("error", "true");
        ActiveSpan.error(e);
    }

}
