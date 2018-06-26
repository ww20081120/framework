package com.hbasesoft.framework.message.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.MessagePublisher;
import com.hbasesoft.framework.message.rocketmq.config.RocketmqAutoConfiguration;

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
@Component
@ConditionalOnBean(DefaultMQProducer.class)
public class RocketmqMessagePublisher implements MessagePublisher {

	private static final Logger log = new Logger(RocketmqMessagePublisher.class);


	// @Autowired
	// private TransactionMQProducer transactionMQProducer;

	@Override
	public String getName() {
		return RocketmqAutoConfiguration.ROCKET_MQ_NAME;
	}

	@Override
	public void publish(String channel, byte[] data) {
		// 默认使用普通消费
		publish(channel, data, RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE);
	}

	/**
	 * produce_model: RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
	 * RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
	 * RocketmqAutoConfiguration.ROCKET_MQ_DEFAULT_PUBLISH_TYPE
	 */
	@Override
	public void publish(String channel, byte[] data, String produce_model) {
		
		DefaultMQProducer defaultMQProducer = ContextHolder.getContext().getBean("defaultProducer",DefaultMQProducer.class);

		// Create a message instance, specifying topic, tag and message body.
		Message msg = new Message(channel, "", data);

		try {
			switch (produce_model) {
			case RocketmqAutoConfiguration.ROCKET_MQ_PUBLISH_TYPE_ORDERLY:
				// 顺序消费
				// defaultMQProducer.send(msg, mq)
				break;
			case RocketmqAutoConfiguration.ROCKET_MQ_PUBLISH_TYPE_TRANSACTION:
				// 事务消费
				// transactionMQProducer.sendMessageInTransaction(msg, tranExecuter, arg);
				break;
			default:
				// 普通消费
				defaultMQProducer.send(msg);
				break;
			}
		} catch (Exception e) {
			log.error(e);
		}

	}

}
