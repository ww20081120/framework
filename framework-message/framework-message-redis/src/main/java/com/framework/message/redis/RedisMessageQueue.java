/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import java.util.ArrayList;
import java.util.List;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil;
import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;
import com.hbasesoft.framework.message.core.MessageQueue;

import redis.clients.jedis.Jedis;
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
public class RedisMessageQueue implements MessageQueue {

    /**
     * 缓存模式
     */
    public static final String MESSAGE_MODEL = "REDIS";

    private static final String REDIS_ADDRESS = "message.redis.address";

    private static final int MAX_IDLE = 5;

    private static final int MAX_WAIT = 100;

    private static final boolean VALIDATE = false;

    private JedisPool jedisPool;

    public RedisMessageQueue() {
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param value <br>
     */
    @Override
    public void push(String key, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(key.getBytes(), value);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * Description: <br>
     * 081120
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @return <br>
     */
    @Override
    public List<byte[]> popList(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(key.getBytes(), 0, -1);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param timeout
     * @param key
     * @return <br>
     */
    @Override
    public byte[] pop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<byte[]> dataList = jedis.blpop(timeout, key.getBytes());
            return CommonUtil.isEmpty(dataList) ? null : dataList.get(dataList.size() - 1);
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    protected Address[] getAddresses() {
        String address = PropertyHolder.getProperty(REDIS_ADDRESS);
        Assert.notEmpty(address, ErrorCodeDef.REDIS_ADDRESS_NOT_SET, REDIS_ADDRESS);
        return ProtocolUtil.parseAddress(address);
    }

    protected JedisPoolConfig getConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(PropertyHolder.getIntProperty("message.redis.max.idle", MAX_IDLE));
        // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(1000 * PropertyHolder.getIntProperty("message.redis.max.wait", MAX_WAIT));
        // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(PropertyHolder.getBooleanProperty("message.redis.testonborrow", VALIDATE));
        return config;
    }

}
