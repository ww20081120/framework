/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.kafka;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.hbasesoft.framework.message.core.MessageQueue;

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
public class KafkaMessageQueue implements MessageQueue {

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
     * @param key
     * @param value <br>
     */
    @Override
    public void push(String key, byte[] value) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(key, value);
        KafkaClientFacotry.getProducer().send(record);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param timeout
     * @param key
     * @return <br>
     */
    @Override
    public List<byte[]> pop(int timeout, String key) {
        KafkaConsumer<String, byte[]> kafkaConsumer = KafkaClientFacotry.getKafkaConsumer(key, key);

        ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(timeout * 1000L);
        if (records != null) {
            List<byte[]> datas = new ArrayList<byte[]>();
            for (ConsumerRecord<String, byte[]> record : records) {
                datas.add(record.value());
            }
            return datas;
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @return <br>
     */
    @Override
    public List<byte[]> popList(String key) {
        return pop(1, key);
    }

}
