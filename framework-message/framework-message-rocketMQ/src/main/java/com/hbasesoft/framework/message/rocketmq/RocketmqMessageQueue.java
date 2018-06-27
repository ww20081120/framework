/**
 * 
 */
package com.hbasesoft.framework.message.rocketmq;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.ServiceState;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.MessageQueue;
import com.hbasesoft.framework.message.rocketmq.factory.RocketmqFactory;

/**
 * <Description> <br>
 * 
 * @author 大刘杰<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.rocketmq <br>
 */
public class RocketmqMessageQueue implements MessageQueue {

	private static final Logger log = new Logger(RocketmqMessageQueue.class);

	private static ConcurrentLinkedQueue<byte[]> mesgQueue = new ConcurrentLinkedQueue<byte[]>();

	/**
	 * @Title: getName @author 大刘杰 @Description: TODO @param @return @return @throws
	 */
	@Override
	public String getName() {
		return RocketmqFactory.ROCKET_MQ_NAME;
	}

	/**
	 * @Title: push @author 大刘杰 @Description: TODO @param @param key @param @param
	 *         value @return @throws
	 */
	@Override
	public void push(String key, byte[] value) {
		push(key, value, RocketmqFactory.ROCKET_MQ_DEFAULT_PRODUCER_GROUP);
	}

	/**
	 * @Title: push @author 大刘杰 @Description: TODO @param @param key @param @param
	 *         value @return @throws
	 */
	public void push(String key, byte[] value, String producerGroup) {
		DefaultMQProducer defaultMQProducer = RocketmqFactory.getDefaultProducer(producerGroup);

		// Create a message instance, specifying topic, tag and message body.
		Message msg = new Message(key, "", value);

		try {
			defaultMQProducer.send(msg);
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * @Title: pop @author 大刘杰 @Description: TODO @param @param
	 *         timeout @param @param key @param @return @return @throws
	 */
	@Override
	public List<byte[]> pop(int timeout, String channel) {
		return pop(timeout, channel, RocketmqFactory.ROCKET_MQ_DEFAULT_CONSUMER_GROUP, false);
	};

	/**
	 * 
	 * @Title: pop @author 大刘杰 @Description: TODO @param timeout @param
	 * channel @param consumerGroup @param isConsumerBroadcasting @return @throws
	 */
	public List<byte[]> pop(int timeout, String channel, String consumerGroup, boolean isConsumerBroadcasting) {

		RocketmqFactory.getPushConsumer(channel, consumerGroup, isConsumerBroadcasting,
				new MessageListenerConcurrently() {
					@Override
					public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
							ConsumeConcurrentlyContext context) {
						try {
							if (msgs.size() == 0)
								return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
							// 事件监听
							for (MessageExt msg : msgs) {
								mesgQueue.add(msg.getBody());
							}
						} catch (Exception e) {
							e.printStackTrace();
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;
						}
						// 如果没有return success，consumer会重复消费此信息，直到success。
						return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
					}
				});

		List<byte[]> datas = new ArrayList<byte[]>();
		if (!mesgQueue.isEmpty()) {
			datas.add(mesgQueue.poll());
		}

		return datas;
	}

	/**
	 * @Title: popList @author 大刘杰 @Description: TODO @param @param
	 *         key @param @return @return @throws
	 */
	@Override
	public List<byte[]> popList(String key) {
		return pop(1, key);
	}
}
