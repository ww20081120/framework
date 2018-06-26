package com.hbasesoft.framework.message.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

//@Component
//@ConditionalOnBean(DefaultMQProducer.class)
public class RocketmqConsumerEventListener {
	// @EventListener(condition = "#event.msgs[0].topic=='TopicTest1' &&
	// #event.msgs[0].tags=='TagA'")
	@EventListener
	public void rocketmqMsgListen(RocketmqEvent event) {
		// DefaultMQPushConsumer consumer = event.getConsumer();
		try {
			System.out
					.println("监听到一个消息达到：" + event.getMsgs().get(0).getMsgId());
			// TODO 进行业务处理
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}