/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hbasesoft.framework.cache.core.CacheException;
import com.hbasesoft.framework.cache.core.ICache;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.core.cache.simple <br>
 */
public class SimpleCache implements ICache {

    /**
     * 缓存模式
     */
    public static final String CACHE_MODEL = "SIMPLE";

    /** cachesMap */
    private Map<String, Map<String, ?>> cachesMap;

    /**
     * SimpleCache
     * 
     * @param cachesMap <br>
     */
    public SimpleCache() {
        this.cachesMap = new HashMap<String, Map<String, ?>>();
    }

    /**
     * Description: 获取节点<br>
     * 
     * @author 王伟<br>
     * @param clazz 数据类型
     * @param nodeName 节点名称
     * @return 缓存数据
     * @throws CacheException <br>
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getNode(Class<T> clazz, String nodeName) {
        return (Map<String, T>) cachesMap.get(nodeName);
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.cache.core.ICache#putNode(java.lang.String, java.util.Map)
     */
    @Override
    public <T> void putNode(String nodeName, Map<String, T> node) {
        cachesMap.put(nodeName, node);
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.cache.core.ICache#removeNode(java.lang.String)
     */
    @Override
    public boolean removeNode(String nodeName) {
        cachesMap.remove(nodeName);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.cache.core.ICache#getValue(java.lang.String, java.lang.String)
     */
    @Override
    public <T> T getValue(Class<T> clazz, String nodeName, String key) {
        Map<String, T> node = getNode(clazz, nodeName);
        return node == null ? null : node.get(key);
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.cache.core.ICache#putValue(java.lang.String, java.lang.String, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> void putValue(String nodeName, String key, T t) {
        Map<String, T> node = (Map<String, T>) getNode(t.getClass(), nodeName);
        if (node == null) {
            node = new ConcurrentHashMap<String, T>();
            putNode(nodeName, node);
        }
        node.put(key, t);
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.cache.core.ICache#updateValue(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void updateValue(String nodeName, String key, Object t) throws CacheException {
        putValue(nodeName, key, t);
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.cache.core.ICache#removeValue(java.lang.String, java.lang.String)
     */
    @Override
    public void removeValue(String nodeName, String key) throws CacheException {
        Map<String, ?> node = cachesMap.get(nodeName);
        if (node != null) {
            node.remove(key);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.cache.core.ICache#clean()
     */
    @Override
    public void clean() throws CacheException {
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
    public String getCacheModel() {
        return CACHE_MODEL;
    }

}
