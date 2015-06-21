/**
 * 
 */
package com.fccfc.framework.cache.simple;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.IStringCache;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.fccfc.framework.cache.simple <br>
 */
public class SimpleStringCache implements IStringCache {

    /** cachesMap */
    private Map<String, Map<String, String>> cachesMap;

    public SimpleStringCache(Map<String, Map<String, String>> cachesMap) {
        this.cachesMap = cachesMap;
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#getNode(java.lang.String)
     */
    @Override
    public Map<String, String> getNode(String nodeName) throws CacheException {
        return cachesMap.get(nodeName);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#putNode(java.lang.String, java.util.Map)
     */
    @Override
    public void putNode(String nodeName, Map<String, String> node) throws CacheException {
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
    public String getValue(String nodeName, String key) throws CacheException {
        Map<String, String> node = getNode(nodeName);
        return node == null ? null : node.get(key);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#putValue(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void putValue(String nodeName, String key, String t) throws CacheException {
        Map<String, String> node = getNode(nodeName);
        if (node == null) {
            node = new ConcurrentHashMap<String, String>();
            putNode(nodeName, node);
        }
        node.put(key, t);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#updateValue(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void updateValue(String nodeName, String key, String t) throws CacheException {
        putValue(nodeName, key, t);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.ICache#removeValue(java.lang.String, java.lang.String)
     */
    @Override
    public String removeValue(String nodeName, String key) throws CacheException {
        Map<String, String> node = getNode(nodeName);
        if (node == null) {
            node = new ConcurrentHashMap<String, String>();
            putNode(nodeName, node);
        }
        return node.remove(key);
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
