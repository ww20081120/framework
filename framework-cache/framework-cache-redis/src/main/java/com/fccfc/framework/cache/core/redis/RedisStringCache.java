/**
 * 
 */
package com.fccfc.framework.cache.core.redis;

import java.util.Map;

import redis.clients.jedis.Jedis;

import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.IStringCache;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.utils.CommonUtil;

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

    /**
     * 缓存
     */
    private Jedis jedis;

    /**
     * RedisStringCache
     * @param host <br>
     * @param port <br>
     */
    public RedisStringCache(String host, int port) {
        jedis = new Jedis(host, port);
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#getNode(java.lang.String)
     */
    @Override
    public Map<String, String> getNode(String nodeName) throws CacheException {
        try {
            return jedis.hgetAll(nodeName);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#putNode(java.lang.String, java.util.Map)
     */
    @Override
    public void putNode(String nodeName, Map<String, String> node) throws CacheException {
        if (CommonUtil.isNotEmpty(node)) {
            try {
                jedis.hmset(nodeName, node);
            }
            catch (Exception e) {
                throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#removeNode(java.lang.String)
     */
    @Override
    public boolean removeNode(String nodeName) throws CacheException {
        try {
            jedis.del(nodeName);
            return true;
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#getValue(java.lang.String, java.lang.String)
     */
    @Override
    public String getValue(String nodeName, String key) throws CacheException {
        try {
            return jedis.hget(nodeName, key);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#putValue(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void putValue(String nodeName, String key, String t) throws CacheException {

        try {
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
        try {
            jedis.hdel(nodeName, key);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.cache.core.IStringCache#clean()
     */
    @Override
    public void clean() throws CacheException {
        try {
            jedis.flushAll();
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
    }

}
