/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.thread.MessageThreadPoolExecutor;
import com.hbasesoft.framework.message.core.MessageSubscriber;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月27日 <br>
 * @since V1.0<br>
 * @see com.framework.message.redis <br>
 */
public final class MessageHandler {

    /** Number */
    private static final int NUM_3 = 3;

    /** Number */
    private static final int NUM_500 = 500;

    /** logger */
    private static final Logger LOGGER = new Logger(MessageHandler.class);

    /** flag */
    private boolean flag = true;

    /** handler */
    private static MessageHandler handler;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static MessageHandler getInstance() {
        if (handler == null) {
            handler = new MessageHandler();
        }
        return handler;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param queue
     * @param channel
     * @param subscriber <br>
     */
    public void addConsummer(final MessageQueue queue, final String channel, final MessageSubscriber subscriber) {
        Thread thread = new Thread(() -> {
            // 建一个线程池
            try {
                long count = 0;
                while (flag) {
                    try {
                        // 每次从redis的队列中消费3条数据
                        List<byte[]> datas = queue.pop(NUM_3, channel);
                        if (CollectionUtils.isNotEmpty(datas)) {

                            String transId = CommonUtil.getTransactionID();
                            LOGGER.debug("receive message by thread[{0}], transId[{1}]", Thread.currentThread().getId(),
                                transId);

                            for (byte[] data : datas) {
                                MessageThreadPoolExecutor.execute(channel,
                                    new Consumer(transId, channel, subscriber, data));
                            }
                        }
                        else {
                            if (++count % NUM_500 == 0) {
                                LOGGER.debug("channel {0} consumer is alived.", channel);
                            }
                        }
                    }
                    catch (Exception e) {
                        LoggerUtil.error(e);
                        Thread.sleep(GlobalConstants.SECONDS);
                    }
                }
            }
            catch (InterruptedException e) {
                LoggerUtil.error(e);
            }
        });
        thread.setName("ls_" + channel);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    public void destory() {
        flag = false;
    }

    /**
     * <Description> <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate Mar 2, 2020 <br>
     * @since V1.0<br>
     * @see com.framework.message.redis <br>
     */
    private static class Consumer implements Runnable {

        /** transid */
        private String transId;

        /** channel */
        private String channel;

        /** subscriber */
        private MessageSubscriber subscriber;

        /** data */
        private byte[] data;

        /**
         * @param transId
         * @param channel
         * @param subscriber
         * @param data
         */
        Consumer(final String transId, final String channel, final MessageSubscriber subscriber, final byte[] data) {
            this.transId = transId;
            this.channel = channel;
            this.subscriber = subscriber;
            this.data = data;
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         *         <br>
         */
        public void run() {
            try {
                LOGGER.debug("{0}|{1} before execute event.", transId, channel);
                this.subscriber.onMessage(this.channel, data);
                LOGGER.debug("{0}|{1} after execute event.", transId, channel);
            }
            catch (Exception e) {
                LOGGER.error(e, "{0}|{1}|FAIL|execute event error.", transId, channel);
            }
        }
    }
}
