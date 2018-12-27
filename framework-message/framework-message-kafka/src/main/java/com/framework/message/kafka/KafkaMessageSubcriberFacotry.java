/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.kafka;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.MessageSubcriberFactory;
import com.hbasesoft.framework.message.core.MessageSubscriber;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年8月24日 <br>
 * @since V1.0<br>
 * @see com.framework.message.kafka <br>
 */
public class KafkaMessageSubcriberFacotry implements MessageSubcriberFactory {

    private static final Logger LOGGER = new Logger("EventHandlerLogger");

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        return KafkaClientFacotry.KAFKA_NAME;
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
    public void registSubscriber(final String channel, boolean broadcast, final MessageSubscriber subscriber) {
        new Thread(() -> {
            KafkaConsumer<String, byte[]> kafkaConsumer = KafkaClientFacotry
                .getKafkaConsumer(broadcast ? channel + CommonUtil.getTransactionID() : channel, channel);
            registSubscriberBroadCast(kafkaConsumer, channel, subscriber);
        }).start();
    }

    private void registSubscriberBroadCast(KafkaConsumer<String, byte[]> kafkaConsumer, String channel,
        final MessageSubscriber subscriber) {

        int index = atomicInteger.incrementAndGet();
        try {
            subscriber.onSubscribe(channel, index);

            // 建一个线程池
            ThreadPoolExecutor executor = createThreadPoolExecutor();

            BlockingQueue<Runnable> bq = executor.getQueue();

            long count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(3 * 1000L);
                    if (records != null) {

                        // 当线程池中的队列出现阻塞后，暂停从redis中进行获取
                        while (bq.remainingCapacity() == 0 && executor.getMaximumPoolSize() == executor.getPoolSize()) {
                            LOGGER.info("wait message[{0}] execute, current pool size is [{1}]", channel, bq.size());
                            Thread.sleep(100);
                        }

                        for (ConsumerRecord<String, byte[]> record : records) {
                            executor.execute(() -> {
                                subscriber.onMessage(channel, record.value());
                            });
                        }
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e);
                    Thread.sleep(1000L);
                }
                if (++count % 500 == 0) {
                    LOGGER.info("subscriber for {0}|{1} is alived.", channel, subscriber.getClass().getName());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
        }
        subscriber.onUnsubscribe(channel, index);

    }

    private ThreadPoolExecutor createThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            PropertyHolder.getIntProperty("message.executor.coreSize", 1), // 设置核心线程数量
            PropertyHolder.getIntProperty("message.executor.maxPoolSize", 10), // 线程池维护线程的最大数量
            PropertyHolder.getIntProperty("message.executor.keepAliveSeconds", 600), TimeUnit.SECONDS, // 允许的空闲时间
            new ArrayBlockingQueue<>(PropertyHolder.getIntProperty("message.executor.queueCapacity", 10))); // 缓存队列
        return executor;
    }
}
