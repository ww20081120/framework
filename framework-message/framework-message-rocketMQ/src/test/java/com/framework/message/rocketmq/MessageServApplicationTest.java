/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.framework.message.rocketmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.hbasesoft.framework.common.Bootstrap;
import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.rocketmq.RocketmqMessagePublisher;
import com.hbasesoft.framework.message.rocketmq.RocketmqMessageSubscribe;
import com.hbasesoft.framework.message.rocketmq.RocketmqMessageSubscriberFactory;

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
//	@Autowired
//	private static RocketmqMessageSubscribe rms;
	
//	@Autowired
//	private RocketmqMessagePublisher rocketmqMessagePublisher;
//	
	public static void main(String[] args) {
		Bootstrap.before();
		ConfigurableApplicationContext context = SpringApplication.run(MessageServApplicationTest.class, args);
		Bootstrap.after(context);
		
		RocketmqMessagePublisher rocketmqMessagePublisher = (RocketmqMessagePublisher)context.getBean(RocketmqMessagePublisher.class);
		RocketmqMessageSubscribe rocketmqMessageSubscribe = (RocketmqMessageSubscribe)context.getBean(RocketmqMessageSubscribe.class);
//		RocketmqMessagePublisher rmp = (RocketmqMessagePublisher)MessageHelper.createMessagePublisher();
		rocketmqMessagePublisher.publish("topic1", "hello word".getBytes());
		RocketmqMessageSubscriberFactory rmsf = (RocketmqMessageSubscriberFactory)MessageHelper.createMessageSubcriberFactory();
		rmsf.registSubscriber("topic1", rocketmqMessageSubscribe);
	}
}
