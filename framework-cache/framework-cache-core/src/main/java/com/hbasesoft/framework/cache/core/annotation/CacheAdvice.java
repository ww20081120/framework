/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.annotation;

import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
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
public class CacheAdvice implements MethodInterceptor {

    private static Logger logger = new Logger(CacheAdvice.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param arg0
     * @return
     * @throws Throwable <br>
     */
    @Override
    @Pointcut("execution(public * com.hbasesoft..*.*(..)) and !execution(public * com.hbasesoft..*Dao.*(..))")
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = null;
        Class<?> returnType = invocation.getMethod().getReturnType();
        Cache cache = AnnotationUtils.findAnnotation(invocation.getMethod(), Cache.class);
        CacheNode cacheNode = null;
        // 携带Cache注解的方法，返回类型不能为空
        if (cache != null && !Void.class.equals(returnType)) {
            result = cache(cache, invocation, returnType);
        }
        else if ((cacheNode = AnnotationUtils.findAnnotation(invocation.getMethod(), CacheNode.class)) != null) {
            result = cacheNode(cacheNode, invocation, returnType);
        }
        else {
            result = invocation.proceed();
            RmCache rmCache = AnnotationUtils.findAnnotation(invocation.getMethod(), RmCache.class);
            if (rmCache != null) {
                rmCache(rmCache, invocation);
            }
        }
        return result;
    }

    private Object cache(Cache cache, MethodInvocation invocation, Class<?> returnType) throws Throwable {
        String key = getCacheKey(cache.key(), invocation);
        Object result = CacheHelper.getCache().get(cache.node(), key, returnType);
        if (result == null) {
            result = invocation.proceed();
            if (result != null) {
                long expireTimes = cache.expireTime();
                if (expireTimes > 0) {
                    CacheHelper.getCache().put(cache.node(), expireTimes, key, result);
                }
                else {
                    CacheHelper.getCache().put(cache.node(), key, result);
                }

                logger.info("－－－－－－>{0}方法设置缓存key_value成功,节点[{1}] key[{2}]",
                    BeanUtil.getMethodSignature(invocation.getMethod()), cache.node(), key);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Object cacheNode(CacheNode cache, MethodInvocation invocation, Class<?> returnType) throws Throwable {
        if (!Map.class.isAssignableFrom(returnType)) {
            throw new ServiceException(ErrorCodeDef.CACHE_ERROR_10002, "未设置缓存的key，或者返回类型不是Map<String, ?> 类型");
        }

        Object result = CacheHelper.getCache().get(cache.node(), cache.bean());
        if (result == null) {
            result = invocation.proceed();
            if (result != null) {
                long expireTimes = cache.expireTime();
                if (expireTimes > 0) {
                    CacheHelper.getCache().putNode(cache.node(), expireTimes, (Map<String, ?>) result);
                }
                else {
                    CacheHelper.getCache().putNode(cache.node(), (Map<String, ?>) result);
                }

                logger.info("－－－－－－>{0}方法设置缓存node成功,节点[{1}] ", BeanUtil.getMethodSignature(invocation.getMethod()),
                    cache.node());
            }
        }

        return null;
    }

    private void rmCache(RmCache rmCache, MethodInvocation invocation) throws Exception {
        if (rmCache.clean()) {
            CacheHelper.getCache().removeNode(rmCache.node());
            logger.info("－－－－－－>{0}方法删除缓存node成功,节点[{1}]", BeanUtil.getMethodSignature(invocation.getMethod()),
                rmCache.node());
        }
        else {
            String key = getCacheKey(rmCache.key(), invocation);
            CacheHelper.getCache().evict(rmCache.node(), key);
            logger.info("－－－－－－>{0}方法删除缓存key_value成功,节点[{1}] key[{2}]",
                BeanUtil.getMethodSignature(invocation.getMethod()), rmCache.node(), key);
        }

    }

    private String getCacheKey(String prefix, MethodInvocation invocation) throws ServiceException {
        Object[] args = invocation.getArguments();

        if (CommonUtil.isEmpty(args)) {
            throw new ServiceException(ErrorCodeDef.CACHE_ERROR_10002, "未设置缓存的key");
        }
        StringBuilder sb = new StringBuilder(prefix);
        for (Object arg : args) {
            sb.append(GlobalConstants.UNDERLINE).append(arg == null ? GlobalConstants.BLANK : arg);
        }
        return sb.toString();
    }
}
