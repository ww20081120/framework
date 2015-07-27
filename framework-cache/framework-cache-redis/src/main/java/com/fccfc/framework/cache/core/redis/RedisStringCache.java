/**
 * 
 */
package com.fccfc.framework.cache.core.redis;

import java.util.Map;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.IStringCache;
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

public class RedisStringCache implements IStringCache {

    private String host;

    private int port;

    /**
     * RedisStringCache
     * 
     * @param host <br>
     * @param port <br>
     */
    public RedisStringCache(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#getNode(java.lang.String)
     */
    @Override
    public Map<String, String> getNode(String nodeName) throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            return jedis.hgetAll(nodeName);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#putNode(java.lang.String, java.util.Map)
     */
    @Override
    public void putNode(String nodeName, Map<String, String> node) throws CacheException {
        if (CommonUtil.isNotEmpty(node)) {
            JedisPool pool = null;
            Jedis jedis = null;
            try {
                pool = RedisConnectionPool.getPool(host, port);
                jedis = pool.getResource();
                jedis.hmset(nodeName, node);
            }
            catch (Exception e) {
                throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
            }
            finally {
                RedisConnectionPool.returnResource(pool, jedis);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#removeNode(java.lang.String)
     */
    @Override
    public boolean removeNode(String nodeName) throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            jedis.del(nodeName);
            return true;
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#getValue(java.lang.String, java.lang.String)
     */
    @Override
    public String getValue(String nodeName, String key) throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            return jedis.hget(nodeName, key);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#putValue(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void putValue(String nodeName, String key, String t) throws CacheException {

        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            if (CommonUtil.isNotEmpty(t)) {
                jedis.hset(nodeName, key, t);
            }
            else {
                jedis.hdel(nodeName, key);
            }
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#updateValue(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public void updateValue(String nodeName, String key, String t) throws CacheException {
        putValue(nodeName, key, t);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#removeValue(java.lang.String, java.lang.String)
     */
    @Override
    public void removeValue(String nodeName, String key) throws CacheException {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = RedisConnectionPool.getPool(host, port);
            jedis = pool.getResource();
            jedis.hdel(nodeName, key);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            RedisConnectionPool.returnResource(pool, jedis);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#clean()
     */
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
