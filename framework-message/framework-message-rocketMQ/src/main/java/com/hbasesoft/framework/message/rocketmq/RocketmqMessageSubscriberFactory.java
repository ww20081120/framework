
package com.hbasesoft.framework.message.rocketmq;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.MessagePublisher;
import com.hbasesoft.framework.message.core.MessageSubcriberFactory;
import com.hbasesoft.framework.message.core.MessageSubscriber;
import com.hbasesoft.framework.message.rocketmq.factory.RocketmqFactory;

/**
 * ClassName: RocketmqMessageSubscriberFactory <br/>
 * Function: 设定Rocket的消费者 <br/>
 * date: 2018年6月27日 下午9:46:52 <br/>
 *
 * @author 大刘杰
 * @since JDK 1.8
 */
public class RocketmqMessageSubscriberFactory implements MessageSubcriberFactory {

    private static final Logger log = new Logger(RocketmqMessageSubscriberFactory.class);

    private static boolean isFirstSub = true;

    private static long startTime = System.currentTimeMillis();

    /**
     * @see com.hbasesoft.framework.message.core.MessageSubcriberFactory#getName()
     */
    @Override
    public String getName() {
        return RocketmqFactory.ROCKET_MQ_NAME;
    }

    /**
     * @see com.hbasesoft.framework.message.core.MessageSubcriberFactory#registSubscriber(String, boolean,
     *      MessageSubscriber)
     */
    @Override
    public void registSubscriber(String channel, boolean broadcast, MessageSubscriber subscriber) {

        subscriber.onSubscribe(channel, 1);
        Map<String, Object> subscriberSetting = subscriber.subscriberSetting();

        switch (String.valueOf(subscriberSetting.get(RocketmqFactory.CONSUME_TYPE))) {
            case MessagePublisher.PUBLISH_TYPE_ORDERLY:
                // 顺序消费
                log.info("启动顺序消费");
                consumeOrderly(channel, broadcast, subscriber);
                break;
            case MessagePublisher.PUBLISH_TYPE_TRANSACTION:
                // 事务消费
                // transactionMQProducer.sendMessageInTransaction(msg, tranExecuter, arg);
                break;
            default:
                // 普通并发消费
                log.info("启动普通并发消费");
                consumeConcurrently(channel, broadcast, subscriber);
                break;
        }
    }

    private void consumeOrderly(String channel, boolean broadcast, MessageSubscriber subscriber) {
        RocketmqFactory.getPushConsumer(channel, channel, broadcast, (MessageListenerOrderly) (msgs, context) -> {
            // 自动提交 更新消费队列的位置
            context.setAutoCommit(true);
            if (msgs.size() == 0) {
                return ConsumeOrderlyStatus.SUCCESS;
            }

            // 事件监听
            for (MessageExt messageExt : msgs) {
                subscriber.onMessage(messageExt.getTopic(), messageExt.getBody());
            }
            return ConsumeOrderlyStatus.SUCCESS;
        });
    }

    private void consumeConcurrently(String channel, boolean broadcast, MessageSubscriber subscriber) {

        RocketmqFactory.getPushConsumer(channel, channel, broadcast,
            (MessageListenerConcurrently) (List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
                try {
                    // msgs = filter(msgs);
                    if (msgs.size() == 0) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    // 事件监听
                    for (MessageExt messageExt : msgs) {
                        subscriber.onMessage(messageExt.getTopic(), messageExt.getBody());
                    }
                }
                catch (Exception e) {
                    log.error(e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                // 如果没有return success，consumer会重复消费此信息，直到success。
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
    }

    /**
     * filter:. <br/>
     *
     * @param msgs
     * @return
     * @author 大刘杰
     * @since JDK 1.8
     */
    @SuppressWarnings("unused")
    private List<MessageExt> filter(List<MessageExt> msgs) {
        if (isFirstSub && !PropertyHolder.getBooleanProperty("message.rocketmq.consumer.isEnableHisConsumer", false)) {
            msgs = msgs.stream().filter(item -> startTime - item.getBornTimestamp() < 0).collect(Collectors.toList());
        }
        if (isFirstSub && msgs.size() > 0) {
            isFirstSub = false;
        }
        return msgs;
    }

}
