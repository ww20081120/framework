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

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
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
    public void registSubscriber(final String channel, final MessageSubscriber subscriber) {
        new Thread(() -> {
            int index = atomicInteger.incrementAndGet();
            try {
                subscriber.onSubscribe(channel, index);
                KafkaConsumer<String, byte[]> kafkaConsumer = KafkaClientFacotry
                    .getKafkaConsumer(channel + Thread.currentThread().getId(), channel);
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
                        LoggerUtil.error(e);
                        Thread.sleep(1000L);
                    }
                }
            }
            catch (Exception e) {
                LoggerUtil.error(e);
            }
            subscriber.onUnsubscribe(channel, index);
        }).start();
    }
}
