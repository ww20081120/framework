/**
 * 
 */
package com.fccfc.framework.cache.core;

import java.util.Map;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.fccfc.framework.cache.core <br>
 */
public interface IStringCache {
    /**
     * Description: 获取节点<br>
     * 
     * @author 王伟 <br>
     * @param nodeName 节点名称 <br>
     * @throws CacheException <br>
     * @return <br>
     */
    Map<String, String> getNode(String nodeName) throws CacheException;

    /**
     * Description: putNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param node <br>
     * @throws CacheException <br>
     */
    void putNode(String nodeName, Map<String, String> node) throws CacheException;

    /**
     * Description: removeNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @throws CacheException <br>
     * @return <br>
     */
    boolean removeNode(String nodeName) throws CacheException;

    /**
     * Description: getValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param key <br>
     * @throws CacheException <br>
     * @return <br>
     */
    String getValue(String nodeName, String key) throws CacheException;

    /**
     * Description: putValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param key <br>
     * @throws CacheException <br>
     * @param t <br>
     */
    void putValue(String nodeName, String key, String t) throws CacheException;

    /**
     * Description: updateValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param key <br>
     * @throws CacheException <br>
     * @param t <br>
     */
    void updateValue(String nodeName, String key, String t) throws CacheException;

    /**
     * Description: removeValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param key <br>
     * @throws CacheException <br>
     */
    void removeValue(String nodeName, String key) throws CacheException;

    /**
     * Description: clean<br>
     * 
     * @author 王伟 <br>
     * @throws CacheException <br>>
     */
    void clean() throws CacheException;
}
