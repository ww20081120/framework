/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

import redis.clients.jedis.Jedis;

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
            jedis = RedisClientFactory.getJedisPool().getResource();
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
            jedis = RedisClientFactory.getJedisPool().getResource();
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
    public List<byte[]> pop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = RedisClientFactory.getJedisPool().getResource();
            List<byte[]> result = jedis.brpop(timeout, key.getBytes());
            if (CollectionUtils.isNotEmpty(result) && result.size() >= 2) {
                result.remove(0);
            }
            return result;
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
}
