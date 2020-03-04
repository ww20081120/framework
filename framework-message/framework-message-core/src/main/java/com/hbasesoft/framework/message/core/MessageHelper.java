/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.message.core.delay.DelayMessageQueue;
import com.hbasesoft.framework.message.core.delay.StepDelayMessageQueueLoader;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core <br>
 */
public final class MessageHelper {

    /** message publisher */
    private static MessagePublisher messagePublisher;

    /** message subcriber factory */
    private static MessageSubcriberFactory messageSubcriberFactory;

    /** delay message queue */
    private static DelayMessageQueue delayMessageQueue;

    /**
     * 
     */
    private MessageHelper() {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static MessagePublisher createMessagePublisher() {
        if (messagePublisher == null) {
            String messageModel = PropertyHolder.getProperty("message.model");
            Assert.notEmpty(messageModel, ErrorCodeDef.MESSAGE_MODEL_NOT_SET);

            ServiceLoader<MessagePublisher> serviceLoader = ServiceLoader.load(MessagePublisher.class);
            for (MessagePublisher c : serviceLoader) {
                if (messageModel.equals(c.getName())) {
                    messagePublisher = c;
                    break;
                }
            }

            if (messagePublisher == null) {
                throw new InitializationException(ErrorCodeDef.MESSAGE_MIDDLE_NOT_FOUND);
            }
        }
        return messagePublisher;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static MessageSubcriberFactory createMessageSubcriberFactory() {
        if (messageSubcriberFactory == null) {
            String messageModel = PropertyHolder.getProperty("message.model");
            Assert.notEmpty(messageModel, ErrorCodeDef.MESSAGE_MODEL_NOT_SET);

            ServiceLoader<MessageSubcriberFactory> serviceLoader = ServiceLoader.load(MessageSubcriberFactory.class);
            for (MessageSubcriberFactory c : serviceLoader) {
                if (messageModel.equals(c.getName())) {
                    messageSubcriberFactory = c;
                    break;
                }
            }

            if (messageSubcriberFactory == null) {
                throw new InitializationException(ErrorCodeDef.MESSAGE_MIDDLE_NOT_FOUND);
            }
        }
        return messageSubcriberFactory;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static DelayMessageQueue getDelayMessageQueue() {
        if (delayMessageQueue == null) {
            ServiceLoader<StepDelayMessageQueueLoader> serviceLoader = ServiceLoader
                .load(StepDelayMessageQueueLoader.class);
            Iterator<StepDelayMessageQueueLoader> it = serviceLoader.iterator();
            if (it.hasNext()) {
                StepDelayMessageQueueLoader loader = it.next();
                if (loader != null) {
                    delayMessageQueue = new DelayMessageQueue(loader);
                }
            }
            Assert.notNull(delayMessageQueue, ErrorCodeDef.UNSPORT_DELAY_MESSAGE);
        }
        return delayMessageQueue;
    }
}
