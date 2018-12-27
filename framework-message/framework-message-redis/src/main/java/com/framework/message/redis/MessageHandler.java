/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.redis;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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

            ThreadPoolExecutor executor = createThreadPoolExecutor();
            BlockingQueue<Runnable> bq = executor.getQueue();
            try {
                while (flag) {
                    try {
                        // 每次从redis的队列中消费3条数据
                        List<byte[]> datas = queue.pop(3, channel);
                        if (CollectionUtils.isNotEmpty(datas)) {

                            String transId = CommonUtil.getTransactionID();
                            LOGGER.info("receive message by thread[{0}], transId[{1}]", Thread.currentThread().getId(),
                                transId);

                            // 当线程池中的队列出现阻塞后，暂停从redis中进行获取
                            while (bq.remainingCapacity() == 0
                                && executor.getMaximumPoolSize() == executor.getPoolSize()) {
                                LOGGER.info("wait message[{0}] execute, current pool size is [{1}]", channel,
                                    bq.size());
                                Thread.sleep(100);
                            }

                            for (byte[] data : datas) {
                                executor.execute(new Consumer(transId, channel, subscriber, data));
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
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void destory() {
        flag = false;
    }

    private ThreadPoolExecutor createThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            PropertyHolder.getIntProperty("message.executor.coreSize", 1), // 设置核心线程数量
            PropertyHolder.getIntProperty("message.executor.maxPoolSize", 10), // 线程池维护线程的最大数量
            PropertyHolder.getIntProperty("message.executor.keepAliveSeconds", 600), TimeUnit.SECONDS, // 允许的空闲时间
            new ArrayBlockingQueue<>(PropertyHolder.getIntProperty("message.executor.queueCapacity", 10))); // 缓存队列
        return executor;
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
