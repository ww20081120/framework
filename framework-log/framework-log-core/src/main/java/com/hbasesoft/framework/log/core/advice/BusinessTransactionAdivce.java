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

import com.hbasesoft.framework.common.GlobalConstants;
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
    private static final boolean DEBUG_OPEN_FLAG = PropertyHolder.getBooleanProperty("logservice.framework.show",
        false);

    /** */
    private static final String[] EXCLOUDS = StringUtils.split(PropertyHolder.getProperty("logservice.nologs.package"),
        GlobalConstants.SPLITOR);

    /** 框架日志的方法 */
    private static final String FRAMEWORK_PACKAGE = "com.hbasesoft.framework.";

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
