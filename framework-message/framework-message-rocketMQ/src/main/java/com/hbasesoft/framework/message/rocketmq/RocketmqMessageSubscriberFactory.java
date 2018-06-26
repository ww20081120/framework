
package com.hbasesoft.framework.message.rocketmq;

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
import com.hbasesoft.framework.message.rocketmq.config.RocketmqAutoConfiguration;

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
@Component
@ConditionalOnBean(DefaultMQProducer.class)
public class RocketmqMessageSubscriberFactory implements MessageSubcriberFactory {

	private static final Logger log = new Logger(RocketmqMessageSubscriberFactory.class);

	private static boolean isFirstSub = true;

	private static long startTime = System.currentTimeMillis();

	// @Autowired
	// private ApplicationEventPublisher publisher;

	/**
	 * 
	 * @Title: getName @author 大刘杰 @Description: TODO @param @return @return @throws
	 */
	@Override
	public String getName() {
		return RocketmqAutoConfiguration.ROCKET_MQ_NAME;
	}

	/**
	 * 
	 * @Title: registSubscriber @author 大刘杰 @Description: TODO @param @param
	 *         channel @param @param subscriber @return @throws
	 */
	@Override
	public void registSubscriber(String channel, MessageSubscriber subscriber) {
		
		DefaultMQPushConsumer defaultMQPushConsumer = ContextHolder.getContext().getBean("defaultPushConsumer",DefaultMQPushConsumer.class);

		try {
			defaultMQPushConsumer.subscribe(channel, "*");
		} catch (MQClientException e) {
			log.error(e);
		}

		if (PropertyHolder.getBooleanProperty("message.rocketmq.consumer.isEnableOrderConsumer", false)) {
			// Orderly consume
			defaultMQPushConsumer.registerMessageListener((List<MessageExt> msgs, ConsumeOrderlyContext context) -> {
				try {
					context.setAutoCommit(true);
					msgs = filter(msgs);
					if (msgs.size() == 0)
						return ConsumeOrderlyStatus.SUCCESS;
					// 事件监听
					// this.publisher.publishEvent(new RocketmqEvent(msgs, defaultMQPushConsumer));
					// TODO 改成顺序消费写法
					subscriber.onMessage(msgs.get(0).getTopic(), msgs.get(0).getBody());
				} catch (Exception e) {
					e.printStackTrace();
					return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
				}
				// 如果没有return success，consumer会重复消费此信息，直到success 。
				return ConsumeOrderlyStatus.SUCCESS;
			});
		} else {
			defaultMQPushConsumer
					.registerMessageListener((List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
						try {
							msgs = filter(msgs);
							if (msgs.size() == 0)
								return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
							// 事件监听
							// this.publisher.publishEvent(new RocketmqEvent(msgs, defaultMQPushConsumer));
							subscriber.onMessage(msgs.get(0).getTopic(), msgs.get(0).getBody());
						} catch (Exception e) {
							e.printStackTrace();
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;
						}
						// 如果没有return success，consumer会重复消费此信息，直到success。
						return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
					});
		}
		
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

	}

	/**
	 * 是否允许历史消费
	 * 
	 * @param msgs
	 * @return
	 */
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
