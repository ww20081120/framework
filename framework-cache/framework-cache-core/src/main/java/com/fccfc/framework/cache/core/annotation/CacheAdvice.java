/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.cache.core.annotation;

import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.utils.CommonUtil;

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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param arg0
     * @return
     * @throws Throwable <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = null;
        Class<?> returnType = invocation.getMethod().getReturnType();
        Cache cache = AnnotationUtils.findAnnotation(invocation.getMethod(), Cache.class);

        // 携带Cache注解的方法，返回类型不能为空
        if (cache != null && !Void.class.equals(returnType)) {

            // 未填写key，而且值不是Map类型的数据，自动保存在CacheConstant.DEFAULT_CACHE_DIR节点下
            if (CommonUtil.isNotEmpty(cache.key())) {
                result = CacheHelper.getCache().getValue(returnType, cache.node(), cache.key());
            }
            else if (returnType.isAssignableFrom(Map.class)) {
                result = CacheHelper.getCache().getNode(returnType, cache.node());
            }
            else {
                result = CacheHelper.getCache().getValue(returnType, CacheConstant.DEFAULT_CACHE_DIR, cache.node());
            }

            if (result == null) {
                result = invocation.proceed();
                if (result != null) {
                    if (CommonUtil.isNotEmpty(cache.key())) {
                        CacheHelper.getCache().putValue(cache.node(), cache.key(), result);
                    }
                    else if (returnType.isAssignableFrom(Map.class)) {
                        CacheHelper.getCache().putNode(cache.node(), (Map<String, ?>) result);
                    }
                    else {
                        CacheHelper.getCache().putValue(CacheConstant.DEFAULT_CACHE_DIR, cache.node(), result);
                    }
                }
            }
        }
        else {
            result = invocation.proceed();
        }
        return result;
    }

}
