/**
 * 
 */
package com.hbasesoft.framework.log.core.advice;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

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
        Method method = methodSignature.getMethod();
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
