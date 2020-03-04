/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
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

    /** message queue */
    private MessageQueue messageQueue = new RedisMessageQueue();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        return RedisClientFactory.MESSAGE_MODEL;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data <br>
     */
    @Override
    public void publish(final String channel, final byte[] data) {
        String broadcastChannels = PropertyHolder.getProperty("message.redis.broadcast.channels");
        if (StringUtils.isNotEmpty(broadcastChannels) && CommonUtil.match(broadcastChannels, channel)) {
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
        else {
            messageQueue.push(channel, data);
        }
    }

}
