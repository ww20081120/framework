/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.consumer.rocketmq;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;
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

    /** tx.rocketmq.producer.retrytimes */
    private static final int RETRY_TIMES = 3;

    /** producerHolder */
    private Map<String, DefaultMQProducer> producerHolder = new HashMap<>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @Override
    public boolean retry(final ClientInfo clientInfo) {

        if (clientInfo != null && StringUtils.isNotEmpty(clientInfo.getClientInfo())) {

            try {
                Message msg = new Message(clientInfo.getClientInfo(), GlobalConstants.BLANK,
                    SerializationUtil.serial(clientInfo));
                getMQProducer(clientInfo.getClientInfo()).send(msg);
                return true;
            }
            catch (Exception e) {
                LoggerUtil.error(e);
            }
        }
        return false;
    }

    private synchronized DefaultMQProducer getMQProducer(final String clientInfo) {
        DefaultMQProducer defaultMQProducer = producerHolder.get(clientInfo);
        if (defaultMQProducer == null) {
            defaultMQProducer = new DefaultMQProducer(clientInfo);
            String address = PropertyHolder.getProperty("tx.rocketmq.namesrvAddr");
            Assert.notEmpty(address, ErrorCodeDef.TX_ROCKET_MQ_ADDRESS_NOT_FOUND);
            defaultMQProducer.setNamesrvAddr(address);
            defaultMQProducer.setRetryTimesWhenSendAsyncFailed(
                PropertyHolder.getIntProperty("tx.rocketmq.producer.retrytimes", RETRY_TIMES));
            try {
                defaultMQProducer.start();
                producerHolder.put(clientInfo, defaultMQProducer);
            }
            catch (MQClientException e) {
                LoggerUtil.error("tx RocketMq defaultProducer faile.", e);
                defaultMQProducer.shutdown();
                throw new InitializationException(ErrorCodeDef.MESSAGE_MODEL_P_CREATE_ERROR, e);
            }
        }
        return defaultMQProducer;
    }

}
