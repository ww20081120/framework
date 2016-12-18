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

import org.springframework.cache.support.SimpleValueWrapper;

import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.ICache;
import com.hbasesoft.framework.common.utils.CommonUtil;
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

    private CacheManager cacheManager;

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

    @Override
    public ValueWrapper get(Object key) {
        Element element = getCache(CacheConstant.DEFAULT_CACHE_DIR).get(key);
        return toWrapper(element);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return get(CacheConstant.DEFAULT_CACHE_DIR, key.toString());
    }

    @Override
    public void put(Object key, Object value) {
        put(CacheConstant.DEFAULT_CACHE_DIR, key.toString(), value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Element existingElement = getCache(CacheConstant.DEFAULT_CACHE_DIR).putIfAbsent(new Element(key, value));
        return toWrapper(existingElement);
    }

    @Override
    public void evict(Object key) {
        getCache(CacheConstant.DEFAULT_CACHE_DIR).remove(key);
    }

    @Override
    public void clear() {
        cacheManager.clearAll();
    }

    private ValueWrapper toWrapper(Element element) {
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
    public <T> Map<String, T> getNode(String nodeName, Class<T> type) {
        Cache cache = getCache(nodeName);
        List<String> keys = cache.getKeysWithExpiryCheck();
        Map<String, T> cacheMap = new HashMap<String, T>();
        if (CommonUtil.isNotEmpty(keys)) {
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
    public <T> void putNode(String nodeName, Map<String, T> node) {
        putNode(nodeName, 0, node);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @return <br>
     */
    @Override
    public void removeNode(String nodeName) {
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
    public <T> T get(String nodeName, String key) {
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
    public <T> void put(String nodeName, String key, T t) {
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
    public void evict(String nodeName, String key) {
        getCache(nodeName).remove(key.toString());
    }

    private Cache getCache(String nodeName) {
        return getCache(nodeName, 0, 0);
    }

    private Cache getCache(String nodeName, long timeToLiveSeconds, long timeToIdleSeconds) {
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
    public <T> void putNode(String nodeName, int expireTimes, Map<String, T> node) {
        if (CommonUtil.isNotEmpty(node)) {
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
    public <T> void put(String nodeName, int expireTimes, String key, T t) {
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
    public <T> T get(Object key, Callable<T> valueLoader) {
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
