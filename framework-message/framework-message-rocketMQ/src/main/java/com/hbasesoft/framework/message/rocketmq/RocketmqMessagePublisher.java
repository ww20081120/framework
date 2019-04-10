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

    private static final Logger log = new Logger(RocketmqMessagePublisher.class);

    @Override
    public String getName() {
        return RocketmqFactory.ROCKET_MQ_NAME;
    }

    @Override
    public void publish(String channel, byte[] data) {
        // 默认使用普通消费
        publish(channel, data, MessagePublisher.PUBLISH_TYPE_DEFAULT, 0);
    }

    @Override
    public void publish(String channel, byte[] data, int seconds) {
        // 默认使用普通消费
        publish(channel, data, MessagePublisher.PUBLISH_TYPE_DEFAULT, seconds);
    }

    @Override
    public void publish(String channel, byte[] data, String produceModel) {
        // 指定消费模式
        publish(channel, data, produceModel, 0);
    }

    /**
     * publish:. <br/>
     *
     * @param channel
     * @param data
     * @param produceModel
     * @param delayTime
     * @author 大刘杰
     * @produceModel: RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
     *                RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
     *                RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
     * @since JDK 1.8
     */
    public void publish(String channel, byte[] data, String produceModel, int seconds) {

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
                case MessagePublisher.PUBLISH_TYPE_ORDERLY:
                    // 顺序消费
                    defaultMQProducer.send(msg, (messageQueue, message, arg) -> {
                        Integer id = (Integer) arg;
                        // System.out.println(messageQueue.get(id));
                        return messageQueue.get(id);
                    }, 0); // 默认四个队列中的0号队列 队列内有序
                    break;
                case MessagePublisher.PUBLISH_TYPE_TRANSACTION:
                    // 事务消费
                    // transactionMQProducer.sendMessageInTransaction(msg, tranExecuter, arg);
                    break;
                default:
                    // 普通消费
                    SendResult send = defaultMQProducer.send(msg);
                    log.info("发送结果 " + send.toString());
                    break;
            }
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.MESSAGE_MODEL_P_SEND_ERROR, e);
        }

    }
}
