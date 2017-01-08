/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hbasesoft.framework.cache.core.redis.clients.jedis.BinaryJedisCluster;
import com.hbasesoft.framework.cache.core.redis.clients.jedis.HostAndPort;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
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
     * @return <br>
     */
    @Override
    protected byte[] get(byte[] key) {
        return cluster.getBytes(new String(key));
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
    protected void put(byte[] key, byte[] value) {
        cluster.set(new String(key), value);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     */
    @Override
    protected void evict(byte[] key) {
        cluster.del(new String(key));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param node
     * @return <br>
     */
    @Override
    protected Map<byte[], byte[]> getNode(byte[] node) {
        return cluster.hgetAllBytes(new String(node));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param dataMap <br>
     */
    @Override
    protected void putNode(byte[] key, Map<byte[], byte[]> dataMap) {
        if (CommonUtil.isNotEmpty(dataMap)) {
            cluster.hmsetBytes(new String(key), dataMap);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @return <br>
     */
    @Override
    protected void removeNode(byte[] nodeName) {
        cluster.del(new String(nodeName));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key
     * @return <br>
     */
    @Override
    protected byte[] get(byte[] nodeName, byte[] key) {
        return cluster.hgetBytes(new String(nodeName), new String(key));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key
     * @param t <br>
     */
    @Override
    protected void put(byte[] nodeName, byte[] key, byte[] t) {
        if (t != null) {
            cluster.hset(new String(nodeName), new String(key), t);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key <br>
     */
    @Override
    protected void evict(byte[] nodeName, byte[] key) {
        cluster.hdel(new String(nodeName), new String(key));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param value
     * @param expireTime
     * @return <br>
     */
    @Override
    public boolean setnx(String key, String value, int expireTime) {
        if (cluster.setnx(key, value) - 1 == 0) {
            cluster.expire(key, expireTime);
            return true;
        }
        return false;
    }
}
