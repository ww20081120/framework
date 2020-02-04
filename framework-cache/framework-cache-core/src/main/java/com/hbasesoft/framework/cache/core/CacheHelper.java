/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ServiceLoader;

import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.cache.core.annotation.CacheProxy;
import com.hbasesoft.framework.cache.core.handler.CachePorxyInvocationHandler;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.core.cache <br>
 */
public final class CacheHelper {

    /** cache */
    private static ICache cache;

    public static ICache getCache() {

        if (cache == null) {
            String cacheModel = PropertyHolder.getProperty("cache.model");
            Assert.notEmpty(cacheModel, ErrorCodeDef.CACHE_MODEL_NOT_SET);

            ServiceLoader<ICache> serviceLoader = ServiceLoader.load(ICache.class);
            for (ICache c : serviceLoader) {
                if (cacheModel.equals(c.getName())) {
                    cache = c;
                    break;
                }
            }

            if (cache == null) {
                throw new InitializationException(ErrorCodeDef.CACHE_NOT_FOUND);
            }
        }

        return cache;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param paths <br>
     * @return <br>
     */
    public static String buildKey(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            sb.append(GlobalConstants.PATH_SPLITOR).append(path);
        }
        return sb.toString();
    }

    public static <T> T proxy(Class<T> clazz, CacheProxy cacheProxy) {

        if (Modifier.isAbstract(clazz.getModifiers())) {
            T target = null;
            if (StringUtils.isNotEmpty(cacheProxy.name())) {
                target = ContextHolder.getContext().getBean(cacheProxy.name(), clazz);
            }
            else {
                target = ContextHolder.getContext().getBean(clazz);
            }

            Assert.notNull(target, ErrorCodeDef.PROXY_TARGET_NOT_FOUND, clazz);

            CachePorxyInvocationHandler invocationHandler = new CachePorxyInvocationHandler(target, cacheProxy, clazz);

            @SuppressWarnings("unchecked")
            T proxyObj = (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.isInterface() ? new Class[] {
                clazz
            } : clazz.getInterfaces(), invocationHandler);

            LoggerUtil.debug("Success cache proxy clazz[{0}].", clazz);
            return proxyObj;
        }
        return null;
    }

}
