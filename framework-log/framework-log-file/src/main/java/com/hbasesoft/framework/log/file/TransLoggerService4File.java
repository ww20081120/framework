/**
 * 
 */
package com.hbasesoft.framework.log.file;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.log.core.TransLoggerService;

import brave.Span;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.hbasesoft.framework.log.file <br>
 */
public class TransLoggerService4File implements TransLoggerService {

    /** 最大长度 */
    private static final int MAX_LENGTH = 1000;

    /** 允许展示的头 */
    private static final List<String> ACCEPT_HEADERS = Arrays.asList(
        StringUtils.split(PropertyHolder.getProperty("logservice.httpHeaders", "Authorization,cookie").toUpperCase(),
            GlobalConstants.SPLITOR));

    /** http header 的前缀 */
    private static final String PREFIX = "http.header.";

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param span
     * @param parentSpan
     * @param beginTime
     * @param method
     * @param params <br>
     */
    @Override
    public void before(final Span span, final Span parentSpan, final long beginTime, final String method,
        final Object[] params) {
        span.tag("method", method);
        if (params != null) {
            String objStr = Arrays.toString(params);
            span.tag("params", objStr.length() > MAX_LENGTH ? objStr.substring(0, MAX_LENGTH) : objStr);
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
                            span.tag(PREFIX + name, request.getHeader(name));
                        }
                    }
                }
            }
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
     * @param method
     * @param returnValue <br>
     */
    @Override
    public void afterReturn(final Span span, final long endTime, final long consumeTime, final String method,
        final Object returnValue) {
        if (returnValue != null) {
            String objStr = CommonUtil.getString(returnValue);
            span.tag("returnValue", objStr.length() > MAX_LENGTH ? objStr.substring(0, MAX_LENGTH) : objStr);
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
     * @param method
     * @param e <br>
     */
    @Override
    public void afterThrow(final Span span, final long endTime, final long consumeTime, final String method,
        final Throwable e) {
        span.tag("error", "true");
        span.tag("exception", e.getClass().getName());
        String errorMsg = e.getMessage();
        if (StringUtils.isNotEmpty(errorMsg)) {
            span.tag("errorMsg", errorMsg.length() > MAX_LENGTH ? errorMsg.substring(0, MAX_LENGTH) : errorMsg);
        }
    }

}
