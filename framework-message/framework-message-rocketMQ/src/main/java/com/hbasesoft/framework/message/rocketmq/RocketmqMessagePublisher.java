package com.hbasesoft.framework.message.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.MessagePublisher;
import com.hbasesoft.framework.message.rocketmq.factory.RocketmqFactory;

/**
 * 
 * <Description> <br>
 * 
 * @author 大刘杰<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.rocketmq <br>
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
		publish(channel, data, RocketmqFactory.ROCKET_MQ_DEFAULT_PUBLISH_TYPE, channel, 0);
	}
	
	
	public void publish(String channel, byte[] data, int delayLevel) {
		// 默认使用普通消费
		publish(channel, data, RocketmqFactory.ROCKET_MQ_DEFAULT_PUBLISH_TYPE, channel, delayLevel);
	}

	/**
	 * 
	 * publish:. <br/>
	 * 
	 * @author 大刘杰
	 * @param channel
	 * @param data
	 * @param produce_model
	 * @param producerGroup
	 * @since JDK 1.8
	 * @produce_model: RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
	 *                 RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
	 *                 RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
	 */
	public void publish(String channel, byte[] data, String produce_model, String producerGroup, int delayLevel) {

		if (GlobalConstants.BLANK.equals(producerGroup.trim())) {
			log.error("producerGroup cannot be empty");
			return;
		}

		DefaultMQProducer defaultMQProducer = RocketmqFactory.getDefaultProducer(producerGroup);

		// Create a message instance, specifying topic, tag and message body.
		Message msg = new Message(channel, GlobalConstants.BLANK, data);

		// Set delay level
		if (delayLevel != 0) {
			msg.setDelayTimeLevel(delayLevel);
		}

		try {
			switch (produce_model) {
			case RocketmqFactory.ROCKET_MQ_PUBLISH_TYPE_ORDERLY:
				// 顺序消费
				// defaultMQProducer.send(msg, mq)
				break;
			case RocketmqFactory.ROCKET_MQ_PUBLISH_TYPE_TRANSACTION:
				// 事务消费
				// transactionMQProducer.sendMessageInTransaction(msg, tranExecuter, arg);
				break;
			default:
				// 普通消费
				SendResult send = defaultMQProducer.send(msg);
				log.info(send.toString());
				break;
			}
		} catch (Exception e) {
			log.error(e);
		}

	}
}
