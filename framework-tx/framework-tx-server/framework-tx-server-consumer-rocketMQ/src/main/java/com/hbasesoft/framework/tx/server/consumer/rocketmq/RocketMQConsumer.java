/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.consumer.rocketmq;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
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
 * @see com.hbasesoft.framework.tx.server.consumer.rocketmq <br>
 */
@Service
public class RocketMQConsumer implements TxConsumer {

    private DefaultMQProducer defaultMQProducer;

    public RocketMQConsumer() {
        defaultMQProducer = new DefaultMQProducer(TxConsumer.CONSUMER_GROUP);
        String address = PropertyHolder.getProperty("tx.rocketmq.namesrvAddr");
        Assert.notEmpty(address, ErrorCodeDef.TX_ROCKET_MQ_ADDRESS_NOT_FOUND);
        defaultMQProducer.setNamesrvAddr(address);
        defaultMQProducer
            .setRetryTimesWhenSendAsyncFailed(PropertyHolder.getIntProperty("tx.rocketmq.producer.retrytimes", 3));
        try {
            defaultMQProducer.start();
        }
        catch (MQClientException e) {
            LoggerUtil.error("tx RocketMq defaultProducer faile.", e);
            defaultMQProducer.shutdown();
            throw new InitializationException(ErrorCodeDef.MESSAGE_MODEL_P_CREATE_ERROR, e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @Override
    public void retry(ClientInfo clientInfo) {

        if (clientInfo != null && StringUtils.isNotEmpty(clientInfo.getClientInfo())) {

            try {
                Message msg = new Message(clientInfo.getClientInfo(), GlobalConstants.BLANK,
                    SerializationUtil.serial(clientInfo));
                defaultMQProducer.send(msg);
            }
            catch (Exception e) {
                throw new ServiceException(e);
            }
        }

    }

}
