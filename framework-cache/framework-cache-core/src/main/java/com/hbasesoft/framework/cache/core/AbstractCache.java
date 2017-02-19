/**
 * 
 */
package com.hbasesoft.framework.cache.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import com.hbasesoft.framework.cache.core.util.SerializationUtil;
import com.hbasesoft.framework.common.utils.CommonUtil;
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
    public <T> T get(Object key, Class<T> type) {
        byte[] datas = get(key.toString().getBytes());
        return getValue(datas);
    }

    private <T> T getValue(byte[] datas) {
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

    private byte[] getData(Object value) {
        try {
            return SerializationUtil.serial(new CacheObject(value));
        }
        catch (Exception e) {
            throw new RuntimeException("serial failed!", e);
        }
    }

    private byte[] getData(int seconds, Object value) {
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
    public void put(Object key, Object value) {
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
    public void evict(Object key) {
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
    public <T> Map<String, T> getNode(String nodeName, Class<T> clazz) {
        Map<byte[], byte[]> dataMap = getNode(nodeName.getBytes());
        Map<String, T> map = null;
        if (CommonUtil.isNotEmpty(dataMap)) {
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
    public <T> void putNode(String nodeName, Map<String, T> node) {
        Map<byte[], byte[]> hmap = new HashMap<byte[], byte[]>();
        for (Entry<String, T> entry : node.entrySet()) {
            byte[] value = getData(entry.getValue());
            if (value != null) {
                hmap.put(entry.getKey().getBytes(), value);
            }
        }
    }

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
    public <T> void putNode(String nodeName, int seconds, Map<String, T> node) {
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
     * @param nodeName
     * @return <br>
     */
    @Override
    public void removeNode(String nodeName) {
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
     * @param clazz
     * @return <br>
     */
    @Override
    public <T> T get(String nodeName, String key) {
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
    public <T> void put(String nodeName, String key, T t) {
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
    public <T> void put(String nodeName, int seconds, String key, T t) {
        put(nodeName.getBytes(), seconds * 10, key.getBytes(), getData(seconds, t));
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
    public void evict(String nodeName, String key) {
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
    public <T> T get(Object key, Callable<T> valueLoader) {
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
