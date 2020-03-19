/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis;

import com.hbasesoft.framework.cache.core.AbstractCache;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;

import redis.clients.jedis.JedisPoolConfig;

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
public abstract class AbstractRedisCache extends AbstractCache {

    /** redis 配置 */
    private static final String REDIS_ADDRESS = "cache.redis.address";

    /** 最大超时时间 */
    private static final int MAX_IDLE = 5;

    /** 最大等待时间 */
    private static final int MAX_WAIT = 100;

    /** 是否校验完成 */
    private static final boolean VALIDATE = false;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected JedisPoolConfig getConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(PropertyHolder.getIntProperty("cache.redis.max.idle", MAX_IDLE));
        // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(
            GlobalConstants.SECONDS * PropertyHolder.getIntProperty("cache.redis.max.wait", MAX_WAIT));
        // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(PropertyHolder.getBooleanProperty("cache.redis.testonborrow", VALIDATE));
        return config;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected Address[] getAddresses() {
        String address = PropertyHolder.getProperty(REDIS_ADDRESS);
        Assert.notEmpty(address, ErrorCodeDef.REDIS_ADDRESS_NOT_SET, REDIS_ADDRESS);
        return ProtocolUtil.parseAddress(address);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param value
     * @param expireTime
     * @return <br>
     */
    public abstract boolean setnx(String key, String value, int expireTime);
}
