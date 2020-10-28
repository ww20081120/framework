/**
 * 
 */
package com.hbasesoft.framework.log.file;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.log.core.AbstractTransLoggerService;

import brave.Span;
import brave.Tracer;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.hbasesoft.framework.log.file <br>
 */
public class TransLoggerService4File extends AbstractTransLoggerService {

    /** tracer */
    private Tracer tracer;

    /** spanMap */
    private Map<String, Span> spanMap = new ConcurrentHashMap<>();

    /** 是否显示http header */
    private static final boolean HTTP_HEADER = PropertyHolder.getBooleanProperty("logservice.httpHeader.show", false);

    /** http header 的前缀 */
    private static final String PREFIX = "http.header.";

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param parentStackId
     * @param beginTime
     * @param method
     * @param params <br>
     */
    @Override
    public void before(final String stackId, final String parentStackId, final long beginTime, final String method,
        final Object[] params) {
        Tracer tc = getTracer();
        if (tc != null) {
            Span span = tc.currentSpan();
            if (span == null) {
                span = tc.newTrace();
            }
            else if (StringUtils.isNotEmpty(parentStackId)) {
                span = tc.newChild(spanMap.get(parentStackId).context());
            }
            else {
                span = tc.newChild(tc.currentSpan().context());
            }
            span.tag("stackId", stackId);
            if (StringUtils.isNotEmpty(parentStackId)) {
                span.tag("parentStackId", parentStackId);
            }
            span.tag("method", method);
            if (params != null) {
                span.tag("params", Arrays.toString(params));
            }

            if (HTTP_HEADER) {
                RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
                if (attributes != null && attributes instanceof ServletRequestAttributes) {
                    HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
                    if (request != null) {
                        Enumeration<String> names = request.getHeaderNames();
                        if (names != null) {
                            while (names.hasMoreElements()) {
                                String name = names.nextElement();
                                span.tag(PREFIX + name, request.getHeader(name));
                            }
                        }
                    }
                }
            }

            span.start();
            spanMap.put(stackId, span);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param endTime
     * @param consumeTime
     * @param method
     * @param returnValue <br>
     */
    @Override
    public void afterReturn(final String stackId, final long endTime, final long consumeTime, final String method,
        final Object returnValue) {
        Span span = spanMap.remove(stackId);
        if (span != null) {
            if (returnValue != null) {
                span.tag("returnValue", CommonUtil.getString(returnValue));
            }
            span.finish();
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param endTime
     * @param consumeTime
     * @param e <br>
     */
    @Override
    public void afterThrow(final String stackId, final long endTime, final long consumeTime, final String method,
        final Throwable e) {
        Span span = spanMap.remove(stackId);
        if (span != null) {
            span.tag("error", "true");
            span.tag("exception", e.getClass().getName());
            String errorMsg = e.getMessage();
            if (StringUtils.isNotEmpty(errorMsg)) {
                span.tag("errorMsg", errorMsg);
            }
            span.finish();
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param sql <br>
     */
    @Override
    public void sql(final String stackId, final String sql) {
        if (stackId != null) {
            Span span = spanMap.get(stackId);
            if (span != null) {
                span.tag(System.nanoTime() + "", sql);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId
     * @param beginTime
     * @param endTime
     * @param consumeTime
     * @param method
     * @param returnValue
     * @param e <br>
     */
    @Override
    public void end(final String stackId, final long beginTime, final long endTime, final long consumeTime,
        final String method, final Object returnValue, final Throwable e) {
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
