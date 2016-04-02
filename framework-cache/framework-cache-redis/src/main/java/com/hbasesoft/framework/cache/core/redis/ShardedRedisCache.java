/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hbasesoft.framework.cache.core.util.SerializationUtil;
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
     * @param clazz
     * @param nodeName
     * @return
     */
    @Override
    public <T> Map<String, T> getNode(String nodeName, Class<T> clazz) {
        ShardedJedis shardedJedis = null;
        Map<String, T> map = null;
        try {
            shardedJedis = shardedPool.getResource();
            Map<byte[], byte[]> hmap = shardedJedis.hgetAll(nodeName.getBytes());
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
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
     */
    @Override
    public <T> void putNode(String nodeName, Map<String, T> node) {
        if (CommonUtil.isNotEmpty(node)) {
            ShardedJedis shardedJedis = null;
            Map<byte[], byte[]> hmap = new HashMap<byte[], byte[]>();
            try {
                shardedJedis = shardedPool.getResource();
                for (Entry<String, T> entry : node.entrySet()) {
                    byte[] value = SerializationUtil.serial(entry.getValue());
                    if (value != null) {
                        hmap.put(entry.getKey().getBytes(), value);
                    }
                }
                shardedJedis.hmset(nodeName.getBytes(), hmap);
            }
            catch (Exception e) {
                throw new RuntimeException("serial map failed!", e);
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
     * @return
     */
    @Override
    public boolean removeNode(String nodeName) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.del(nodeName.getBytes());
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
     */
    @Override
    public <T> T get(String nodeName, String key, Class<T> clazz) {
        T value = null;
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            value = SerializationUtil.unserial(clazz, shardedJedis.hget(nodeName.getBytes(), key.getBytes()));
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
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
     */
    @Override
    public <T> void put(String nodeName, String key, T t) {
        if (t != null) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedPool.getResource();
                shardedJedis.hset(nodeName.getBytes(), key.getBytes(), SerializationUtil.serial(t));
            }
            catch (Exception e) {
                throw new RuntimeException("serial map failed!", e);
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
     * @param key
     */
    @Override
    public void evict(String nodeName, String key) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.hdel(nodeName.getBytes(), key.getBytes());
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
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
     * @param type
     * @return <br>
     */
    @Override
    public <T> T get(Object key, Class<T> type) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            return SerializationUtil.unserial(type, shardedJedis.get(key.toString().getBytes()));
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
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
    public void put(Object key, Object value) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.set(shardedJedis.get(key.toString().getBytes()), SerializationUtil.serial(value));
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
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
    public void evict(Object key) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedPool.getResource();
            shardedJedis.del(key.toString().getBytes());
        }
        finally {
            shardedPool.returnResourceObject(shardedJedis);
        }
    }

}
