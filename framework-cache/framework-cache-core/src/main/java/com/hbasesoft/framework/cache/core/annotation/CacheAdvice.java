/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.annotation.Key;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.engine.VelocityParseFactory;
import com.hbasesoft.framework.common.utils.logger.Logger;

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
@Component
public class CacheAdvice {

    /** logger */
    private static Logger logger = new Logger(CacheAdvice.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param thisJoinPoint
     * @return Object
     * @throws Throwable <br>
     */
    @Around("@annotation(com.hbasesoft.framework.cache.core.annotation.Cache)")
    public Object invoke(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
        Object result = null;

        Signature sig = thisJoinPoint.getSignature();
        if (sig instanceof MethodSignature) {
            MethodSignature msig = (MethodSignature) sig;
            Object target = thisJoinPoint.getTarget();
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

            Class<?> returnType = currentMethod.getReturnType();
            Cache cache = AnnotationUtils.findAnnotation(currentMethod, Cache.class);
            // 携带Cache注解的方法，返回类型不能为空
            if (cache != null && !Void.class.equals(returnType)) {
                result = cache(cache, thisJoinPoint, currentMethod, returnType);
            }
            else {
                result = thisJoinPoint.proceed();
            }
            return result;

        }
        else {
            return thisJoinPoint.proceed();
        }
    }

    private Object cache(final Cache cache, final ProceedingJoinPoint thisJoinPoint, final Method method,
        final Class<?> returnType) throws Throwable {
        String key = getCacheKey(cache.key(), method, thisJoinPoint.getArgs());
        Object result = CacheHelper.getCache().get(key);
        if (result == null) {
            result = thisJoinPoint.proceed();
            if (result != null) {
                int expireTimes = cache.expireTime();
                if (expireTimes > 0) {
                    CacheHelper.getCache().put(key, expireTimes, result);
                }
                else {
                    CacheHelper.getCache().put(key, result);
                }

                logger.debug("－－－－－－>{0}方法设置缓存key_value成功, key[{1}]", BeanUtil.getMethodSignature(method), key);
            }
        }

        return result;
    }

    private String getCacheKey(final String template, final Method method, final Object[] args)
        throws FrameworkException {
        if (StringUtils.isEmpty(template) && ArrayUtils.isEmpty(args)) {
            throw new ServiceException(ErrorCodeDef.CACHE_ERROR);
        }
        String key;
        if (StringUtils.isNotEmpty(template)) {
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
            key = VelocityParseFactory.parse(template, paramMap);
            Assert.isFalse(key.contains(GlobalConstants.DOLLAR_BRACE), ErrorCodeDef.CACHE_ERROR);
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
        return key;
    }
}
