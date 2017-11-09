/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.hbasesoft.framework.cache.core.AbstractCache;
import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.common.utils.CommonUtil;

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
    private Map<String, Map<String, byte[]>> cachesMap;

    /**
     * SimpleCache
     * 
     * @param cachesMap <br>
     */
    public SimpleCache() {
        this.cachesMap = new HashMap<String, Map<String, byte[]>>();
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
        return get(CacheConstant.DEFAULT_CACHE_DIR.getBytes(), key);
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
        put(CacheConstant.DEFAULT_CACHE_DIR.getBytes(), 0, key, value);
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
        this.cachesMap.put(new String(key), null);
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
        Map<byte[], byte[]> cache = new HashMap<>();
        Map<String, byte[]> temp = this.cachesMap.get(new String(node));
        if (CommonUtil.isNotEmpty(temp)) {
            for (Entry<String, byte[]> entry : temp.entrySet()) {
                cache.put(entry.getKey().getBytes(), entry.getValue());
            }
        }
        return cache;
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
        Map<String, byte[]> temp = new HashMap<>();

        if (CommonUtil.isNotEmpty(dataMap)) {
            for (Entry<byte[], byte[]> entry : dataMap.entrySet()) {
                temp.put(new String(entry.getKey()), entry.getValue());
            }
        }
        this.cachesMap.put(new String(key), temp);
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
        this.cachesMap.remove(new String(nodeName));
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
        Map<String, byte[]> defaultCache = this.cachesMap.get(new String(nodeName));
        return defaultCache == null ? null : defaultCache.get(new String(key));
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
    protected void put(byte[] nodeName, int seconds, byte[] key, byte[] t) {
        Map<String, byte[]> defaultCache = this.cachesMap.get(new String(nodeName));
        if (defaultCache == null) {
            defaultCache = new HashMap<String, byte[]>();
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
    protected void evict(byte[] nodeName, byte[] key) {
        put(nodeName, 0, key, null);
    }

}
