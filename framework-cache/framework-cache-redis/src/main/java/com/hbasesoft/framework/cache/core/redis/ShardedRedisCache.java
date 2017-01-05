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

    private ShardedJedisPool shardedPool;

    public ShardedRedisCache() {
        String cacheModel = PropertyHolder.getProperty("cache.model");
        if (CACHE_MODEL.equals(cacheModel)) {
            Address[] addresses = getAddresses();
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(addresses.length);
            for (Address addr : addresses) {
                shards.add(new JedisShardInfo(addr.getHost(), addr.getPort()));
            }
            shardedPool = new ShardedJedisPool(getConfig(), shards);
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
    protected byte[] get(byte[] key) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            return shardedJedis.get(key);
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.set(key, value);
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.del(key);
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            return shardedJedis.hgetAll(node);
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedPool.getResource();
                shardedJedis.hmset(key, dataMap);
            }
            finally {
                shardedPool.returnResourceObject(shardedJedis);
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.del(nodeName);
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            return shardedJedis.hget(nodeName, key);
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
    protected void put(byte[] nodeName, byte[] key, byte[] t) {
        if (t != null) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedPool.getResource();
                shardedJedis.hset(nodeName, key, t);
            }
            finally {
                shardedPool.returnResourceObject(shardedJedis);
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.hdel(nodeName, key);
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            if (shardedJedis.setnx(key, value) - 1 == 0) {
                shardedJedis.expire(key, expireTime);
                return true;
            }
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
        }
        return false;
    }

}
