/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core;

import java.util.Map;

/**
 * <Description> <br>
 * 缓存接口
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.core.cache <br>
 */
public interface ICache {
    
    String getName();

    <T> T get(String key);

    default <T> void put(String key, T t) {
        put(key, 0, t);
    };

    <T> void put(String key, int seconds, T t);

    void remove(String key);

    default Map<String, String> getNode(String nodeName) {
        return getNode(nodeName, String.class);
    };

    <T> Map<String, T> getNode(String nodeName, Class<T> clazz);

    default <T> void putNode(String nodeName, Map<String, T> node) {
        putNode(nodeName, 0, node);
    }

    <T> void putNode(String nodeName, int seconds, Map<String, T> node);

    <T> T getNodeValue(String nodeName, String key);

    <T> void putNodeValue(String nodeName, int seconds, String key, T t);

    default <T> void putNodeValue(String nodeName, String key, T t) {
        putNodeValue(nodeName, 0, key, t);
    };

    void removeNodeValue(String nodeName, String key);

    void clear();
    
    Object getNativeCache();
}
