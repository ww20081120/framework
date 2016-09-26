/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.simple;

import java.util.HashMap;
import java.util.Map;

import com.hbasesoft.framework.cache.core.AbstractCache;
import com.hbasesoft.framework.cache.core.CacheConstant;

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
     * 缓存模式
     */
    public static final String CACHE_MODEL = "SIMPLE";

    /** cachesMap */
    private Map<byte[], Map<byte[], byte[]>> cachesMap;

    /**
     * SimpleCache
     * 
     * @param cachesMap <br>
     */
    public SimpleCache() {
        this.cachesMap = new HashMap<byte[], Map<byte[], byte[]>>();
    }

    /*
     * (non-Javadoc)
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
    protected byte[] get(byte[] key) {
        return get(CacheConstant.DEFAULT_CACHE_DIR.getBytes());
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
    protected void put(byte[] key, byte[] value) {
        put(CacheConstant.DEFAULT_CACHE_DIR.getBytes(), key, value);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     */
    @Override
    protected void evict(byte[] key) {
        this.cachesMap.put(key, null);
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
    protected Map<byte[], byte[]> getNode(byte[] node) {
        return this.cachesMap.get(node);
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
    protected void putNode(byte[] key, Map<byte[], byte[]> dataMap) {
        this.cachesMap.put(key, dataMap);
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
    protected void removeNode(byte[] nodeName) {
        this.cachesMap.remove(nodeName);
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
    protected byte[] get(byte[] nodeName, byte[] key) {
        Map<byte[], byte[]> defaultCache = this.cachesMap.get(nodeName);
        return defaultCache == null ? null : defaultCache.get(key);
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
    protected void put(byte[] nodeName, byte[] key, byte[] t) {
        Map<byte[], byte[]> defaultCache = this.cachesMap.get(nodeName);
        if (defaultCache == null) {
            defaultCache = new HashMap<byte[], byte[]>();
            this.cachesMap.put(CacheConstant.DEFAULT_CACHE_DIR.getBytes(), defaultCache);
        }
        defaultCache.put(key, t);
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
    protected void evict(byte[] nodeName, byte[] key) {
        put(nodeName, key, null);
    }

}
