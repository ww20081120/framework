/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

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
public class RedisCache extends AbstractRedisCache {

    /**
     * 缓存模式
     */
    public static final String CACHE_MODEL = "REDIS";

    /** jeditPool */
    private JedisPool jedisPool;

    /**
     * redisCache
     */
    public RedisCache() {
        String cacheModel = PropertyHolder.getProperty("cache.model");
        if (CACHE_MODEL.equals(cacheModel)) {
            Address[] addresses = getAddresses();
            String passwd = ArrayUtils.isNotEmpty(addresses) ? addresses[0].getPassword() : null;
            JedisPoolConfig config = new JedisPoolConfig();
            super.setConfig(config);
            if (StringUtils.isEmpty(passwd)) {
                jedisPool = new JedisPool(config, addresses[0].getHost(), addresses[0].getPort());
            }
            else {
                jedisPool = new JedisPool(config, addresses[0].getHost(), addresses[0].getPort(),
                    Protocol.DEFAULT_TIMEOUT, passwd, Protocol.DEFAULT_DATABASE, null);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void clear() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(super.getDbIndex());
            jedis.flushAll();
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
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
        return jedisPool;
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(super.getDbIndex());
            return jedis.get(key);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
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
    public void put(final byte[] key, final int seconds, final byte[] value) {
        if (value != null && value.length > 0) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.select(super.getDbIndex());
                if (seconds > 0) {
                    jedis.setex(key, seconds, value);
                }
                else {
                    jedis.set(key, value);
                }
            }
            finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(super.getDbIndex());
            jedis.del(key);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(super.getDbIndex());
            return jedis.hgetAll(node);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
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
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.select(super.getDbIndex());
                jedis.hmset(key, dataMap);
                if (seconds > 0) {
                    jedis.expire(key, seconds);
                }
            }
            finally {
                if (jedis != null) {
                    jedis.close();
                }
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(super.getDbIndex());
            return jedis.hget(nodeName, key);
        }
        finally {
            if (jedis != null) {
                jedis.close();
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
     * @param t <br>
     */
    @Override
    public void putNodeValue(final byte[] nodeName, final int seconds, final byte[] key, final byte[] t) {
        if (t != null) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.select(super.getDbIndex());
                jedis.hset(nodeName, key, t);
                if (seconds > 0) {
                    jedis.expire(nodeName, seconds);
                }
            }
            finally {
                if (jedis != null) {
                    jedis.close();
                }
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(super.getDbIndex());
            jedis.hdel(nodeName, key);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(super.getDbIndex());
            if (jedis.setnx(key, value) - 1 == 0) {
                jedis.expire(key, expireTime);
                return true;
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

}
