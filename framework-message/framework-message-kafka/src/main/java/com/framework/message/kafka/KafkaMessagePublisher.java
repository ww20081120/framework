/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.hbasesoft.framework.message.core.MessagePublisher;

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
public class KafkaMessagePublisher implements MessagePublisher {

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
     * @param data <br>
     */
    @Override
    public void publish(String channel, boolean broadcast, byte[] data) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(channel, data);
        KafkaClientFacotry.getProducer().send(record);
    }

}
