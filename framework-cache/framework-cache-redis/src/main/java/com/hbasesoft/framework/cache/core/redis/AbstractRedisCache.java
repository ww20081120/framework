/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.redis;

import com.hbasesoft.framework.cache.core.AbstractCache;
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

    private static final String REDIS_ADDRESS = "cache.redis.address";

    private static final int MAX_IDLE = 5;

    private static final int MAX_WAIT = 100;

    private static final boolean validate = false;

    protected JedisPoolConfig getConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(PropertyHolder.getIntProperty("cache.redis.max.idle", MAX_IDLE));
        // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(1000 * PropertyHolder.getIntProperty("cache.redis.max.wait", MAX_WAIT));
        // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(PropertyHolder.getBooleanProperty("cache.redis.testonborrow", validate));
        return config;
    }

    protected Address[] getAddresses() {
        String address = PropertyHolder.getProperty(REDIS_ADDRESS);
        Assert.notEmpty(address, "{0} 未配置", REDIS_ADDRESS);
        return ProtocolUtil.parseAddress(address);
    }

}
