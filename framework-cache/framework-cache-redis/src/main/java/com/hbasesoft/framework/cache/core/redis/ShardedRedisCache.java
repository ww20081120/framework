/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

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
public class ShardedRedisCache extends AbstractRedisCache {

    /**
     * 缓存模式
     */
    public static final String CACHE_MODEL = "REDIS_SHARDED";

    /** shrdedPool */
    private ShardedJedisPool shardedPool;

    /**
     * ShardedRedisCache
     */
    public ShardedRedisCache() {
        String cacheModel = PropertyHolder.getProperty("cache.model");
        if (CACHE_MODEL.equals(cacheModel)) {
            Address[] addresses = getAddresses();
            String passwd = CommonUtil.isNotEmpty(addresses) ? addresses[0].getPassword() : null;
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(addresses.length);
            for (Address addr : addresses) {
                JedisShardInfo jedisShardInfo = new JedisShardInfo(addr.getHost(), addr.getPort());
                jedisShardInfo.setPassword(passwd);
                shards.add(jedisShardInfo);
            }

            GenericObjectPoolConfig<ShardedJedis> config = new GenericObjectPoolConfig<>();
            super.setConfig(config);
            shardedPool = new ShardedJedisPool(config, shards);
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
        return shardedPool;
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            return shardedJedis.get(key);
        }
        finally {
            if (shardedJedis != null) {
                shardedJedis.close();
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
    public void put(final byte[] key, int seconds, final byte[] value) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            if (seconds > 0) {
                shardedJedis.setex(key, seconds, value);
            }
            else {
                shardedJedis.set(key, value);
            }
        }
        finally {
            if (shardedJedis != null) {
                shardedJedis.close();
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.del(key);
        }
        finally {
            if (shardedJedis != null) {
                shardedJedis.close();
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            return shardedJedis.hgetAll(node);
        }
        finally {
            if (shardedJedis != null) {
                shardedJedis.close();
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
    public void putNode(final byte[] key, int seconds, final Map<byte[], byte[]> dataMap) {
        if (MapUtils.isNotEmpty(dataMap)) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedPool.getResource();
                shardedJedis.hmset(key, dataMap);
                if (seconds > 0) {
                    shardedJedis.expire(key, seconds);
                }
            }
            finally {
                if (shardedJedis != null) {
                    shardedJedis.close();
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            return shardedJedis.hget(nodeName, key);
        }
        finally {
            if (shardedJedis != null) {
                shardedJedis.close();
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
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedPool.getResource();
                shardedJedis.hset(nodeName, key, t);
                if (seconds > 0) {
                    shardedJedis.expire(key, seconds);
                }
            }
            finally {
                if (shardedJedis != null) {
                    shardedJedis.close();
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.hdel(nodeName, key);
        }
        finally {
            if (shardedJedis != null) {
                shardedJedis.close();
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            if (shardedJedis.setnx(key, value) - 1 == 0) {
                shardedJedis.expire(key, expireTime);
                return true;
            }
        }
        finally {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }
        return false;
    }

}
