/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月19日 <br>
 * @since V1.0<br>
 * @see com.framework.message.redis <br>
 */
public final class RedisClientFactory {

    /**
     * 缓存模式
     */
    public static final String MESSAGE_MODEL = "REDIS";

    public static final String CLUSTER_MESSAGE_MODEL = "REDIS_CLUSTER";

    private static final String REDIS_ADDRESS = "message.redis.address";
    
    private static final int MAX_TOTLE = 30;

    private static final int MAX_IDLE = 20;

    private static final int MAX_WAIT = 100;

    private static final boolean VALIDATE = false;

    private static JedisPool jedisPool;

    private static BinaryJedisCluster cluster;

    private RedisClientFactory() {
    }

    public static JedisPool getJedisPool() {
        if (jedisPool == null) {
            String cacheModel = PropertyHolder.getProperty("message.model");
            if (MESSAGE_MODEL.equals(cacheModel)) {
                Address[] addresses = getAddresses();
                List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(addresses.length);
                for (Address addr : addresses) {
                    shards.add(new JedisShardInfo(addr.getHost(), addr.getPort()));
                }
                jedisPool = new JedisPool(getConfig(), addresses[0].getHost(), addresses[0].getPort());
            }
        }
        return jedisPool;
    }

    public static BinaryJedisCluster getBinaryJedisCluster() {
        if (cluster == null) {
            String cacheModel = PropertyHolder.getProperty("cache.model");
            if (CLUSTER_MESSAGE_MODEL.equals(cacheModel)) {
                Address[] addresses = getAddresses();
                Set<HostAndPort> readSet = new HashSet<HostAndPort>();
                for (Address addr : addresses) {
                    HostAndPort hostAndPort = new HostAndPort(addr.getHost(), addr.getPort());
                    readSet.add(hostAndPort);
                }
                cluster = new BinaryJedisCluster(readSet,
                    PropertyHolder.getIntProperty("message.redis.cluster.max.timeout", 100000));
            }
        }
        return cluster;
    }

    private static Address[] getAddresses() {
        String address = PropertyHolder.getProperty(REDIS_ADDRESS);
        Assert.notEmpty(address, ErrorCodeDef.REDIS_ADDRESS_NOT_SET, REDIS_ADDRESS);
        return ProtocolUtil.parseAddress(address);
    }

    private static JedisPoolConfig getConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        
        //最大连接数, 默认30个
        config.setMaxTotal(PropertyHolder.getIntProperty("message.redis.max.total", MAX_TOTLE));
        
        // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(PropertyHolder.getIntProperty("message.redis.max.idle", MAX_IDLE));
        
        // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(1000 * PropertyHolder.getIntProperty("message.redis.max.wait", MAX_WAIT));
        
        // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(PropertyHolder.getBooleanProperty("message.redis.testonborrow", VALIDATE));
        
        return config;
    }

}
