/**
 * 
 */
package com.hbasesoft.framework.db.core.annotation.handler;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.db.core.DynamicDataSourceManager;
import com.hbasesoft.framework.db.core.annotation.DataSource;

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
public class DynamicDataSourceChangeAdvice implements Ordered {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Pointcut("execution(public * com.hbasesoft..*(..))")
    public void change() {
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
    @Around("change()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        DataSource dataSource = method.getDeclaredAnnotation(DataSource.class);
        if (dataSource != null) {
            DynamicDataSourceManager.setDataSourceCode(dataSource.value());
        }
        else {
            Object target = joinPoint.getTarget();
            dataSource = target.getClass().getDeclaredAnnotation(DataSource.class);
            if (dataSource != null) {
                DynamicDataSourceManager.setDataSourceCode(dataSource.value());
            }
        }
        try {
            return joinPoint.proceed();
        }
        finally {
            if (dataSource != null) {
                DynamicDataSourceManager.setDataSourceCode(DynamicDataSourceManager.DEFAULT_DATASOURCE_CODE);
            }
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
