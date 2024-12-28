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
public class RmCacheNodeAdvice {

    /** logger */
    private static Logger logger = new Logger(RmCacheNodeAdvice.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param thisJoinPoint
     * @return Object
     * @throws Throwable <br>
     */
    @Around("@annotation(com.hbasesoft.framework.cache.core.annotation.RmCacheNode)")
    public Object invoke(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
        Object result = null;

        Signature sig = thisJoinPoint.getSignature();
        if (sig instanceof MethodSignature) {
            MethodSignature msig = (MethodSignature) sig;
            Object target = thisJoinPoint.getTarget();
            Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
            result = thisJoinPoint.proceed();
            RmCacheNode rmCache = AnnotationUtils.findAnnotation(currentMethod, RmCacheNode.class);
            if (rmCache != null) {
                rmCache(rmCache, currentMethod, thisJoinPoint.getArgs());
            }
            return result;

        }
        else {
            return thisJoinPoint.proceed();
        }
    }

    private void rmCache(final RmCacheNode rmCache, final Method method, final Object[] args) throws Exception {
        if (rmCache.clean()) {
            for (String node : rmCache.node()) {
                CacheHelper.getCache().remove(node);
                logger.debug("－－－－－－>{0}方法删除缓存node成功,节点[{1}]", BeanUtil.getMethodSignature(method), node);
            }
        }
        else {
            String key = getCacheKey(rmCache.key(), method, args);
            for (String node : rmCache.node()) {
                CacheHelper.getCache().removeNodeValue(node, key);
                logger.debug("－－－－－－>{0}方法删除缓存key_value成功,节点[{1}] key[{2}]", BeanUtil.getMethodSignature(method),
                    rmCache.node(), key);
            }
        }
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
