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

import com.hbasesoft.framework.cache.core.CacheException;
import com.hbasesoft.framework.cache.core.util.SerializationUtil;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;
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
     * @return <br>
     */
    @Override
    public String getCacheModel() {
        return CACHE_MODEL;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @param nodeName
     * @return
     * @throws CacheException <br>
     */
    @Override
    public <T> Map<String, T> getNode(Class<T> clazz, String nodeName) throws CacheException {
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
        catch (UtilException e) {
            throw new CacheException(e);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public <T> void putNode(String nodeName, Map<String, T> node) throws CacheException {
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
            catch (UtilException e) {
                throw new CacheException(e);
            }
            catch (Exception e) {
                throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public boolean removeNode(String nodeName) throws CacheException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(nodeName.getBytes());
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public <T> T getValue(Class<T> clazz, String nodeName, String key) throws CacheException {
        T value = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            value = SerializationUtil.unserial(clazz, jedis.hget(nodeName.getBytes(), key.getBytes()));
        }
        catch (UtilException e) {
            throw new CacheException(e);
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public <T> void putValue(String nodeName, String key, T t) throws CacheException {
        if (t != null) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.hset(nodeName.getBytes(), key.getBytes(), SerializationUtil.serial(t));
            }
            catch (UtilException e) {
                throw new CacheException(e);
            }
            catch (Exception e) {
                throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public void removeValue(String nodeName, String key) throws CacheException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(nodeName.getBytes(), key.getBytes());
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
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
     * @throws CacheException <br>
     */
    @Override
    public void clean() throws CacheException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.flushAll();
        }
        catch (Exception e) {
            throw new CacheException(ErrorCodeDef.CACHE_ERROR_10002, "serial map failed!", e);
        }
        finally {
            jedisPool.returnResourceObject(jedis);
        }
    }

}
