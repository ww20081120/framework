/**
 * 
 */
package com.hbasesoft.framework.cache.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.commons.collections.MapUtils;

import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.hbasesoft.framework.cache.core <br>
 */
public abstract class AbstractCache implements ICache {

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
    public <T> T get(final Object key, final Class<T> type) {
        byte[] datas = get(key.toString().getBytes());
        return getValue(datas);
    }

    private <T> T getValue(final byte[] datas) {
        try {
            CacheObject cacheObj = SerializationUtil.unserial(CacheObject.class, datas);
            if (cacheObj != null) {
                return cacheObj.getTarget();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("unserial failed!", e);
        }
        return null;
    }

    private byte[] getData(final Object value) {
        try {
            return SerializationUtil.serial(new CacheObject(value));
        }
        catch (Exception e) {
            throw new RuntimeException("serial failed!", e);
        }
    }

    private byte[] getData(final int seconds, final Object value) {
        try {
            return SerializationUtil.serial(new CacheObject(seconds, value));
        }
        catch (Exception e) {
            throw new RuntimeException("serial failed!", e);
        }
    }

    protected abstract byte[] get(byte[] key);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param value <br>
     */
    @Override
    public void put(final Object key, final Object value) {
        byte[] keys = key.toString().getBytes();
        put(keys, getData(value));
    }

    protected abstract void put(byte[] key, byte[] value);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     */
    @Override
    public void evict(final Object key) {
        evict(key.toString().getBytes());
    }

    protected abstract void evict(byte[] key);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param clazz
     * @return <br>
     */
    @Override
    public <T> Map<String, T> getNode(final String nodeName, final Class<T> clazz) {
        Map<byte[], byte[]> dataMap = getNode(nodeName.getBytes());
        Map<String, T> map = null;
        if (MapUtils.isNotEmpty(dataMap)) {
            map = new HashMap<String, T>();
            for (Entry<byte[], byte[]> entry : dataMap.entrySet()) {
                map.put(new String(entry.getKey()), getValue(entry.getValue()));
            }
        }
        return map;
    }

    protected abstract Map<byte[], byte[]> getNode(byte[] node);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param node <br>
     */
    @Override
    public <T> void putNode(final String nodeName, final Map<String, T> node) {
        Map<byte[], byte[]> hmap = new HashMap<byte[], byte[]>();
        for (Entry<String, T> entry : node.entrySet()) {
            byte[] value = getData(entry.getValue());
            if (value != null) {
                hmap.put(entry.getKey().getBytes(), value);
            }
        }
        putNode(nodeName.getBytes(), hmap);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName 节点名称
     * @param seconds 超时秒数
     * @param node 节点数据<br>
     */
    @Override
    public <T> void putNode(final String nodeName, final int seconds, final Map<String, T> node) {
        Map<byte[], byte[]> hmap = new HashMap<byte[], byte[]>();
        for (Entry<String, T> entry : node.entrySet()) {
            byte[] value = getData(seconds, entry.getValue());
            if (value != null) {
                hmap.put(entry.getKey().getBytes(), value);
            }
        }
    }

    protected abstract void putNode(byte[] key, Map<byte[], byte[]> dataMap);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName <br>
     */
    @Override
    public void removeNode(final String nodeName) {
        removeNode(nodeName.getBytes());
    }

    protected abstract void removeNode(byte[] nodeName);

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
    public <T> T get(final String nodeName, final String key) {
        byte[] datas = get(nodeName.getBytes(), key.getBytes());
        return getValue(datas);
    }

    protected abstract byte[] get(byte[] nodeName, byte[] key);

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
    public <T> void put(final String nodeName, final String key, final T t) {
        put(nodeName.getBytes(), 0, key.getBytes(), getData(t));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param seconds
     * @param key
     * @param t <br>
     */
    @Override
    public <T> void put(final String nodeName, final int seconds, final String key, final T t) {
        put(nodeName.getBytes(), seconds, key.getBytes(), getData(seconds, t));
    }

    protected abstract void put(byte[] nodeName, int seconds, byte[] key, byte[] t);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param nodeName
     * @param key <br>
     */
    @Override
    public void evict(final String nodeName, final String key) {
        evict(nodeName.getBytes(), key.getBytes());
    }

    protected abstract void evict(byte[] nodeName, byte[] key);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param valueLoader
     * @return <br>
     */
    @Override
    public <T> T get(final Object key, final Callable<T> valueLoader) {
        T result = get(CacheConstant.DEFAULT_CACHE_DIR, key.toString());
        if (result == null) {
            try {
                result = valueLoader.call();
                put(CacheConstant.DEFAULT_CACHE_DIR, key.toString(), result);
            }
            catch (Exception e) {
                LoggerUtil.error(e);
            }
        }
        return result;
    }

}
