/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis.lock;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.cache.core.annotation.DulplicateLock;
import com.hbasesoft.framework.cache.core.redis.AbstractRedisCache;
import com.hbasesoft.framework.cache.core.redis.util.KeyUtil;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年3月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.redis.lock <br>
 */
@Aspect
@Configuration
public class DulplicateLockAdvice {

    public static final String LOCKED = "DULPLICATE_LOCKED";

    private AbstractRedisCache redisCache;

    public DulplicateLockAdvice() {
        this.redisCache = (AbstractRedisCache) CacheHelper.getCache();
    }

    @Pointcut("execution(public * com.hbasesoft..*Service.*(..))")
    public void dulplicateLock() {
    }

    @Around("dulplicateLock()")
    public Object invoke(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        Signature sig = thisJoinPoint.getSignature();
        if (sig instanceof MethodSignature) {
            MethodSignature msig = (MethodSignature) sig;
            Object target = thisJoinPoint.getTarget();
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

            DulplicateLock dulplicateLock = AnnotationUtils.findAnnotation(currentMethod, DulplicateLock.class);
            if (dulplicateLock != null) {
                String key = dulplicateLock.value()
                    + KeyUtil.getLockKey(dulplicateLock.key(), currentMethod, thisJoinPoint.getArgs());

                Assert.isTrue(redisCache.setnx(key, LOCKED, dulplicateLock.expireTime()),
                    ErrorCodeDef.DULPLICATE_MESSAGE, key);

                try {
                    // 加锁成功，执行方法
                    return thisJoinPoint.proceed();
                }
                catch (Exception e) {
                    redisCache.evict(key);
                }
            }
        }
        return thisJoinPoint.proceed();
    }

}
