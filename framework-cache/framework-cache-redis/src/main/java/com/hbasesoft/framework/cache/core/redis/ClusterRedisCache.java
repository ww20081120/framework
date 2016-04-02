/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hbasesoft.framework.cache.core.redis.clients.jedis.BinaryJedisCluster;
import com.hbasesoft.framework.cache.core.redis.clients.jedis.HostAndPort;
import com.hbasesoft.framework.cache.core.util.SerializationUtil;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core.redis <br>
 */
public class ClusterRedisCache extends AbstractRedisCache {

    /**
     * 缓存模式
     */
    public static final String CACHE_MODEL = "REDIS_CLUSTER";

    private BinaryJedisCluster cluster;

    public ClusterRedisCache() {
        String cacheModel = PropertyHolder.getProperty("cache.model");
        if (CACHE_MODEL.equals(cacheModel)) {
            Address[] addresses = getAddresses();
            Set<HostAndPort> readSet = new HashSet<HostAndPort>();
            for (Address addr : addresses) {
                HostAndPort hostAndPort = new HostAndPort(addr.getHost(), addr.getPort());
                readSet.add(hostAndPort);
            }
            cluster = new BinaryJedisCluster(readSet,
                PropertyHolder.getIntProperty("cache.redis.cluster.max.timeout", 100000),
                PropertyHolder.getIntProperty("cache.redis.cluster.max.redirections", 10));
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @param nodeName
     * @return
     * @ <br>
     */
    @Override
    public <T> Map<String, T> getNode(String nodeName, Class<T> clazz) {
        Map<String, T> map = null;
        try {
            Map<byte[], byte[]> hmap = cluster.hgetAllBytes(nodeName);
            if (CommonUtil.isNotEmpty(hmap)) {
                map = new HashMap<String, T>();
                for (Entry<byte[], byte[]> entry : hmap.entrySet()) {
                    map.put(new String(entry.getKey()), SerializationUtil.unserial(clazz, entry.getValue()));
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }

        return map;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param node
     * @ <br>
     */
    @Override
    public <T> void putNode(String nodeName, Map<String, T> node) {
        if (CommonUtil.isNotEmpty(node)) {
            Map<byte[], byte[]> hmap = new HashMap<byte[], byte[]>();
            try {
                for (Entry<String, T> entry : node.entrySet()) {
                    byte[] value = SerializationUtil.serial(entry.getValue());
                    if (value != null) {
                        hmap.put(entry.getKey().getBytes(), value);
                    }
                }
                cluster.hmsetBytes(nodeName, hmap);
            }
            catch (Exception e) {
                throw new RuntimeException("serial map failed!", e);
            }
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @return
     * @ <br>
     */
    @Override
    public boolean removeNode(String nodeName) {
        try {
            cluster.del(nodeName);
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        return true;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @param nodeName
     * @param key
     * @return
     * @ <br>
     */
    @Override
    public <T> T get(String nodeName, String key, Class<T> clazz) {
        T value = null;
        try {
            value = SerializationUtil.unserial(clazz, cluster.hgetBytes(nodeName, key));
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        return value;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key
     * @param t
     * @ <br>
     */
    @Override
    public <T> void put(String nodeName, String key, T t) {
        if (t != null) {
            try {
                cluster.hset(nodeName, key, SerializationUtil.serial(t));
            }
            catch (Exception e) {
                throw new RuntimeException("serial map failed!", e);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key
     * @ <br>
     */
    @Override
    public void evict(String nodeName, String key) {
        cluster.hdel(nodeName, key);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @ <br>
     */
    @Override
    public void clear() {
        throw new RuntimeException("该方法未被实现");
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
        return cluster;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param type
     * @return <br>
     */
    @Override
    public <T> T get(Object key, Class<T> type) {
        try {
            return SerializationUtil.unserial(type, cluster.getBytes(key.toString()));
        }
        catch (UtilException e) {
            throw new RuntimeException("serial map failed!", e);
        }
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
    public void put(Object key, Object value) {
        try {
            cluster.set(key.toString(), SerializationUtil.serial(value));
        }
        catch (UtilException e) {
            throw new RuntimeException("serial map failed!", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     */
    @Override
    public void evict(Object key) {
        cluster.del(key.toString());
    }

}
