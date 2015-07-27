/**
 * 
 */
package com.fccfc.framework.cache.core.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fccfc.framework.cache.core.AbstractCache;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.utils.CommonUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <Description> <br>
 * 
 * @author xgf<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月2日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.core.cache.redis <br>
 */

public class RedisCache extends AbstractCache {

    /**
     * 缓存模式
     */
    public static final String CACHE_MODEL = "REDIS";

    private String host;

    private int port;

    /**
     * RedisCache
     * 
     * @param host <br>
     * @param port <br>
     */
    public RedisCache(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Map<String, Object> getNode(String nodeName) throws CacheException {
        byte[] bNodeName = nodeName.getBytes();
        Map<String, Object> map = null;
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            Map<byte[], byte[]> hmap = jedis.hgetAll(bNodeName);
            if (CommonUtil.isNotEmpty(hmap)) {
                map = new HashMap<String, Object>();
                for (Entry<byte[], byte[]> entry : hmap.entrySet()) {
                    map.put(new String(entry.getKey()), unserial(entry.getValue()));
                }
            }
            return map;
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    @Override
    public void putNode(String nodeName, Map<String, Object> node) throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            if (CommonUtil.isNotEmpty(node)) {
                pool = RedisConnectionPool.getPool(host, port);
                jedis = pool.getResource();
                Map<byte[], byte[]> hmap = new HashMap<byte[], byte[]>();
                for (Entry<String, Object> entry : node.entrySet()) {
                    byte[] value = serial(entry.getValue());
                    if (value != null) {
                        hmap.put(entry.getKey().getBytes(), value);
                    }
                }
                jedis.hmset(nodeName.getBytes(), hmap);
            }
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    @Override
    public boolean removeNode(String nodeName) throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            jedis.del(nodeName.getBytes());
            return true;
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    @Override
    public Object getValue(String nodeName, String key) throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            byte[] data = jedis.hget(nodeName.getBytes(), key.getBytes());
            return unserial(data);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    @Override
    public void putValue(String nodeName, String key, Object t) throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            byte[] data = serial(t);
            if (data != null && data.length > 0) {
                jedis.hset(nodeName.getBytes(), key.getBytes(), data);
            }
            else {
                jedis.hdel(nodeName.getBytes(), key.getBytes());
            }
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    @Override
    public void updateValue(String nodeName, String key, Object t) throws CacheException {
        putValue(nodeName, key, t);
    }

    @Override
    public void removeValue(String nodeName, String key) throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            jedis.hdel(nodeName.getBytes(), key.getBytes());
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    @Override
    public void clean() throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            jedis.flushAll();
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

}
