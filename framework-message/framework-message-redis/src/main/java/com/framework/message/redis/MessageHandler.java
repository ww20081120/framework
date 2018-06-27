/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
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

    private static final Logger LOGGER = new Logger("MessageHandlerLogger");

    private ArrayBlockingQueue<Consumer> consumerQueue = new ArrayBlockingQueue<Consumer>(10000);

    private boolean flag = true;

    private static MessageHandler handler;

    private MessageHandler() {
        initExecutor();
    }

    public static MessageHandler getInstance() {
        if (handler == null) {
            handler = new MessageHandler();
        }
        return handler;
    }

    public void addConsummer(MessageQueue queue, final String channel, final MessageSubscriber subscriber) {
        int coreReadSize = PropertyHolder.getIntProperty("message.event.coreConsummerSize", 1); // 核心读取线程大小
        for (int i = 0; i < coreReadSize; i++) {
            new Thread(() -> {
                try {
                    while (flag) {
                        try {
                            List<byte[]> datas = queue.pop(3, channel);
                            if (CollectionUtils.isNotEmpty(datas)) {
                                for (byte[] data : datas) {
                                    String transId = CommonUtil.getTransactionID();
                                    LOGGER.info("receive message by thread[{0}], transId[{1}]",
                                        Thread.currentThread().getId(), transId);
                                    consumerQueue.put(new Consumer(transId, channel, subscriber, data));
                                }
                            }
                            else {
                                LOGGER.info("channel {0} consumer is alived.", channel);
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
            }).start();
        }
    }

    private void initExecutor() {
        int corePoolSize = PropertyHolder.getIntProperty("message.redis.corePoolSize", 20); // 核心线程数
        for (int i = 0; i < corePoolSize; i++) {
            new Thread(() -> {
                long count = 0;
                try {
                    while (flag) {
                        try {
                            Consumer consumer = consumerQueue.poll(100, TimeUnit.MILLISECONDS);
                            if (consumer != null) {
                                consumer.excute();
                            }
                        }
                        catch (Exception e) {
                            LoggerUtil.error(e);
                            Thread.sleep(1000);
                        }

                        if (++count % 1000 == 0) {
                            LOGGER.info("thread {0} for executor is alived.", Thread.currentThread().getId());
                        }
                    }
                }
                catch (InterruptedException e1) {
                    LoggerUtil.error(e1);
                }
            }).start();
        }
    }

    public void destory() {
        flag = false;
    }

    private static class Consumer {

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

        public void excute() {
            try {
                LOGGER.info("{0}|{1} before execute event.", transId, channel);
                this.subscriber.onMessage(this.channel, data);
                LOGGER.info("{0}|{1} after execute event.", transId, channel);
            }
            catch (Exception e) {
                LOGGER.error(e, "{0}|{1}|FAIL|execute event error.", transId, channel);
            }
        }
    }
}
