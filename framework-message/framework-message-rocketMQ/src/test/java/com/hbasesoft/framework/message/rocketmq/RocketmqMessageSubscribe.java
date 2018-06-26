/**
 * 
 */
package com.hbasesoft.framework.message.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.MessageSubscriber;

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
//@Component
//@ConditionalOnBean(DefaultMQProducer.class)
public class RocketmqMessageSubscribe implements MessageSubscriber {

	@Autowired
	private ApplicationContext app;

	@Autowired
	private DefaultMQPushConsumer defaultMQPushConsumer;

	private Logger log = new Logger(RocketmqMessageSubscribe.class);

	// public RocketmqMessageSubscribe() {
	// defaultMQPushConsumer = (DefaultMQPushConsumer)
	// app.getBean(DefaultMQPushConsumer.class);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hbasesoft.framework.message.core.MessageSubscriber#onMessage(java.lang.
	 * String, byte[])
	 */
	@Override
	public void onMessage(String channel, byte[] data) {
		try {

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

		} catch (Exception e) {
			log.info("RocketMq pushConsumer Start failure!!!.");
			log.error(e.getMessage(), e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hbasesoft.framework.message.core.MessageSubscriber#onSubscribe(java.lang.
	 * String, int)
	 */
	@Override
	public void onSubscribe(String channel, int subscribeChannels) {
		// subscribe topic and subkeys eg.[channel:subscribeChannels]
		// consumer.subscribe("TopicTest", "TagA || TagC || TagD");
		try {
			defaultMQPushConsumer.subscribe(channel, "*");
		} catch (MQClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hbasesoft.framework.message.core.MessageSubscriber#onUnsubscribe(java.
	 * lang.String, int)
	 */
	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		defaultMQPushConsumer.unsubscribe(channel);
	}

	/**
	 * 
	 * @Title: shutdown @author 大刘杰 @Description: 终止消费者 @param @return @throws
	 */
	public void shutdown() {
		defaultMQPushConsumer.shutdown();
	}
}
