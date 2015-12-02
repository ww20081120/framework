/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.cache.core;

import java.util.Map;

/**
 * <Description> <br>
 * 缓存接口
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.fccfc.framework.core.cache <br>
 */
public interface ICache {

    /**
     * Description: 获取cache模式 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String getCacheModel();

    /**
     * Description: 获取节点<br>
     * 
     * @author 王伟 <br>
     * @param nodeName 节点名称
     * @throws CacheException <br>
     * @return <br>
     */
    default Map<String, String> getNode(String nodeName) throws CacheException {
        return getNode(String.class, nodeName);
    };

    /**
     * Description: 获取节点<br>
     * 
     * @author 王伟<br>
     * @param clazz 数据类型
     * @param nodeName 节点名称
     * @return 缓存数据
     * @throws CacheException <br>
     */
    <T> Map<String, T> getNode(Class<T> clazz, String nodeName) throws CacheException;

    /**
     * Description: putNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param node <br>
     * @throws CacheException <br>
     */
    <T> void putNode(String nodeName, Map<String, T> node) throws CacheException;

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
    default String getValue(String nodeName, String key) throws CacheException {
        return getValue(String.class, nodeName, key);
    };

    /**
     * Description: 获取数据<br>
     * 
     * @author 王伟<br>
     * @param clazz 数据类型
     * @param nodeName 节点名称
     * @param key 缓存的key
     * @return 返回类型
     * @throws CacheException <br>
     */
    <T> T getValue(Class<T> clazz, String nodeName, String key) throws CacheException;

    /**
     * Description: putValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param key <br>
     * @param t <br>
     * @throws CacheException <br>
     */
    <T> void putValue(String nodeName, String key, T t) throws CacheException;

    /**
     * Description: updateValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param key <br>
     * @param t <br>
     * @throws CacheException <br>
     */
    default <T> void updateValue(String nodeName, String key, T t) throws CacheException {
        if (t == null) {
            removeValue(nodeName, key);
        }
        else {
            putValue(nodeName, key, t);
        }
    }

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
     * @throws CacheException <br>
     */
    void clean() throws CacheException;
}
