
package com.hbasesoft.framework.message.rocketmq;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.MessageSubcriberFactory;
import com.hbasesoft.framework.message.core.MessageSubscriber;
import com.hbasesoft.framework.message.rocketmq.factory.RocketmqFactory;

/**
 * <Description> <br>
 * 
 * @author 大刘杰<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.rocketmq <br>
 */
public class RocketmqMessageSubscriberFactory implements MessageSubcriberFactory {

	private static final Logger log = new Logger(RocketmqMessageSubscriberFactory.class);

	private static boolean isFirstSub = true;

	private static long startTime = System.currentTimeMillis();

	/**
	 * 
	 * @Title: getName @author 大刘杰 @Description: TODO @param @return @return @throws
	 */
	@Override
	public String getName() {
		return RocketmqFactory.ROCKET_MQ_NAME;
	}

	/**
	 * 
	 * @Title: registSubscriber @author 大刘杰 @Description: TODO @param @param
	 *         channel @param @param subscriber @return @throws
	 */
	@Override
	public void registSubscriber(String channel, MessageSubscriber subscriber) {
		RocketmqFactory.getPushConsumer(channel, "consumerGroupNameregistSubscriber", true,
				(List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
					try {
						// msgs = filter(msgs);
						if (msgs.size() == 0)
							return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
						// 事件监听
						for (MessageExt messageExt : msgs) {
							subscriber.onMessage(messageExt.getTopic(), messageExt.getBody());
						}
					} catch (Exception e) {
						log.error(e);
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
					// 如果没有return success，consumer会重复消费此信息，直到success。
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				});
	}

	/**
	 * 是否允许历史消费
	 * 
	 * @param msgs
	 * @return
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
