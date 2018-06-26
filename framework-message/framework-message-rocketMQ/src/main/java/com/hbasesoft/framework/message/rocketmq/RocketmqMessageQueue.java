/**
 * 
 */
package com.hbasesoft.framework.message.rocketmq;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.MessageQueue;
import com.hbasesoft.framework.message.rocketmq.config.RocketmqAutoConfiguration;

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

	/**
	 * @Title: getName @author 大刘杰 @Description: TODO @param @return @return @throws
	 */
	@Override
	public String getName() {
		return RocketmqAutoConfiguration.ROCKET_MQ_NAME;
	}

	/**
	 * @Title: push @author 大刘杰 @Description: TODO @param @param key @param @param
	 *         value @return @throws
	 */
	@Override
	public void push(String key, byte[] value) {
		DefaultMQProducer defaultMQProducer = ContextHolder.getContext().getBean("defaultProducer",
				DefaultMQProducer.class);

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

		DefaultMQPushConsumer defaultMQPushConsumer = ContextHolder.getContext().getBean("defaultPushConsumer",
				DefaultMQPushConsumer.class);

		List<byte[]> datas = new ArrayList<byte[]>();

		try {
			defaultMQPushConsumer.subscribe(channel, "*");
		} catch (MQClientException e) {
			log.error(e);
		}

		defaultMQPushConsumer.registerMessageListener((List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
			try {
				if (msgs.size() == 0)
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				// 事件监听
				// this.publisher.publishEvent(new RocketmqEvent(msgs, defaultMQPushConsumer));
				datas.add(msgs.get(0).getBody());
			} catch (Exception e) {
				e.printStackTrace();
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
			// 如果没有return success，consumer会重复消费此信息，直到success。
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 延迟5秒再启动，主要是等待spring事件监听相关程序初始化完成，否则，回出现对RocketMQ的消息进行消费后立即发布消息到达的事件，然而此事件的监听程序还未初始化，从而造成消息的丢失
					Thread.sleep(5000);
					// Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
					try {
						defaultMQPushConsumer.start();
					} catch (Exception e) {
						log.info("RocketMq pushConsumer Start failure!!!.");
						log.error(e.getMessage(), e);
					}
					log.info("RocketMq pushConsumer Started.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

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
