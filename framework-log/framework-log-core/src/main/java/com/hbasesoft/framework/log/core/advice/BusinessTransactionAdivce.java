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
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.common.FrameworkException;
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
@Configuration
public class BusinessTransactionAdivce {

    @Pointcut("execution(public * com.hbasesoft..*(..))")
    public void log() {
    }

    @Around("log()")
    public Object around(ProceedingJoinPoint joinPoint) throws FrameworkException {

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
            throw new FrameworkException(e);
        }
    }
    //
    // /**
    // * 方法执行前拦截
    // *
    // * @param point 参数
    // * @throws FrameworkException <br>
    // */
    // @Before("log()")
    // public void before(JoinPoint point) throws FrameworkException {
    // MethodSignature methodSignature = (MethodSignature) point.getSignature();
    // TransLogUtil.before(point.getTarget(), methodSignature.getMethod(), point.getArgs());
    // }
    //
    // /**
    // * 方法正常执行结束拦截
    // *
    // * @param point 参数
    // * @param returnValue 方法返回值
    // * @throws ServiceException
    // */
    // @AfterReturning(pointcut = "log()", returning = "returnValue")
    // public void afterReturning(JoinPoint point, Object returnValue) {
    // MethodSignature methodSignature = (MethodSignature) point.getSignature();
    // TransLogUtil.afterReturning(point.getTarget(), methodSignature.getMethod(), returnValue);
    // }
    //
    // /**
    // * 方法异常执行结束拦截
    // *
    // * @param point 参数
    // * @param ex 异常信息
    // * @throws ServiceException
    // */
    // @AfterThrowing(pointcut = "log()", throwing = "ex")
    // public void afterThrowing(JoinPoint point, Exception ex) {
    // MethodSignature methodSignature = (MethodSignature) point.getSignature();
    // TransLogUtil.afterThrowing(point.getTarget(), methodSignature.getMethod(), ex);
    // }

}
