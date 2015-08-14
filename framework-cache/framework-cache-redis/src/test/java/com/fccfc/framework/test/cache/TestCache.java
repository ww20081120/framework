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

    /**
     * cache
     */
    private ICache cache;

    /**
     * stringCache
     */
    private IStringCache stringCache;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br> <br>
     */
    @Before
    public void init() {
        cache = new RedisCache("127.0.0.1", 6379);
        stringCache = new RedisStringCache("127.0.0.1", 6379);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws CacheException <br>
     */
    @Test
    public void getNode() throws CacheException {
    	
    }

    /**
     * Description: putNode<br>
     * 
     * @author 王伟 <br>
     * @throws CacheException <br>
     */
    public void putNode() throws CacheException {
    	
    }

    /**
     * Description: removeNode<br>
     * 
     * @author 王伟 <br>
     * @throws CacheException <br>
     */
    public void removeNode() throws CacheException {
    	
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws CacheException <br>
     */
    @Test
    public void getValue() throws CacheException {
        System.out.println(cache.getValue("a", "a"));
        System.out.println(stringCache.getValue("b", "a"));
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @throws CacheException <br>
     */
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
     * @throws CacheException <br>
     */
    public void updateValue() throws CacheException {
    }

    /**
     * Description: removeValue<br>
     * 
     * @author 王伟 <br>
     * @throws CacheException <br>
     */
    public void removeValue() throws CacheException {
    }

    /**
     * Description: clean<br>
     * 
     * @author 王伟 <br>
     * @throws CacheException <br>
     */
    public void clean() throws CacheException {
    }
}
