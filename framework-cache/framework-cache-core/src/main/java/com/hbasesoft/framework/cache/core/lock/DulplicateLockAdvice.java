/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.lock;

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
import com.hbasesoft.framework.cache.core.util.KeyUtil;
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

    /** LOCKED */
    public static final String LOCKED = "DULPLICATE_LOCKED";

    /**
     * Description: dulplicateLock <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Pointcut("execution(public * com.hbasesoft..*Service.*(..))")
    public void dulplicateLock() {
    }

    /**
     * Description: invoke<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param thisJoinPoint
     * @return Object
     * @throws Throwable <br>
     */
    @Around("dulplicateLock()")
    public Object invoke(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
        Signature sig = thisJoinPoint.getSignature();
        if (sig instanceof MethodSignature) {
            MethodSignature msig = (MethodSignature) sig;
            Object target = thisJoinPoint.getTarget();
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

            DulplicateLock dulplicateLock = AnnotationUtils.findAnnotation(currentMethod, DulplicateLock.class);
            if (dulplicateLock != null) {
                String key = dulplicateLock.name()
                    + KeyUtil.getLockKey(dulplicateLock.key(), currentMethod, thisJoinPoint.getArgs());

                Lock lock = CacheHelper.getLock(key);

                Assert.isTrue(lock.tryLock(dulplicateLock.expireTime()), ErrorCodeDef.DULPLICATE_MESSAGE, key);

                try {
                    // 加锁成功，执行方法
                    return thisJoinPoint.proceed();
                }
                catch (Exception e) {
                    lock.unlock();
                }
            }
        }
        return thisJoinPoint.proceed();
    }

}
