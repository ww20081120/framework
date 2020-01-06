/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;

/**
 * <Description> <br>
 * 
 * @author liuxianan<br>
 * @version 2.0<br>
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

    private JedisPool jedisPool;

    public RedisCache() {
        String cacheModel = PropertyHolder.getProperty("cache.model");
        if (CACHE_MODEL.equals(cacheModel)) {
            Address[] addresses = getAddresses();
            String passwd = CommonUtil.isNotEmpty(addresses) ? addresses[0].getPassword() : null;
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(addresses.length);
            for (Address addr : addresses) {
                shards.add(new JedisShardInfo(addr.getHost(), addr.getPort()));
            }
            if (CommonUtil.isEmpty(passwd)) {
                jedisPool = new JedisPool(getConfig(), addresses[0].getHost(), addresses[0].getPort());
            } else {
                jedisPool = new JedisPool(getConfig(),  addresses[0].getHost(),
                        addresses[0].getPort(), Protocol.DEFAULT_TIMEOUT, passwd, Protocol.DEFAULT_DATABASE, null);
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
    protected byte[] get(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
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
    protected void put(byte[] key, byte[] value) {
        if (value != null && value.length > 0) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.set(key, value);
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
    protected void evict(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
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
    protected Map<byte[], byte[]> getNode(byte[] node) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
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
    protected void putNode(byte[] key, Map<byte[], byte[]> dataMap) {
        if (CommonUtil.isNotEmpty(dataMap)) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.hmset(key, dataMap);
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
     * @return <br>
     */
    @Override
    protected void removeNode(byte[] nodeName) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(nodeName);
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
     * @return <br>
     */
    @Override
    protected byte[] get(byte[] nodeName, byte[] key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
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
    protected void put(byte[] nodeName, int seconds, byte[] key, byte[] t) {
        if (t != null) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.hset(nodeName, key, t);
                if (seconds > 0) {
                    jedis.expire(new String(nodeName), seconds);
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
    protected void evict(byte[] nodeName, byte[] key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
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
    public boolean setnx(String key, String value, int expireTime) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
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
