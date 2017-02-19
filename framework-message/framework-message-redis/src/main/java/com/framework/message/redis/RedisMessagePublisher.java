/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.message.core.MessagePublisher;

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
public class RedisMessagePublisher implements MessagePublisher {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data <br>
     */
    @Override
    public void publish(String channel, byte[] data) {
        Jedis jedis = null;
        try {
            jedis = RedisClientFactory.getJedisPool().getResource();
            jedis.publish(channel.getBytes(), data);
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
