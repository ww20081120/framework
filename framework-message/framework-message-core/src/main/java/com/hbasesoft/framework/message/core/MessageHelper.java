/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core;

import java.util.ServiceLoader;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.PropertyHolder;

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

    private static MessagePublisher messagePublisher;

    private static MessageQueue messageQueue;

    private static MessageSubcriberFactory messageSubcriberFactory;

    private MessageHelper() {
    }

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

    public static MessageQueue createMessageQueue() {
        if (messageQueue == null) {
            String messageModel = PropertyHolder.getProperty("message.model");
            Assert.notEmpty(messageModel, ErrorCodeDef.MESSAGE_MODEL_NOT_SET);

            ServiceLoader<MessageQueue> serviceLoader = ServiceLoader.load(MessageQueue.class);
            for (MessageQueue c : serviceLoader) {
                if (messageModel.equals(c.getName())) {
                    messageQueue = c;
                    break;
                }
            }

            if (messageQueue == null) {
                throw new InitializationException(ErrorCodeDef.MESSAGE_MIDDLE_NOT_FOUND);
            }
        }
        return messageQueue;
    }

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
}
