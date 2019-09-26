/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.MessageSubscriber;
import com.hbasesoft.framework.message.core.util.MessageThreadPoolExecutor;

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

    private static final Logger LOGGER = new Logger("MessageHandlerLogger");

    private boolean flag = true;

    private static MessageHandler handler;

    public static MessageHandler getInstance() {
        if (handler == null) {
            handler = new MessageHandler();
        }
        return handler;
    }

    public void addConsummer(MessageQueue queue, final String channel, final MessageSubscriber subscriber) {
        Thread thread = new Thread(() -> {
            // 建一个线程池
            try {
                while (flag) {
                    try {
                        // 每次从redis的队列中消费3条数据
                        List<byte[]> datas = queue.pop(3, channel);
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
                            LOGGER.debug("channel {0} consumer is alived.", channel);
                        }
                    }
                    catch (Exception e) {
                        LoggerUtil.error(e);
                        Thread.sleep(1000);
                    }
                }
            }
            catch (InterruptedException e) {
                LoggerUtil.error(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void destory() {
        flag = false;
    }

    private static class Consumer implements Runnable {

        private String transId;

        private String channel;

        private MessageSubscriber subscriber;

        private byte[] data;

        public Consumer(String transId, String channel, MessageSubscriber subscriber, byte[] data) {
            this.transId = transId;
            this.channel = channel;
            this.subscriber = subscriber;
            this.data = data;
        }

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
