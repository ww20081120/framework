/**
 * 
 */
package com.hbasesoft.framework.log.core.advice;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cloud.sleuth.Tracer.SpanInScope;

import com.hbasesoft.framework.common.annotation.NoTracerLog;
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
public class TracerLogMethodInterceptor implements MethodInterceptor {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param joinPoint
     * @return
     * @throws Throwable <br>
     */
    @Override
    public Object invoke(final MethodInvocation joinPoint) throws Throwable {
        Object target = joinPoint.getThis();

        // 带有NoTranslog的不打印
        NoTracerLog noTransLog = target.getClass().getAnnotation(NoTracerLog.class);
        if (noTransLog != null) {
            return joinPoint.proceed();
        }

        // 框架不打印
        Method method = joinPoint.getMethod();

        // 开始执行时间
        long beginTime = System.currentTimeMillis();

        Object[] args = joinPoint.getArguments();
        try (SpanInScope scope = TransLogUtil.before(beginTime, method, args)) {
            Object returnValue = joinPoint.proceed();
            TransLogUtil.afterReturning(beginTime, method, returnValue);
            return returnValue;
        }
        catch (Throwable e) {
            TransLogUtil.afterThrowing(beginTime, method, e);
            throw e;
        }
    }

}
