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
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.annotation.NoTransLog;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.log.core.TransLogUtil;

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
    private static final boolean DEBUG_OPEN_FLAG = PropertyHolder.getBooleanProperty("logservice.framework.show", true);

    /** Filter的日志是否打印 */
    private static final boolean FILTER_OPEN_FLAG = PropertyHolder.getBooleanProperty("logservice.filter.show", false);

    /** 框架日志的方法 */
    private static final String FRAMEWORK_PACKAGE = "com.hbasesoft.framework.";

    @Pointcut("execution(public * com.hbasesoft..*(..))")
    public void log() {
    }

    @Around("log()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object target = joinPoint.getTarget();

        // 有NoTransLog不打印
        NoTransLog noTransLog = target.getClass().getAnnotation(NoTransLog.class);
        if (noTransLog != null) {
            return joinPoint.proceed();
        }

        // 框架不打印
        Method method = methodSignature.getMethod();
        // 执行方法
        String methodName = method.getDeclaringClass().getName();
        if (!DEBUG_OPEN_FLAG && StringUtils.startsWith(methodName, FRAMEWORK_PACKAGE)) {
            return joinPoint.proceed();
        }

        // Filter、Interceptor 不执行
        if (!FILTER_OPEN_FLAG
            && (StringUtils.endsWith(methodName, "Filter") || StringUtils.endsWith(methodName, "Interceptor"))) {
            return joinPoint.proceed();
        }

        Object[] args = joinPoint.getArgs();
        TransLogUtil.before(target, method, args);
        try {
            Object returnValue = joinPoint.proceed();
            TransLogUtil.afterReturning(target, method, returnValue);
            return returnValue;
        }
        catch (Throwable e) {
            TransLogUtil.afterThrowing(target, method, e);
            throw e;
        }
    }
}
