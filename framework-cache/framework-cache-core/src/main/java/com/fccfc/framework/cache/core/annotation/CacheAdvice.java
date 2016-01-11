/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.cache.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.bean.BeanUtil;
import com.fccfc.framework.common.utils.logger.Logger;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月2日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.cache.core.annotation <br>
 */
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
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = null;
        Class<?> returnType = invocation.getMethod().getReturnType();
        Cache cache = AnnotationUtils.findAnnotation(invocation.getMethod(), Cache.class);
        // 携带Cache注解的方法，返回类型不能为空
        if (cache != null && !Void.class.equals(returnType)) {
            result = cache(cache, invocation, returnType);
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

    @SuppressWarnings("unchecked")
    private Object cache(Cache cache, MethodInvocation invocation, Class<?> returnType) throws Throwable {
        Object result = null;
        if (CacheType.KEY_VALUE == cache.type()) {
            String key = cache.key();
            String paramKey = getCacheKey(invocation.getMethod(), invocation.getArguments());
            if (CommonUtil.isNotEmpty(key) && CommonUtil.isNotEmpty(paramKey)) {
                key += GlobalConstants.PERIOD + paramKey;
            }
            else {
                key += paramKey;
                if (CommonUtil.isEmpty(key)) {
                    throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "未设置缓存的key");
                }
            }

            result = CacheHelper.getCache().getValue(returnType, cache.node(), key);
            if (result == null) {
                result = invocation.proceed();
                if (result != null) {
                    CacheHelper.getCache().putValue(cache.node(), key, result);
                    logger.info("－－－－－－>{0}方法设置缓存key_value成功,节点[{1}] key[{2}]",
                        BeanUtil.getMethodSignature(invocation.getMethod()), cache.node(), key);
                }
            }

        }
        else if (CacheType.NODE == cache.type() && !Map.class.isAssignableFrom(returnType)) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "未设置缓存的key，或者返回类型不是Map<String, ?> 类型");
        }
        else {
            result = CacheHelper.getCache().getNode(cache.bean(), cache.node());
            if (result == null) {

                result = invocation.proceed();
                if (result != null) {
                    CacheHelper.getCache().putNode(cache.node(), (Map<String, ?>) result);
                    logger.info("－－－－－－>{0}方法设置缓存node成功,节点[{1}] ", BeanUtil.getMethodSignature(invocation.getMethod()),
                        cache.node());
                }
            }
        }

        return result;
    }

    private void rmCache(RmCache rmCache, MethodInvocation invocation) throws Exception {
        if (CacheType.KEY_VALUE == rmCache.type()) {
            String key = rmCache.key();
            String paramKey = getRmCacheKey(invocation.getMethod(), invocation.getArguments());
            if (CommonUtil.isNotEmpty(key) && CommonUtil.isNotEmpty(paramKey)) {
                key += GlobalConstants.PERIOD + paramKey;
            }
            else {
                key += paramKey;
                if (CommonUtil.isEmpty(key)) {
                    throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "未设置缓存的key");
                }
            }

            CacheHelper.getCache().removeValue(rmCache.node(), key);
            logger.info("－－－－－－>{0}方法删除缓存key_value成功,节点[{1}] key[{2}]",
                BeanUtil.getMethodSignature(invocation.getMethod()), rmCache.node(), key);
        }
        else {
            CacheHelper.getCache().removeNode(rmCache.node());
            logger.info("－－－－－－>{0}方法删除缓存node成功,节点[{1}]", BeanUtil.getMethodSignature(invocation.getMethod()),
                rmCache.node());
        }

    }

    private String getCacheKey(Method method, Object[] args) {
        StringBuilder sb = new StringBuilder();
        Annotation[][] annotations = method.getParameterAnnotations();
        if (CommonUtil.isNotEmpty(annotations)) {
            for (int i = 0; i < annotations.length; i++) {
                boolean hasKey = false;
                Annotation[] as = annotations[i];
                if (CommonUtil.isNotEmpty(as)) {
                    for (Annotation a : as) {
                        if (a instanceof CacheKey) {
                            hasKey = true;
                            break;
                        }
                    }
                }

                if (hasKey) {
                    if (sb.length() > 0) {
                        sb.append(GlobalConstants.PERIOD);
                    }
                    sb.append(args[i]);
                }
            }
        }
        return sb.toString();
    }

    private String getRmCacheKey(Method method, Object[] args) throws OgnlException {
        Annotation[][] annotations = method.getParameterAnnotations();

        if (CommonUtil.isNotEmpty(annotations)) {
            for (int i = 0; i < annotations.length; i++) {
                boolean hasKey = false;
                Annotation[] as = annotations[i];
                CacheKey cacheKey = null;
                if (CommonUtil.isNotEmpty(as)) {
                    for (Annotation a : as) {
                        if (a instanceof CacheKey) {
                            hasKey = true;
                            cacheKey = (CacheKey) a;
                            break;
                        }
                    }
                }

                if (hasKey) {
                    Object obj = CommonUtil.isEmpty(cacheKey.value()) ? args[i]
                        : Ognl.getValue(cacheKey.value(), args[i]);
                    return obj == null ? GlobalConstants.BLANK : obj.toString();
                }
            }
        }
        return GlobalConstants.BLANK;
    }

}
