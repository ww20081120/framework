/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.kafka;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.hbasesoft.framework.common.utils.CommonUtil;
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

            long count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(3 * 1000L);
                    if (records != null) {
                        for (ConsumerRecord<String, byte[]> record : records) {
                            subscriber.onMessage(channel, record.value());
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
}
