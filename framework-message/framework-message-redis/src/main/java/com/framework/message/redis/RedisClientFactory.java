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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;

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

    /** CLUSTER_MESSAGE_MODEL */
    public static final String CLUSTER_MESSAGE_MODEL = "REDIS_CLUSTER";

    /** REDIS_ADDRESS */
    private static final String REDIS_ADDRESS = "message.redis.address";

    /** MAX_TOTLE */
    private static final int MAX_TOTLE = 1000;

    /** MAX_IDLE */
    private static final int MAX_IDLE = 20;

    /** MAX_WAIT */
    private static final int MAX_WAIT = 100;

    /** max timeout */
    private static final int MAX_TIMEOUT = 100000;

    /** VALIDATE */
    private static final boolean VALIDATE = false;

    /** DEFAULT_MAX_REDIRECTIONS */
    private static final int DEFAULT_MAX_REDIRECTIONS = 5;

    /** JedisPool */
    private static JedisPool jedisPool;

    /** BinaryJedisCluster */
    private static BinaryJedisCluster cluster;

    /** lock */
    private static Object lock = new Object();

    /**
     * 
     */
    private RedisClientFactory() {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static JedisPool getJedisPool() {
        synchronized (lock) {
            if (jedisPool == null) {
                String cacheModel = PropertyHolder.getProperty("message.model");
                if (MESSAGE_MODEL.equals(cacheModel)) {
                    Address[] addresses = getAddresses();
                    String passwd = CommonUtil.isNotEmpty(addresses) ? addresses[0].getPassword() : null;
                    List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(addresses.length);
                    for (Address addr : addresses) {
                        shards.add(new JedisShardInfo(addr.getHost(), addr.getPort()));
                    }
                    if (StringUtils.isEmpty(passwd)) {
                        jedisPool = new JedisPool(getConfig(), addresses[0].getHost(), addresses[0].getPort());
                    }
                    else {
                        jedisPool = new JedisPool(getConfig(), addresses[0].getHost(), addresses[0].getPort(),
                            Protocol.DEFAULT_TIMEOUT, passwd, Protocol.DEFAULT_DATABASE, null);
                    }
                }
            }
            return jedisPool;
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static BinaryJedisCluster getBinaryJedisCluster() {
        synchronized (lock) {
            if (cluster == null) {
                String cacheModel = PropertyHolder.getProperty("cache.model");
                if (CLUSTER_MESSAGE_MODEL.equals(cacheModel)) {
                    Address[] addresses = getAddresses();
                    Set<HostAndPort> readSet = new HashSet<HostAndPort>();
                    String passwd = CommonUtil.isNotEmpty(addresses) ? addresses[0].getPassword() : null;
                    for (Address addr : addresses) {
                        HostAndPort hostAndPort = new HostAndPort(addr.getHost(), addr.getPort());
                        readSet.add(hostAndPort);
                    }
                    // cluster = new BinaryJedisCluster(readSet,
                    // PropertyHolder.getIntProperty("message.redis.cluster.max.timeout", 100000));

                    if (StringUtils.isEmpty(passwd)) {
                        cluster = new BinaryJedisCluster(readSet,
                            PropertyHolder.getIntProperty("message.redis.cluster.max.timeout", MAX_TIMEOUT),
                            DEFAULT_MAX_REDIRECTIONS, getConfig());
                    }
                    else {
                        cluster = new JedisCluster(readSet,
                            PropertyHolder.getIntProperty("cache.redis.cluster.max.timeout", MAX_TIMEOUT),
                            PropertyHolder.getIntProperty("cache.redis.cluster.max.timeout", MAX_TIMEOUT),
                            DEFAULT_MAX_REDIRECTIONS, passwd, new GenericObjectPoolConfig());
                    }
                }
            }
            return cluster;
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    private static Address[] getAddresses() {
        String address = PropertyHolder.getProperty(REDIS_ADDRESS);
        Assert.notEmpty(address, ErrorCodeDef.REDIS_ADDRESS_NOT_SET, REDIS_ADDRESS);
        return ProtocolUtil.parseAddress(address);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    private static JedisPoolConfig getConfig() {
        JedisPoolConfig config = new JedisPoolConfig();

        // 最大连接数, 默认30个
        config.setMaxTotal(PropertyHolder.getIntProperty("message.redis.max.total", MAX_TOTLE));

        // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(PropertyHolder.getIntProperty("message.redis.max.idle", MAX_IDLE));

        // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(
            GlobalConstants.SECONDS * PropertyHolder.getIntProperty("message.redis.max.wait", MAX_WAIT));

        // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(PropertyHolder.getBooleanProperty("message.redis.testonborrow", VALIDATE));

        return config;
    }

}
