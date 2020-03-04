/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import com.hbasesoft.framework.message.core.MessageSubcriberFactory;
import com.hbasesoft.framework.message.core.MessageSubscriber;

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
public class ClusterRedisMessageSubcriberFactory implements MessageSubcriberFactory {

    /** message queue */
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
     * @param subscriber <br>
     */
    @Override
    public void registSubscriber(final String channel, final boolean broadcast, final MessageSubscriber subscriber) {
        if (broadcast) {
            new Thread(() -> {
                RedisClientFactory.getBinaryJedisCluster().subscribe(new BinaryListener(subscriber),
                    channel.getBytes());
            }).start();
        }
        else {
            MessageHandler.getInstance().addConsummer(messageQueue, channel, subscriber);
        }
    }

}
