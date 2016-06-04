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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisShardInfo;

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

    private JedisPool jedisPool;

    public RedisCache() {
        String cacheModel = PropertyHolder.getProperty("cache.model");
        if (CACHE_MODEL.equals(cacheModel)) {
            Address[] addresses = getAddresses();
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(addresses.length);
            for (Address addr : addresses) {
                shards.add(new JedisShardInfo(addr.getHost(), addr.getPort()));
            }
            jedisPool = new JedisPool(getConfig(), addresses[0].getHost(), addresses[0].getPort());
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
        Jedis jedis = null;
        Map<String, T> map = null;
        try {
            jedis = jedisPool.getResource();
            Map<byte[], byte[]> hmap = jedis.hgetAll(nodeName.getBytes());
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
            jedisPool.returnResourceObject(jedis);
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
            Jedis jedis = null;
            Map<byte[], byte[]> hmap = new HashMap<byte[], byte[]>();
            try {
                jedis = jedisPool.getResource();
                for (Entry<String, T> entry : node.entrySet()) {
                    byte[] value = SerializationUtil.serial(entry.getValue());
                    if (value != null) {
                        hmap.put(entry.getKey().getBytes(), value);
                    }
                }
                jedis.hmset(nodeName.getBytes(), hmap);
            }
            catch (Exception e) {
                throw new RuntimeException("serial map failed!", e);
            }
            finally {
                jedisPool.returnResourceObject(jedis);
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(nodeName.getBytes());
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        finally {
            jedisPool.returnResourceObject(jedis);
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            value = SerializationUtil.unserial(clazz, jedis.hget(nodeName.getBytes(), key.getBytes()));
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        finally {
            jedisPool.returnResourceObject(jedis);
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
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.hset(nodeName.getBytes(), key.getBytes(), SerializationUtil.serial(t));
            }
            catch (Exception e) {
                throw new RuntimeException("serial map failed!", e);
            }
            finally {
                jedisPool.returnResourceObject(jedis);
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(nodeName.getBytes(), key.getBytes());
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        finally {
            jedisPool.returnResourceObject(jedis);
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
            jedisPool.returnResourceObject(jedis);
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
     * @param type
     * @return <br>
     */
    @Override
    public <T> T get(Object key, Class<T> type) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] data = jedis.get(key.toString().getBytes());
            return SerializationUtil.unserial(type, data);
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        finally {
            jedisPool.returnResourceObject(jedis);
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key.toString().getBytes(), SerializationUtil.serial(value));
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        finally {
            jedisPool.returnResourceObject(jedis);
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key.toString().getBytes());
        }
        catch (Exception e) {
            throw new RuntimeException("serial map failed!", e);
        }
        finally {
            jedisPool.returnResourceObject(jedis);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param expireTimes
     * @param node <br>
     */
    @Override
    public <T> void putNode(String nodeName, long expireTimes, Map<String, T> node) {
        if (CommonUtil.isNotEmpty(node)) {
            Jedis jedis = null;
            Map<byte[], byte[]> hmap = new HashMap<byte[], byte[]>();
            try {
                jedis = jedisPool.getResource();
                for (Entry<String, T> entry : node.entrySet()) {
                    byte[] value = SerializationUtil.serial(entry.getValue());
                    if (value != null) {
                        hmap.put(entry.getKey().getBytes(), value);
                    }
                }
                jedis.expire(nodeName.getBytes(), new Long(expireTimes).intValue());
                jedis.hmset(nodeName.getBytes(), hmap);
            }
            catch (Exception e) {
                throw new RuntimeException("serial map failed!", e);
            }
            finally {
                jedisPool.returnResourceObject(jedis);
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param expireTimes
     * @param key
     * @param t <br>
     */
    @Override
    public <T> void put(String nodeName, long expireTimes, String key, T t) {
        if (t != null) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.expire(nodeName.getBytes(), new Long(expireTimes).intValue());
                jedis.hset(nodeName.getBytes(), key.getBytes(), SerializationUtil.serial(t));
            }
            catch (Exception e) {
                throw new RuntimeException("serial map failed!", e);
            }
            finally {
                jedisPool.returnResourceObject(jedis);
            }
        }
    }

}
