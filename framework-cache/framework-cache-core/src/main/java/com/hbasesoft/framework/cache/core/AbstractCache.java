/**
 * 
 */
package com.hbasesoft.framework.cache.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;

import com.hbasesoft.framework.common.utils.bean.SerializationUtil;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.hbasesoft.framework.cache.core <br>
 */
public abstract class AbstractCache implements ICache {

    protected abstract byte[] get(byte[] key);

    protected abstract void put(byte[] key, int seconds, byte[] value);

    protected abstract void remove(byte[] key);

    protected abstract Map<byte[], byte[]> getNode(byte[] node);

    protected abstract void putNode(byte[] key, int seconds, Map<byte[], byte[]> dataMap);

    protected abstract byte[] getNodeValue(byte[] nodeName, byte[] key);

    protected abstract void putNodeValue(byte[] nodeName, int seconds, byte[] key, byte[] t);

    protected abstract void removeNodeValue(byte[] nodeName, byte[] key);

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
    public <T> T get(String key) {
        byte[] datas = get(key.getBytes());
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param value <br>
     */
    @Override
    public <T> void put(String key, int seconds, T t) {
        byte[] keys = key.getBytes();
        put(keys, seconds, getData(t));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key <br>
     */
    @Override
    public void remove(final String key) {
        remove(key.getBytes());
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
        putNode(nodeName.getBytes(), seconds, hmap);
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
    public <T> T getNodeValue(final String nodeName, final String key) {
        byte[] datas = getNodeValue(nodeName.getBytes(), key.getBytes());
        return getValue(datas);
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
    public <T> void putNodeValue(final String nodeName, final String key, final T t) {
        putNodeValue(nodeName.getBytes(), 0, key.getBytes(), getData(t));
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
    public <T> void putNodeValue(final String nodeName, final int seconds, final String key, final T t) {
        putNodeValue(nodeName.getBytes(), seconds, key.getBytes(), getData(seconds, t));
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
    public void removeNodeValue(final String nodeName, final String key) {
        removeNodeValue(nodeName.getBytes(), key.getBytes());
    }

}
