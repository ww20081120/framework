/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.demo;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.message.core.MessageSubscriber;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.message <br>
 */
public class MessageLinsener implements MessageSubscriber {

    /** Number */
    private static final int NUM_4 = 4;

    /** name */
    private String name = CommonUtil.getRandomChar(NUM_4);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data <br>
     */
    @Override
    public void onMessage(final String channel, final byte[] data) {
        System.out.println(">>>>>>>>>>>>>" + Thread.currentThread().getName() + ">>>>>>>>>>>>>>>>>>>" + name
            + " onMessage: " + channel + "---" + new String(data));
        try {
            Thread.sleep(2 * GlobalConstants.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param subscribeChannels <br>
     */
    @Override
    public void onSubscribe(final String channel, final int subscribeChannels) {
        System.out.println(Thread.currentThread().getId());
        System.out.println(name + " onSubscribe: " + channel + "---" + subscribeChannels);
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
    public void onUnsubscribe(final String channel, final int subscribedChannels) {
        System.out.println(name + " onUnsubscribe: " + channel + "---" + subscribedChannels);
    }

}
