/**
 * 
 */
package com.hbasesoft.framework.log.kafka;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
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
@Service
public class TransLoggerService4Kafka extends AbstractTransLoggerService {

    private Tracer tracer;

    private Map<String, Span> spanMap = new ConcurrentHashMap<>();

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
    public void before(String stackId, String parentStackId, long beginTime, String method, Object[] params) {
        Tracer tracer = getTracer();
        Span span = tracer.currentSpan();
        if (span == null) {
            span = tracer.newTrace();
        }
        else if (StringUtils.isNotEmpty(parentStackId)) {
            span = tracer.newChild(spanMap.get(parentStackId).context());
        }
        else {
            span = tracer.newChild(tracer.currentSpan().context());
        }
        span.tag("stackId", stackId);
        if (StringUtils.isNotEmpty(parentStackId)) {
            span.tag("parentStackId", parentStackId);
        }
        span.tag("method", method);
        if (params != null) {
            span.tag("params", Arrays.toString(params));
        }
        span.start();
        spanMap.put(stackId, span);
    }

    @Override
    public void afterReturn(String stackId, long endTime, long consumeTime, String method, Object returnValue) {
        Span span = spanMap.remove(stackId);
        if (returnValue != null) {
            span.tag("returnValue", CommonUtil.getString(returnValue));
        }
        span.finish();
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
    public void afterThrow(String stackId, long endTime, long consumeTime, String method, Throwable e) {
        Span span = spanMap.remove(stackId);
        span.tag("error", "true");
        span.tag("exception", e.getClass().getName());
        String errorMsg = e.getMessage();
        if (StringUtils.isNotEmpty(errorMsg)) {
            span.tag("errorMsg", errorMsg);
        }
        span.finish();
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.log.core.TransLoggerService#sql(java.lang.String, java.lang.String)
     */
    @Override
    public void sql(String stackId, String sql) {
        spanMap.get(stackId).tag(System.nanoTime() + "", sql);
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
    public void end(String stackId, long beginTime, long endTime, long consumeTime, String method, Object returnValue,
        Throwable e) {
    }

    private Tracer getTracer() {
        if (tracer == null) {
            tracer = ContextHolder.getContext().getBean(Tracer.class);
        }
        return tracer;
    }
}
