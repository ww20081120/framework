/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.ehcache;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.cache.support.SimpleValueWrapper;

import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.ICache;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年4月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.ehcache <br>
 */
public class EhcacheImpl implements ICache {

    /**
     * 缓存模式
     */
    public static final String CACHE_MODEL = "EHCACHE";

    /** cacheManager */
    private CacheManager cacheManager;

    /**
     * EhcacheImpl
     */
    public EhcacheImpl() {
        URL url = EhcacheImpl.class.getResource("ehcache.xml");
        this.cacheManager = (url != null) ? CacheManager.create(url) : CacheManager.create();

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        return CACHE_MODEL;
    }

    @Override
    public final CacheManager getNativeCache() {
        return this.cacheManager;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @return <br>
     */
    @Override
    public ValueWrapper get(final Object key) {
        Element element = getCache(CacheConstant.DEFAULT_CACHE_DIR).get(key);
        return toWrapper(element);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param type
     * @return <br>
     */
    @Override
    public <T> T get(final Object key, final Class<T> type) {
        return get(CacheConstant.DEFAULT_CACHE_DIR, key.toString());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param value <br>
     */
    @Override
    public void put(final Object key, final Object value) {
        put(CacheConstant.DEFAULT_CACHE_DIR, key.toString(), value);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param value
     * @return <br>
     */
    @Override
    public ValueWrapper putIfAbsent(final Object key, final Object value) {
        Element existingElement = getCache(CacheConstant.DEFAULT_CACHE_DIR).putIfAbsent(new Element(key, value));
        return toWrapper(existingElement);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     */
    @Override
    public void evict(final Object key) {
        getCache(CacheConstant.DEFAULT_CACHE_DIR).remove(key);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void clear() {
        cacheManager.clearAll();
    }

    private ValueWrapper toWrapper(final Element element) {
        return (element != null ? new SimpleValueWrapper(element.getObjectValue()) : null);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param type
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getNode(final String nodeName, final Class<T> type) {
        Cache cache = getCache(nodeName);
        List<String> keys = cache.getKeysWithExpiryCheck();
        Map<String, T> cacheMap = new HashMap<String, T>();
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                Element element = cache.get(key);
                Object value = (element != null ? element.getObjectValue() : null);
                if (value != null && type != null && !type.isInstance(value)) {
                    throw new IllegalStateException(
                        "Cached value is not of required type [" + type.getName() + "]: " + value);
                }
                cacheMap.put(key, (T) value);
            }
        }
        return cacheMap;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param node <br>
     */
    @Override
    public <T> void putNode(final String nodeName, final Map<String, T> node) {
        putNode(nodeName, 0, node);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     */
    @Override
    public void removeNode(final String nodeName) {
        cacheManager.removeCache(nodeName);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final String nodeName, final String key) {
        Element element = getCache(nodeName).get(key);
        Object value = (element != null ? element.getObjectValue() : null);
        return (T) value;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key
     * @param t <br>
     */
    @Override
    public <T> void put(final String nodeName, final String key, final T t) {
        getCache(nodeName).put(new Element(key, t));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key <br>
     */
    @Override
    public void evict(final String nodeName, final String key) {
        getCache(nodeName).remove(key.toString());
    }

    private Cache getCache(final String nodeName) {
        return getCache(nodeName, 0, 0);
    }

    private Cache getCache(final String nodeName, final long timeToLiveSeconds, final long timeToIdleSeconds) {
        Cache cache = cacheManager.getCache(nodeName);
        if (cache == null) {
            cache = new Cache(nodeName, 0, true, false, timeToLiveSeconds, timeToIdleSeconds);
            cacheManager.addCache(cache);
        }
        return cache;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param expireTimes
     * @param node <br>
     */
    @Override
    public <T> void putNode(final String nodeName, final int expireTimes, final Map<String, T> node) {
        if (MapUtils.isNotEmpty(node)) {
            Cache cache = getCache(nodeName, expireTimes, 0);
            for (Entry<String, T> entry : node.entrySet()) {
                cache.put(new Element(entry.getKey(), entry.getValue()));
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param expireTimes
     * @param key
     * @param t <br>
     */
    @Override
    public <T> void put(final String nodeName, final int expireTimes, final String key, final T t) {
        getCache(nodeName, expireTimes, 0).put(new Element(key, t));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param valueLoader
     * @return <br>
     */
    @Override
    public <T> T get(final Object key, final Callable<T> valueLoader) {
        T result = get(CacheConstant.DEFAULT_CACHE_DIR, key.toString());
        if (result == null) {
            try {
                result = valueLoader.call();
                put(CacheConstant.DEFAULT_CACHE_DIR, key.toString(), result);
            }
            catch (Exception e) {
                LoggerUtil.error(e);
            }
        }
        return result;
    }

}
