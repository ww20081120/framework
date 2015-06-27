/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
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
     * Description: 获取节点<br>
     * 
     * @author 王伟 <br>
     * @param nodeName 节点名称
     * @return <br>
     */
    Map<String, Object> getNode(String nodeName) throws CacheException;

    /**
     * Description: putNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @param node <br>
     */
    void putNode(String nodeName, Map<String, Object> node) throws CacheException;

    /**
     * Description: removeNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @return <br>
     */
    boolean removeNode(String nodeName) throws CacheException;

    /**
     * Description: getValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @param key
     * @return <br>
     */
    Object getValue(String nodeName, String key) throws CacheException;

    /**
     * Description: putValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @param key
     * @param t <br>
     */
    void putValue(String nodeName, String key, Object t) throws CacheException;

    /**
     * Description: updateValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @param key
     * @param t <br>
     */
    void updateValue(String nodeName, String key, Object t) throws CacheException;

    /**
     * Description: removeValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @param key
     */
    void removeValue(String nodeName, String key) throws CacheException;

    /**
     * Description: clean<br>
     * 
     * @author 王伟 <br>
     * <br>
     */
    void clean() throws CacheException;
}
