/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.hbasesoft.framework.common.Bootstrap;
import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.message.rocketmq.RocketmqMessagePublisher;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年1月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.sgp.bootstrap.plat <br>
 */
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = "com.hbasesoft")
@ImportResource("classpath*:META-INF/spring/*.xml")
@Configuration
public class MessageServApplicationTest {
	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param args
	 *            <br>
	 */
	// @Autowired
	// private static RocketmqMessageSubscribe rms;

	// @Autowired
	// private RocketmqMessagePublisher rocketmqMessagePublisher;
	//
	public static void main(String[] args) {
		Bootstrap.before();
		ConfigurableApplicationContext context = SpringApplication.run(MessageServApplicationTest.class, args);
		Bootstrap.after(context);

		EventData eventData = new EventData();
		for (int i = 0; i < 10; i++) {
			eventData.put("topic1", "Hello word" + i);

		}
		
		// RocketmqMessagePublisher rocketmqMessagePublisher =
		// (RocketmqMessagePublisher)context.getBean(RocketmqMessagePublisher.class);
		// for (int i = 0; i < 10; i++) {
		// rocketmqMessagePublisher.publish("topic1", ("hello word" + i).getBytes());
		// }
	}
}
