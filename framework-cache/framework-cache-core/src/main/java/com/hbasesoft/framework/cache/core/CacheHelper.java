/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.hbasesoft.framework.cache.core.lock.Lock;
import com.hbasesoft.framework.cache.core.lock.LockLoader;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.Invoker;
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

    /** LockLoader */
    private static LockLoader lockLoader;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
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
     * Description: 获取缓存<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param lockName
     * @return <br>
     */
    public static Lock getLock(final String lockName) {
        if (lockLoader == null) {
            ServiceLoader<LockLoader> serviceLoader = ServiceLoader.load(LockLoader.class);
            if (serviceLoader != null) {
                Iterator<LockLoader> iterator = serviceLoader.iterator();
                if (iterator.hasNext()) {
                    lockLoader = iterator.next();
                }
            }
        }
        return lockLoader.getInstance(lockName);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param key
     * @param seconds
     * @param invoker
     * @return <br>
     */
    public static <T> T lock(final String key, final int seconds, final Invoker<T> invoker) {
        Lock lock = getLock(key);
        try {
            lock.lock(seconds);
            return invoker.invoke();
        }
        catch (Throwable e) {
            throw new FrameworkException(e);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param key
     * @param invoker
     * @return <br>
     */
    public static <T> T getCacheData(final String key, final Invoker<T> invoker) {
        return getCacheData(key, 0, invoker);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param key
     * @param seconds
     * @param invoker
     * @return <br>
     */
    public static <T> T getCacheData(final String key, final int seconds, final Invoker<T> invoker) {
        ICache c = getCache();
        T data = c.get(key);
        if (data == null) {
            try {
                data = invoker.invoke();
            }
            catch (Throwable e) {
                LoggerUtil.error(e);
            }
            if (data != null) {
                c.put(key, seconds, data);
            }
        }
        return data;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param paths <br>
     * @return <br>
     */
    public static String buildKey(final String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            sb.append(GlobalConstants.PATH_SPLITOR).append(path);
        }
        return sb.toString();
    }

}
