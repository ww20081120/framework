package com.hbasesoft.framework.message.rocketmq.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;

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
@Configuration
public class RocketmqAutoConfiguration {

	private static final Logger log = new Logger(RocketmqAutoConfiguration.class);

	public static final String ROCKET_MQ_NAME = "ROCKET_MQ";

	// 普通消费
	public static final String ROCKET_MQ_DEFAULT_PUBLISH_TYPE = "NORMAL";
	// 顺序消费
	public static final String ROCKET_MQ_PUBLISH_TYPE_ORDERLY = "ORDERLY";
	// 事务消费
	public static final String ROCKET_MQ_PUBLISH_TYPE_TRANSACTION = "TRANSACTION";

	/**
	 * 初始化向rocketmq发送普通消息的生产者
	 */
	@Bean(name = "defaultProducer")
	@ConditionalOnProperty(prefix = RocketmqProperties.PREFIX, value = "producerGroupName")
	public DefaultMQProducer defaultProducer() throws MQClientException {
		/**
		 * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ProducerGroupName需要由应用来保证唯一<br>
		 * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
		 * 因为服务器会回查这个Group下的任意一个Producer
		 */
		// Producer Group Name
		DefaultMQProducer producer = new DefaultMQProducer(
				PropertyHolder.getProperty("message.rocketmq.producer.producerGroupName"));

		// Name service address
		producer.setNamesrvAddr(PropertyHolder.getProperty("message.rocketmq.namesrvAddr"));

		// Defalut value ip@pid when not set , this key used for cluster
		// producer.setInstanceName(properties.getProducerInstanceName());

		// vip netty channel
		producer.setVipChannelEnabled(false);

		// Producer retry times
		producer.setRetryTimesWhenSendAsyncFailed(10);

		/**
		 * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
		 * 注意：切记不可以在每次发送消息时，都调用start方法
		 */
		producer.start();
		log.info("RocketMq defaultProducer Started.");
		return producer;
	}

	/**
	 * 初始化向rocketmq发送事务消息的生产者
	 */
	@Bean(name = "transactionProducer")
	@ConditionalOnProperty(prefix = RocketmqProperties.PREFIX, value = "transactionProducerGroupName")
	public TransactionMQProducer transactionProducer() throws MQClientException {
		/**
		 * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ProducerGroupName需要由应用来保证唯一<br>
		 * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
		 * 因为服务器会回查这个Group下的任意一个Producer
		 */
		// Producer Group Name
		TransactionMQProducer producer = new TransactionMQProducer(
				PropertyHolder.getProperty("message.rocketmq.producer.transactionProducerGroupName"));

		// Name service address
		producer.setNamesrvAddr(PropertyHolder.getProperty("message.rocketmq.namesrvAddr"));

		// Defalut value ip@pid when not set , this key used for cluster
		// producer.setInstanceName(properties.getProducerTranInstanceName());

		// Retry times
		producer.setRetryTimesWhenSendAsyncFailed(10);

		// 事务回查最小并发数
		producer.setCheckThreadPoolMinSize(2);
		// 事务回查最大并发数
		producer.setCheckThreadPoolMaxSize(2);
		// 队列数
		producer.setCheckRequestHoldMax(2000);

		// TODO 由于社区版本的服务器阉割调了消息回查的功能，所以这个地方没有意义
		// TransactionCheckListener transactionCheckListener = new
		// TransactionCheckListenerImpl();
		// producer.setTransactionCheckListener(transactionCheckListener);

		/**
		 * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
		 * 注意：切记不可以在每次发送消息时，都调用start方法
		 */
		producer.start();

		log.info("RocketMq TransactionMQProducer Started.");
		return producer;
	}

	/**
	 * 初始化rocketmq消息监听方式的消费者
	 */
	@Bean(name = "defaultPushConsumer")
	// @Scope("prototype")
	// @ConditionalOnProperty(prefix = RocketmqProperties.PREFIX, value =
	// "consumerInstanceName")
	public DefaultMQPushConsumer pushConsumer() throws MQClientException {

		// Consumer Group Name
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(
				PropertyHolder.getProperty("message.rocketmq.consumer.consumerGroupName"));

		// Name service address
		consumer.setNamesrvAddr(PropertyHolder.getProperty("message.rocketmq.namesrvAddr"));

		// Defalut value ip@pid when not set , this key used for cluster
		// consumer.setInstanceName(properties.getConsumeCrInstanceName());

		// Message Model
		if (PropertyHolder.getBooleanProperty("message.rocketmq.consumer.isConsumerBroadcasting", false)) {
			consumer.setMessageModel(MessageModel.BROADCASTING);
		}

		// One time consume max size
		consumer.setConsumeMessageBatchMaxSize(
				PropertyHolder.getIntProperty("message.rocketmq.consumer.consumerBatchMaxSize", 0) == 0 ? 1
						: PropertyHolder.getIntProperty("message.rocketmq.consumer.consumerBatchMaxSize", 0));// 设置批量消费，以提升消费吞吐量，默认是1

		return consumer;
	}

}
