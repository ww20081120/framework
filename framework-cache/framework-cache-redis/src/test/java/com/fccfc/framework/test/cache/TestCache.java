/**
 * 
 */
package com.fccfc.framework.test.cache;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.ICache;
import com.fccfc.framework.cache.core.IStringCache;
import com.fccfc.framework.cache.core.redis.RedisCache;
import com.fccfc.framework.cache.core.redis.RedisStringCache;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.fccfc.framework.test.cache <br>
 */
public class TestCache {

    private ICache cache;

    private IStringCache stringCache;

    @Before
    public void init() {
        cache = new RedisCache("112.124.36.185", 6379);
        stringCache = new RedisStringCache("112.124.36.185", 6379);
    }

    @Test
    public void getNode() throws CacheException {

    }

    /**
     * Description: putNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @param node <br>
     */
    public void putNode() throws CacheException {

    }

    /**
     * Description: removeNode<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @return <br>
     */
    public void removeNode() throws CacheException {

    }

    @Test
    public void getValue() throws CacheException {
        System.out.println(cache.getValue("a", "a"));
        System.out.println(stringCache.getValue("b", "a"));
    }

    @Test
    public void putValue() throws CacheException {
        JSONObject object = new JSONObject();
        object.put("a", "b");
        cache.putValue("a", "a", object);

        stringCache.putValue("b", "a", null);
    }

    /**
     * Description: updateValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @param key
     * @param t <br>
     */
    public void updateValue() throws CacheException {
    }

    /**
     * Description: removeValue<br>
     * 
     * @author 王伟 <br>
     * @param nodeName
     * @param key
     * @return <br>
     */
    public void removeValue() throws CacheException {
    }

    /**
     * Description: clean<br>
     * 
     * @author 王伟 <br>
     * <br>
     */
    public void clean() throws CacheException {
    }
}
