package com.hbasesoft.framework.message.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.MessagePublisher;
import com.hbasesoft.framework.message.rocketmq.factory.RocketmqFactory;

/**
 * <Description> <br>
 *
 * @author 大刘杰<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月25日 <br>
 * @see com.hbasesoft.framework.message.rocketmq <br>
 * @since V1.0<br>
 */
public class RocketmqMessagePublisher implements MessagePublisher {

    /** log */
    private static final Logger LOG = new Logger(RocketmqMessagePublisher.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        return RocketmqFactory.ROCKET_MQ_NAME;
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
    public void publish(final String channel, final byte[] data) {
        // 默认使用普通消费
        publish(channel, data, MessagePublisher.PUBLISH_TYPE_DEFAULT, 0);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data
     * @param seconds <br>
     */
    @Override
    public void publish(final String channel, final byte[] data, final int seconds) {
        // 默认使用普通消费
        publish(channel, data, MessagePublisher.PUBLISH_TYPE_DEFAULT, seconds);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data
     * @param produceModel <br>
     */
    @Override
    public void publish(final String channel, final byte[] data, final String produceModel) {
        // 指定消费模式
        publish(channel, data, produceModel, 0);
    }

    /**
     * publish:. <br/>
     *
     * @param channel
     * @param data
     * @param produceModel
     * @param seconds
     * @author 大刘杰
     * @produceModel: RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
     *                RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
     *                RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
     * @since JDK 1.8
     */
    public void publish(final String channel, final byte[] data, final String produceModel, final int seconds) {

        DefaultMQProducer defaultMQProducer = RocketmqFactory
            .getDefaultProducer(PropertyHolder.getProperty("message.rocketmq.groupName", "hbasesoft"));

        // Create a message instance, specifying topic, tag and message body.
        Message msg = new Message(channel, GlobalConstants.BLANK, data);

        // Set delay level
        if (seconds > 0L) {
            msg.setDelayTimeLevel(RocketmqFactory.calculationLevel(seconds));
        }

        try {
            switch (produceModel) {
                case RocketmqFactory.ROCKET_MQ_PUBLISH_TYPE_ORDERLY:
                    // 顺序消费
                    defaultMQProducer.send(msg, (messageQueue, message, arg) -> {
                        Integer id = (Integer) arg;
                        System.out.println(messageQueue.get(id));
                        return messageQueue.get(id);
                    }, 0); // 默认四个队列中的0号队列 队列内有序
                    break;
                case RocketmqFactory.ROCKET_MQ_PUBLISH_TYPE_TRANSACTION:
                    // 事务消费
                    // transactionMQProducer.sendMessageInTransaction(msg, tranExecuter, arg);
                    break;
                default:
                    // 普通消费
                    SendResult send = defaultMQProducer.send(msg);
                    LOG.debug("发送结果 " + send.toString());
                    break;
            }
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.MESSAGE_MODEL_P_SEND_ERROR, e);
        }

    }
}
