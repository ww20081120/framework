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

    /**
     * @Method getName
     * @param
     * @return java.lang.String
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:34
    */
    String getName();

    /**
     * @Method get
     * @param key
     * @param <T>
     * @return T
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:34
    */
    <T> T get(String key);

    /**
     * @Method put
     * @param key
     * @param t
     * @param <T>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:34
    */
    default <T> void put(String key, T t) {
        put(key, 0, t);
    };

    /**
     * @Method put
     * @param key
     * @param seconds
     * @param t
     * @param <T>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:36
    */
    <T> void put(String key, int seconds, T t);

    /**
     * @Method remove
     * @param key
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:38
    */
    void remove(String key);

    /**
     * @Method getNode
     * @param nodeName
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:38
    */
    default Map<String, String> getNode(String nodeName) {
        return getNode(nodeName, String.class);
    };

    /**
     * @Method getNode
     * @param nodeName
     * @param clazz
     * @param <T>
     * @return java.util.Map<java.lang.String,T>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:38
    */
    <T> Map<String, T> getNode(String nodeName, Class<T> clazz);

    /**
     * @Method putNode
     * @param nodeName
     * @param node
     * @param <T>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:38
    */
    default <T> void putNode(String nodeName, Map<String, T> node) {
        putNode(nodeName, 0, node);
    }

    /**
     * @Method putNode
     * @param nodeName
     * @param seconds
     * @param node
     * @param <T>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:39
    */
    <T> void putNode(String nodeName, int seconds, Map<String, T> node);

    /**
     * @Method getNodeValue
     * @param nodeName
     * @param key
     * @param <T>
     * @return T
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:39
    */
    <T> T getNodeValue(String nodeName, String key);

    /**
     * @Method putNodeValue
     * @param nodeName
     * @param seconds
     * @param key
     * @param t
     * @param <T>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:39
    */
    <T> void putNodeValue(String nodeName, int seconds, String key, T t);

    /**
     * @Method putNodeValue
     * @param nodeName
     * @param key
     * @param t
     * @param  <T>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:39
    */
    default <T> void putNodeValue(String nodeName, String key, T t) {
        putNodeValue(nodeName, 0, key, t);
    };

    /**
     * @Method removeNodeValue
     * @param nodeName
     * @param key
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:40
    */
    void removeNodeValue(String nodeName, String key);

    /**
     * @Method clear
     * @param
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:40
    */
    void clear();

    /**
     * @Method getNativeCache
     * @param
     * @return java.lang.Object
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:40
    */
    Object getNativeCache();

    /**
     * @Method get
     * @param key
     * @return byte[]
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:40
    */
    byte[] get(byte[] key);

    /**
     * @Method put
     * @param key
     * @param seconds
     * @param value
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:40
    */
    void put(byte[] key, int seconds, byte[] value);

    /**
     * @Method remove
     * @param key
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:40
    */
    void remove(byte[] key);

    /**
     * @Method getNode
     * @param node
     * @return java.util.Map<byte[],byte[]>
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:40
    */
    Map<byte[], byte[]> getNode(byte[] node);

    /**
     * @Method putNode
     * @param key
     * @param seconds
     * @param dataMap
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:41
    */
    void putNode(byte[] key, int seconds, Map<byte[], byte[]> dataMap);

    /**
     * @Method getNodeValue
     * @param nodeName
     * @param key
     * @return byte[]
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:41
    */
    byte[] getNodeValue(byte[] nodeName, byte[] key);

    /**
     * @Method putNodeValue
     * @param nodeName
     * @param seconds
     * @param key
     * @param t
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:41
    */
    void putNodeValue(byte[] nodeName, int seconds, byte[] key, byte[] t);

    /**
     * @Method removeNodeValue
     * @param nodeName
     * @param key
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:41
    */
    void removeNodeValue(byte[] nodeName, byte[] key);
}
