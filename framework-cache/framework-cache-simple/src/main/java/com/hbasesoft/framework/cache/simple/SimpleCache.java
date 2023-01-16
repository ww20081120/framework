/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hbasesoft.framework.cache.core.AbstractCache;
import com.hbasesoft.framework.common.utils.PropertyHolder;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.core.cache.simple <br>
 */
public class SimpleCache extends AbstractCache {

    /**
     * 默认缓存目录
     */
    private static final String DEFAULT_CACHE_DIR = "/";

    private static final int MAX_SIZE = PropertyHolder.getIntProperty("cache.max_size", 10000);

    /**
     * 缓存模式
     */
    public static final String CACHE_MODEL = "SIMPLE";

    /** cachesMap */
    private Map<String, Cache<String, byte[]>> cachesMap;

    /**
     * 默认构造
     */
    public SimpleCache() {
        this.cachesMap = new HashMap<String, Cache<String, byte[]>>();
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.hbasesoft.framework.cache.core.ICache#clean()
     */
    @Override
    public void clear() {
        cachesMap.clear();
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Object getNativeCache() {
        return cachesMap;
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
    public byte[] get(final byte[] key) {
        return getNodeValue(DEFAULT_CACHE_DIR.getBytes(), key);
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
    public void put(final byte[] key, int seconds, final byte[] value) {
        putNodeValue(DEFAULT_CACHE_DIR.getBytes(), 0, key, value);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param node
     * @return <br>
     */
    @Override
    public Map<byte[], byte[]> getNode(final byte[] node) {
        Cache<String, byte[]> temp = this.cachesMap.get(new String(node));
        return temp == null ? null : string2ByteMap(temp.asMap());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param dataMap <br>
     */
    @Override
    public void putNode(final byte[] key, int seconds, final Map<byte[], byte[]> dataMap) {
        Cache<String, byte[]> cache = buildCache(seconds);
        cache.putAll(byte2StringMap(dataMap));
        this.cachesMap.put(new String(key), cache);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     */
    @Override
    public void remove(final byte[] nodeName) {
        this.cachesMap.remove(new String(nodeName));
        removeNodeValue(DEFAULT_CACHE_DIR.getBytes(), nodeName);
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
    @Override
    public byte[] getNodeValue(final byte[] nodeName, final byte[] key) {
        Cache<String, byte[]> defaultCache = this.cachesMap.get(new String(nodeName));
        return defaultCache == null ? null : defaultCache.getIfPresent(new String(key));
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
    public void putNodeValue(final byte[] nodeName, final int seconds, final byte[] key, final byte[] t) {
        Cache<String, byte[]> defaultCache = this.cachesMap.get(new String(nodeName));
        if (defaultCache == null) {
            defaultCache = buildCache(seconds);
            this.cachesMap.put(new String(nodeName), defaultCache);
        }
        defaultCache.put(new String(key), t);
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
    public void removeNodeValue(final byte[] nodeName, final byte[] key) {
        Cache<String, byte[]> nodeMap = this.cachesMap.get(new String(nodeName));
        if (nodeMap != null) {
            nodeMap.invalidate(new String(key));
        }
    }

    private Cache<String, byte[]> buildCache(int seconds) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(MAX_SIZE);
        if (seconds > 0) {
            builder.expireAfterWrite(seconds, TimeUnit.SECONDS);
        }
        return builder.build();
    }

    private Map<String, byte[]> byte2StringMap(Map<byte[], byte[]> map) {
        Map<String, byte[]> m = new HashMap<>();
        if (map != null) {
            for (Entry<byte[], byte[]> entry : map.entrySet()) {
                m.put(new String(entry.getKey()), entry.getValue());
            }
        }
        return m;
    }

    private Map<byte[], byte[]> string2ByteMap(Map<String, byte[]> map) {
        Map<byte[], byte[]> m = new HashMap<>();
        if (map != null) {
            for (Entry<String, byte[]> entry : map.entrySet()) {
                m.put(entry.getKey().getBytes(), entry.getValue());
            }
        }
        return m;
    }
}
