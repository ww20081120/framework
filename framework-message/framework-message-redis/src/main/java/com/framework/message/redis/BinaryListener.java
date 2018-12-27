/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import com.hbasesoft.framework.message.core.MessageSubscriber;
import com.hbasesoft.framework.message.core.util.MessageThreadPoolExecutor;

import redis.clients.jedis.BinaryJedisPubSub;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月21日 <br>
 * @since V1.0<br>
 * @see com.framework.message.redis <br>
 */
public class BinaryListener extends BinaryJedisPubSub {

    private MessageSubscriber subscriber;

    public BinaryListener(MessageSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param message <br>
     */
    @Override
    public void onMessage(byte[] channel, byte[] message) {
        String channelStr = new String(channel);
        MessageThreadPoolExecutor.execute(channelStr, () -> {
            subscriber.onMessage(new String(channel), message);
        });

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param subscribedChannels <br>
     */
    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {
        subscriber.onSubscribe(new String(channel), subscribedChannels);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param subscribedChannels <br>
     */
    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        subscriber.onUnsubscribe(new String(channel), subscribedChannels);
    }

}
