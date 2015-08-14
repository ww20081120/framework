/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.cache.simple;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.ICache;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.fccfc.framework.core.cache.simple <br>
 */
public class SimpleCache implements ICache {

    /**
     * 缓存模式
     */
    public static final String CACHE_MODEL = "SIMPLE";

    /** cachesMap */
    private Map<String, Map<String, Object>> cachesMap;

    /**
     * SimpleCache
     * @param cachesMap <br>
     */
    public SimpleCache(Map<String, Map<String, Object>> cachesMap) {
        this.cachesMap = cachesMap;
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#getNode(java.lang.String)
     */
    @Override
    public Map<String, Object> getNode(String nodeName) throws CacheException {
        return cachesMap.get(nodeName);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#putNode(java.lang.String, java.util.Map)
     */
    @Override
    public void putNode(String nodeName, Map<String, Object> node) throws CacheException {
        cachesMap.put(nodeName, node);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#removeNode(java.lang.String)
     */
    @Override
    public boolean removeNode(String nodeName) throws CacheException {
        cachesMap.remove(nodeName);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#getValue(java.lang.String, java.lang.String)
     */
    @Override
    public Object getValue(String nodeName, String key) throws CacheException {
        Map<String, Object> node = getNode(nodeName);
        return node == null ? null : node.get(key);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#putValue(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void putValue(String nodeName, String key, Object t) throws CacheException {
        Map<String, Object> node = getNode(nodeName);
        if (node == null) {
            node = new ConcurrentHashMap<String, Object>();
            putNode(nodeName, node);
        }
        node.put(key, t);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#updateValue(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void updateValue(String nodeName, String key, Object t) throws CacheException {
        putValue(nodeName, key, t);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#removeValue(java.lang.String, java.lang.String)
     */
    @Override
    public void removeValue(String nodeName, String key) throws CacheException {
        Map<String, Object> node = getNode(nodeName);
        if (node == null) {
            node = new ConcurrentHashMap<String, Object>();
            putNode(nodeName, node);
        }
        node.remove(key);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#clean()
     */
    @Override
    public void clean() throws CacheException {
        cachesMap.clear();
    }

}
