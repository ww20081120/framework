/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.client.consumer.rocketmq;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.tx.core.DefaultConsumer;
import com.hbasesoft.framework.tx.core.TxConsumer;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 3, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.client.consumer.rocketmq <br>
 */
public class RocketMQStartupListener implements StartupListener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void complete(ApplicationContext context) {
        LoggerUtil.info("开始启动分布式事务Rocket MQ Consumer");

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(TxConsumer.CONSUMER_CHANNEL);

        // Name service address
        consumer.setNamesrvAddr(PropertyHolder.getProperty("tx.rocketmq.namesrvAddr"));
        // Set Consume Thread
        consumer.setConsumeThreadMin(PropertyHolder.getIntProperty("tx.executor.coreSize", 20));
        consumer.setConsumeThreadMax(PropertyHolder.getIntProperty("tx.executor.maxPoolSize", 64));
        // One time consume max size
        consumer.setConsumeMessageBatchMaxSize(1);

        TxConsumer txConsumer = new DefaultConsumer();

        try {
            consumer.subscribe(TxConsumer.CONSUMER_CHANNEL, "*");

            consumer.registerMessageListener(new MessageListenerConcurrently() {

                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                    ConsumeConcurrentlyContext context) {
                    if (CollectionUtils.isNotEmpty(msgs)) {
                        try {
                            for (MessageExt messageExt : msgs) {
                                byte[] body = messageExt.getBody();
                                if (body != null && body.length > 0) {
                                    txConsumer.retry(SerializationUtil.unserial(ClientInfo.class, body));
                                }
                            }
                        }
                        catch (Exception e) {
                            LoggerUtil.error(e);
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();
            LoggerUtil.info("启动分布式事务Rocket MQ Consumer 成功！");

        }
        catch (MQClientException e) {
            LoggerUtil.error("RocketMq tx consumer Start failure!!!.", e);
            consumer.shutdown();
            throw new InitializationException(ErrorCodeDef.MESSAGE_MODEL_C_CREATE_ERROR, e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public LoadOrder getOrder() {
        return LoadOrder.LAST;
    }
}
