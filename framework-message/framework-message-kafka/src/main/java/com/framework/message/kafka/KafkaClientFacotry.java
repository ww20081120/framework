/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.kafka;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import com.hbasesoft.framework.common.utils.PropertyHolder;

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
public final class KafkaClientFacotry {
    /** message.kafka.batch.size */
    private static final int BATCH_SIZE = 16384;

    /** message.kafka.buffer.memory */
    private static final long BUFFER_MEMORY = 33554432L;

    /** kafka name */
    public static final String KAFKA_NAME = "KAFKA";

    /** lock */
    private static Object lock = new Object();

    /** kafka producer */
    private static KafkaProducer<String, byte[]> kafkaProducer;

    /**
     * 
     */
    private static ThreadLocal<Map<String, KafkaConsumer<String, byte[]>>> threadLocalHolder = 
        new ThreadLocal<Map<String, KafkaConsumer<String, byte[]>>>();

    private KafkaClientFacotry() {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static KafkaProducer<String, byte[]> getProducer() {
        synchronized (lock) {
            if (kafkaProducer == null) {
                Properties props = new Properties();
                props.put("bootstrap.servers", PropertyHolder.getProperty("message.kafka.bootstrap.servers"));
                props.put("acks", PropertyHolder.getProperty("message.kafka.acks", "all"));
                props.put("retries", PropertyHolder.getIntProperty("message.kafka.retries", 0));
                props.put("batch.size", PropertyHolder.getIntProperty("message.kafka.batch.size", BATCH_SIZE));
                props.put("linger.ms", PropertyHolder.getIntProperty("message.kafka.linger.ms", 1));
                props.put("buffer.memory",
                    PropertyHolder.getLongProperty("message.kafka.buffer.memory", BUFFER_MEMORY));
                props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
                kafkaProducer = new KafkaProducer<String, byte[]>(props);
            }
        }
        return kafkaProducer;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param group
     * @param topic
     * @return <br>
     */
    public static KafkaConsumer<String, byte[]> getKafkaConsumer(final String group, final String topic) {
        Map<String, KafkaConsumer<String, byte[]>> consumerConnectorHolder = getConsumerConnectorHolder();
        KafkaConsumer<String, byte[]> consumer = consumerConnectorHolder.get(group);
        if (consumer == null) {
            consumer = newKafkaConsumer(group);
            // 订阅主题列表topic
            consumer.subscribe(Arrays.asList(topic));
            consumerConnectorHolder.put(group, consumer);
        }
        return consumer;
    }

    private static KafkaConsumer<String, byte[]> newKafkaConsumer(final String group) {
        Properties props = new Properties();

        props.put("bootstrap.servers", PropertyHolder.getProperty("message.kafka.bootstrap.servers"));

        // 消费者的组id
        props.put("group.id", group); // 这里是GroupA或者GroupB

        props.put("enable.auto.commit", PropertyHolder.getProperty("message.kafka.enable.auto.commit", "true"));
        props.put("auto.commit.interval.ms",
            PropertyHolder.getProperty("message.kafka.auto.commit.interval.ms", "1000"));

        // 从poll(拉)的回话处理时长
        props.put("session.timeout.ms", PropertyHolder.getProperty("message.kafka.session.timeout.ms", "30000"));
        // poll的数量限制
        props.put("max.poll.records", PropertyHolder.getProperty("message.kafka.max.poll.records", "100"));

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");

        return new KafkaConsumer<String, byte[]>(props);
    }

    private static Map<String, KafkaConsumer<String, byte[]>> getConsumerConnectorHolder() {
        Map<String, KafkaConsumer<String, byte[]>> consumerConnectorHolder = threadLocalHolder.get();
        if (consumerConnectorHolder == null) {
            consumerConnectorHolder = new HashMap<String, KafkaConsumer<String, byte[]>>();
            threadLocalHolder.set(consumerConnectorHolder);
        }
        return consumerConnectorHolder;
    }

}
