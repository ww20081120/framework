/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis.lock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.cache.core.annotation.CacheLock;
import com.hbasesoft.framework.cache.core.annotation.Key;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.engine.VelocityParseFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.annotation <br>
 */
@Aspect
@Configuration
public class CacheLockAdvice {

    @Pointcut("execution(public * com.hbasesoft..*Service.*(..))")
    public void cacheLock() {
    }

    @Around("cacheLock()")
    public Object invoke(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        Signature sig = thisJoinPoint.getSignature();
        if (sig instanceof MethodSignature) {
            MethodSignature msig = (MethodSignature) sig;
            Object target = thisJoinPoint.getTarget();
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

            CacheLock cacheLock = AnnotationUtils.findAnnotation(currentMethod, CacheLock.class);
            if (cacheLock != null) {
                // 新建一个锁
                RedisLock lock = new RedisLock(
                    cacheLock.value() + getLockKey(cacheLock.key(), currentMethod, thisJoinPoint.getArgs()));

                // 加锁
                boolean result = lock.lock(cacheLock.timeOut(), cacheLock.expireTime());
                if (!result) {
                    throw new ServiceException(ErrorCodeDef.GET_CACHE_LOCK_ERROR, "获取锁{0}失败", lock);
                }

                try {
                    // 加锁成功，执行方法
                    return thisJoinPoint.proceed();
                }
                finally {
                    lock.unlock();
                }

            }
        }
        return thisJoinPoint.proceed();
    }

    private String getLockKey(String template, Method method, Object[] args) throws FrameworkException {
        if (CommonUtil.isEmpty(template) && CommonUtil.isEmpty(args)) {
            return GlobalConstants.BLANK;
        }
        String key;
        if (CommonUtil.isNotEmpty(template)) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof Key) {
                        paramMap.put(((Key) annotation).value(), args[i]);
                        break;
                    }
                }
            }
            key = VelocityParseFactory.parse(CommonUtil.getTransactionID(), template, paramMap);
        }
        else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(GlobalConstants.UNDERLINE);
                }
                sb.append(args[i] == null ? GlobalConstants.BLANK : args[i]);
            }
            key = sb.toString();
        }
        return "_" + key;
    }
}
