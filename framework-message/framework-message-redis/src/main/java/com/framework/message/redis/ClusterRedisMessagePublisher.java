/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.message.core.MessagePublisher;

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
public class ClusterRedisMessagePublisher implements MessagePublisher {

    /** message Queue */
    private MessageQueue messageQueue = new ClusterRedisMessageQueue();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        return RedisClientFactory.CLUSTER_MESSAGE_MODEL;
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
            RedisClientFactory.getBinaryJedisCluster().publish(channel.getBytes(), data);
        }
        else {
            messageQueue.push(channel, data);
        }
    }

}
