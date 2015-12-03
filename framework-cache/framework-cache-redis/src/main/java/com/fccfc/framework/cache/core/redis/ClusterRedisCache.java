/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.cache.core.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.redis.clients.jedis.BinaryJedisCluster;
import com.fccfc.framework.cache.core.redis.clients.jedis.HostAndPort;
import com.fccfc.framework.cache.core.util.SerializationUtil;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.PropertyHolder;
import com.fccfc.framework.common.utils.UtilException;
import com.fccfc.framework.common.utils.io.ProtocolUtil.Address;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月2日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.cache.core.redis <br>
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
     * @return <br>
     */
    @Override
    public String getCacheModel() {
        return CACHE_MODEL;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @param nodeName
     * @return
     * @throws CacheException <br>
     */
    @Override
    public <T> Map<String, T> getNode(Class<T> clazz, String nodeName) throws CacheException {
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
        catch (UtilException e) {
            throw new CacheException(e);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public <T> void putNode(String nodeName, Map<String, T> node) throws CacheException {
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
            catch (UtilException e) {
                throw new CacheException(e);
            }
            catch (Exception e) {
                throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public boolean removeNode(String nodeName) throws CacheException {
        try {
            cluster.del(nodeName);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public <T> T getValue(Class<T> clazz, String nodeName, String key) throws CacheException {
        T value = null;
        try {
            value = SerializationUtil.unserial(clazz, cluster.hgetBytes(nodeName, key));
        }
        catch (UtilException e) {
            throw new CacheException(e);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public <T> void putValue(String nodeName, String key, T t) throws CacheException {
        if (t != null) {
            try {
                cluster.hset(nodeName, key, SerializationUtil.serial(t));
            }
            catch (UtilException e) {
                throw new CacheException(e);
            }
            catch (Exception e) {
                throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public void removeValue(String nodeName, String key) throws CacheException {
        try {
            cluster.hdel(nodeName, key);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws CacheException <br>
     */
    @Override
    public void clean() throws CacheException {
        throw new CacheException(ErrorCodeDef.UNSPORT_METHOD_ERROR, "该方法未被实现");
    }

}
