/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core;

import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * <Description> <br>
 * 缓存接口
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.core.cache <br>
 */
public interface ICache extends Cache {

    default ValueWrapper get(Object key) {
        return new SimpleValueWrapper(get(key, String.class));
    }

    /**
     * Description: 获取节点<br>
     * 
     * @author 王伟 <br>
     * @param nodeName 节点名称
     * @return <br>
     */
    default Map<String, String> getNode(String nodeName) {
        return getNode(nodeName, String.class);
    };

    /**
     * Description: 获取节点<br>
     * 
     * @author 王伟<br>
     * @param clazz 数据类型
     * @param nodeName 节点名称
     * @return 缓存数据
     */
    <T> Map<String, T> getNode(String nodeName, Class<T> clazz);

    /**
     * Description: putNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param node <br>
     */
    <T> void putNode(String nodeName, Map<String, T> node);

    /**
     * Description: putNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param expireTimes <br>
     * @param node <br>
     */
    <T> void putNode(String nodeName, long expireTimes, Map<String, T> node);

    /**
     * Description: removeNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @return <br>
     */
    boolean removeNode(String nodeName);

    /**
     * Description: getValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param key <br>
     * @return <br>
     */
    default String get(String nodeName, String key) {
        return get(nodeName, key, String.class);
    };

    /**
     * Description: 获取数据<br>
     * 
     * @author 王伟<br>
     * @param clazz 数据类型
     * @param nodeName 节点名称
     * @param key 缓存的key
     * @return 返回类型
     */
    <T> T get(String nodeName, String key, Class<T> clazz);

    /**
     * Description: putValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param key <br>
     * @param t <br>
     */
    <T> void put(String nodeName, String key, T t);

    /**
     * Description: putValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param expireTimes <br>
     * @param key <br>
     * @param t <br>
     */
    <T> void put(String nodeName, long expireTimes, String key, T t);

    /**
     * Description: removeValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName <br>
     * @param key <br>
     */
    void evict(String nodeName, String key);

    @Override
    default ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper vrapper = get(key);
        if (vrapper == null) {
            put(key, value);
        }
        return vrapper;
    }
}
