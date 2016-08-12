/**
 * 
 */
package com.hbasesoft.framework.log.core.advice;

import java.util.ServiceLoader;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.log.core.TransLoggerService;
import com.hbasesoft.framework.log.core.TransManager;
import com.hbasesoft.framework.log.core.annotation.NoTransLog;

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

    @Pointcut("execution(public * com.hbasesoft..*Service*.*(..)) or "
        + "execution(public * com.hbasesoft..*Controller*.*(..)) or "
        + "execution(public * com.hbasesoft..*Interceptor*.*(..)) or "
        + "execution(public * com.hbasesoft..*Excutor*.*(..)) or "
        + "execution(public * com.hbasesoft..*Handler*.*(..))")
    public void log() {
    }

    /**
     * 栈最大深度
     */
    @Value("${logservice.max.deep.size}")
    private int maxDeepLen;

    /** 最大执行时间 */
    @Value("${logservice.max.execute.time}")
    private long maxExcuteTime;

    @Value("${logservice.aways.log}")
    private boolean alwaysLog;

    /**
     * transLoggerServices
     */
    private ServiceLoader<TransLoggerService> transLoggerServices;

    /**
     * 方法执行前拦截
     * 
     * @param point 参数
     * @throws FrameworkException <br>
     */
    @Before("log()")
    public void before(JoinPoint point) throws FrameworkException {

        NoTransLog noTransLog = point.getTarget().getClass().getAnnotation(NoTransLog.class);
        if (noTransLog == null) {
            // 开始执行时间
            long beginTime = System.currentTimeMillis();

            // 执行方法
            String method = getMethodSignature(point);

            // 输入参数
            Object[] args = point.getArgs();

            TransManager manager = TransManager.getInstance();

            // 深度检测
            if (manager.getStackSize() > maxDeepLen) {
                throw new FrameworkException(ErrorCodeDef.STACK_OVERFLOW_ERROR_10030, "业务过于复杂，请简化业务");
            }

            // 父id
            String parentStackId = manager.peek();

            // id
            String stackId = UUID.randomUUID().toString();
            manager.push(stackId, beginTime);

            // 执行记录
            for (TransLoggerService service : getTransLoggerServices()) {
                service.before(stackId, parentStackId, beginTime, method, args);
            }
        }

    }

    /**
     * 方法正常执行结束拦截
     * 
     * @param point 参数
     * @param returnValue 方法返回值
     * @throws ServiceException
     */
    @AfterReturning(pointcut = "log()", returning = "returnValue")
    public void afterReturning(JoinPoint point, Object returnValue) {
        NoTransLog noTransLog = point.getTarget().getClass().getAnnotation(NoTransLog.class);
        if (noTransLog == null) {
            // 执行完成时间
            long endTime = System.currentTimeMillis();

            TransManager manager = TransManager.getInstance();
            String stackId = manager.pop();
            if (CommonUtil.isEmpty(stackId)) {
                return;
            }

            long beginTime = manager.getBeginTime(stackId);
            long consumeTime = endTime - beginTime;

            if (consumeTime > maxExcuteTime) {
                manager.setTimeout(true);
            }

            // 执行方法
            String method = getMethodSignature(point);

            // 执行记录
            for (TransLoggerService service : getTransLoggerServices()) {
                service.afterReturn(stackId, endTime, consumeTime, method, returnValue);
            }

            if (manager.getStackSize() <= 0) {
                for (TransLoggerService service : getTransLoggerServices()) {
                    service.end(stackId, beginTime, endTime, consumeTime, method, returnValue, null);
                }

                for (TransLoggerService service : getTransLoggerServices()) {
                    service.clean();
                }

                manager.clean();
            }
        }
    }

    /**
     * 方法异常执行结束拦截
     * 
     * @param point 参数
     * @param ex 异常信息
     * @throws ServiceException
     */
    @AfterThrowing(pointcut = "log()", throwing = "ex")
    public void afterThrowing(JoinPoint point, Exception ex) {
        NoTransLog noTransLog = point.getTarget().getClass().getAnnotation(NoTransLog.class);
        if (noTransLog == null) {
            // 执行完成时间
            long endTime = System.currentTimeMillis();

            TransManager manager = TransManager.getInstance();
            String stackId = manager.pop();
            if (CommonUtil.isEmpty(stackId)) {
                return;
            }

            long beginTime = manager.getBeginTime(stackId);
            long consumeTime = endTime - beginTime;

            manager.setError(true);

            // 执行方法
            String method = getMethodSignature(point);

            // 执行记录
            for (TransLoggerService service : getTransLoggerServices()) {
                service.afterThrow(stackId, endTime, consumeTime, method, ex);
            }

            if (manager.getStackSize() <= 0) {
                for (TransLoggerService service : getTransLoggerServices()) {
                    service.end(stackId, beginTime, endTime, consumeTime, method, null, ex);
                }

                for (TransLoggerService service : getTransLoggerServices()) {
                    service.clean();
                }

                manager.clean();
            }
        }
    }

    /**
     * 获取 方法描述
     * 
     * @param point <br>
     * @return <br>
     */
    private String getMethodSignature(JoinPoint point) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(point.getTarget().getClass().getName()).append('<').append(point.getSignature().getName())
            .append('>');
        sbuf.append('(');

        Object[] args = point.getArgs();
        if (CommonUtil.isNotEmpty(args)) {
            for (Object obj : args) {
                sbuf.append(obj == null ? "NULL" : obj.getClass().getName()).append(',');
            }
        }
        sbuf.append(')');
        return sbuf.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    private ServiceLoader<TransLoggerService> getTransLoggerServices() {
        if (transLoggerServices == null) {
            transLoggerServices = ServiceLoader.load(TransLoggerService.class);
            for (TransLoggerService transLoggerService : transLoggerServices) {
                transLoggerService.setAlwaysLog(alwaysLog);
            }
        }
        return transLoggerServices;
    }

    public void setMaxDeepLen(int maxDeepLen) {
        this.maxDeepLen = maxDeepLen;
    }

    public void setMaxExcuteTime(long maxExcuteTime) {
        this.maxExcuteTime = maxExcuteTime * 1000;
    }
}
