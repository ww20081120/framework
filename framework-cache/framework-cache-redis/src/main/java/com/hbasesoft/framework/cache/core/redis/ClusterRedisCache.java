/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

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

    /** MAX_TIMEOUT */
    private static final int MAX_TIMEOUT = 100000;

    /** DEFAULT_MAX_REDIRECTIONS */
    private static final int DEFAULT_MAX_REDIRECTIONS = 5;

    /** cluster */
    private JedisCluster cluster;

    /**
     * ClusterRedisCache
     */
    public ClusterRedisCache() {
        String cacheModel = PropertyHolder.getProperty("cache.model");
        if (CACHE_MODEL.equals(cacheModel)) {
            Address[] addresses = getAddresses();
            String passwd = CommonUtil.isNotEmpty(addresses) ? addresses[0].getPassword() : null;
            Set<HostAndPort> readSet = new HashSet<HostAndPort>();
            for (Address addr : addresses) {
                HostAndPort hostAndPort = new HostAndPort(addr.getHost(), addr.getPort());
                readSet.add(hostAndPort);
            }
            if (StringUtils.isEmpty(passwd)) {
                cluster = new JedisCluster(readSet,
                    PropertyHolder.getIntProperty("cache.redis.cluster.max.timeout", MAX_TIMEOUT));
            }
            else {
                cluster = new JedisCluster(readSet,
                    PropertyHolder.getIntProperty("cache.redis.cluster.max.timeout", MAX_TIMEOUT),
                    PropertyHolder.getIntProperty("cache.redis.cluster.max.timeout", MAX_TIMEOUT),
                    DEFAULT_MAX_REDIRECTIONS, passwd, new GenericObjectPoolConfig<>());
            }
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
    public byte[] get(final byte[] key) {
        return cluster.get(key);
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
    public void put(final byte[] key, final int seconds, final byte[] value) {
        if (seconds > 0) {
            cluster.setex(key, seconds, value);
        }
        else {
            cluster.set(key, value);
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
    public void remove(final byte[] key) {
        cluster.del(key);
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
    public Map<byte[], byte[]> getNode(final byte[] node) {
        return cluster.hgetAll(node);
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
    public void putNode(final byte[] key, final int seconds, final Map<byte[], byte[]> dataMap) {
        if (MapUtils.isNotEmpty(dataMap)) {
            cluster.hmset(key, dataMap);
            if (seconds > 0) {
                cluster.expire(key, seconds);
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
     * @return <br>
     */
    @Override
    public byte[] getNodeValue(final byte[] nodeName, final byte[] key) {
        return cluster.hget(nodeName, key);
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
    public void putNodeValue(final byte[] nodeName, final int seconds, final byte[] key, final byte[] t) {
        if (t != null) {
            cluster.hset(nodeName, key, t);
            if (seconds > 0) {
                cluster.expire(nodeName, seconds);
            }
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
    public void removeNodeValue(final byte[] nodeName, final byte[] key) {
        cluster.hdel(nodeName, key);
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
    public boolean setnx(final String key, final String value, final int expireTime) {
        if (cluster.setnx(key.getBytes(), value.getBytes()) - 1 == 0) {
            cluster.expire(key.getBytes(), expireTime);
            return true;
        }
        return false;
    }
}
