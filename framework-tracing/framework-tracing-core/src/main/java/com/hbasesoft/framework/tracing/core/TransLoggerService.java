/**
 * 
 */
package com.hbasesoft.framework.tracing.core;

import io.micrometer.tracing.Span;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.hbasesoft.framework.tracing.core <br>
 */
public interface TransLoggerService {

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
    void before(Span span, Span parentSpan, long beginTime, String method, Object[] params);

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
    void afterReturn(Span span, long endTime, long consumeTime, String method, Object returnValue);

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
    void afterThrow(Span span, long endTime, long consumeTime, String method, Throwable e);
}
