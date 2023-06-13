/**
 * 
 */
package com.hbasesoft.framework.log.core.advice;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.annotation.NoTransLog;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.log.core.TransLogUtil;

import brave.Span;
import brave.Tracer;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月6日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.advice <br>
 */
@Aspect
@Component
public class BusinessTransactionAdivce {

    /** framework 的日志是否打印 */
    private static final boolean DEBUG_OPEN_FLAG = PropertyHolder.getBooleanProperty("logservice.framework.show",
        false);

    /** */
    private static final String[] EXCLOUDS = StringUtils.split(PropertyHolder.getProperty("logservice.nologs.package"),
        GlobalConstants.SPLITOR);

    /** 框架日志的方法 */
    private static final String FRAMEWORK_PACKAGE = "com.hbasesoft.framework.";

    /** tracer */
    private Tracer tracer;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Pointcut("execution(public * com.hbasesoft..*(..))")
    public void log() {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param joinPoint
     * @return Object
     * @throws Throwable <br>
     */
    @Around("log()")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {

        Tracer tc = getTracer();
        if (tc == null) {
            return joinPoint.proceed();
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object target = joinPoint.getTarget();

        // 带有NoTranslog的不打印
        NoTransLog noTransLog = target.getClass().getAnnotation(NoTransLog.class);
        if (noTransLog != null) {
            return joinPoint.proceed();
        }

        // 框架不打印
        Method method = methodSignature.getMethod();
        String clazzName = method.getDeclaringClass().getName();
        if (!DEBUG_OPEN_FLAG && StringUtils.startsWith(clazzName, FRAMEWORK_PACKAGE)) {
            return joinPoint.proceed();
        }

        // 排除掉的包不打印
        if (EXCLOUDS != null && EXCLOUDS.length > 0) {
            for (String pack : EXCLOUDS) {
                if (StringUtils.startsWith(clazzName, pack)) {
                    return joinPoint.proceed();
                }
            }
        }

        // 父Span
        Span parentSpan = tc.currentSpan();

        // 当前的Span
        Span span = null;
        if (parentSpan != null) {
            span = tracer.newChild(parentSpan.context());
        }
        else {
            span = tracer.newTrace();
        }

        // 开始执行时间
        long beginTime = System.currentTimeMillis();
        span.start(beginTime);

        Object[] args = joinPoint.getArgs();
        String methodStr = getMethodSignature(method);
        TransLogUtil.before(span, parentSpan, beginTime, methodStr, args);
        try {
            Object returnValue = joinPoint.proceed();
            // 执行完成时间
            long endTime = System.currentTimeMillis();
            TransLogUtil.afterReturning(span, endTime, beginTime - endTime, methodStr, returnValue);
            span.finish(endTime);
            return returnValue;
        }
        catch (Throwable e) {
            // 执行完成时间
            long endTime = System.currentTimeMillis();
            TransLogUtil.afterThrowing(span, endTime, beginTime - endTime, methodStr, e);
            span.finish(endTime);
            throw e;
        }
        finally {
            TransLogUtil.end(span);
        }
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
        if (CommonUtil.isNotEmpty(types)) {
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
