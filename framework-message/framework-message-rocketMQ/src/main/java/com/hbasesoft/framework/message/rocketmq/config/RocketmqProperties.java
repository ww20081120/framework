package com.hbasesoft.framework.message.rocketmq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
@ConfigurationProperties(RocketmqProperties.PREFIX)
public class RocketmqProperties {
	public static final String PREFIX = "message.rocketmq.producer";
	private String producerGroupName;
	private String transactionProducerGroupName;


	public String getProducerGroupName() {
		return producerGroupName;
	}

	public void setProducerGroupName(String producerGroupName) {
		this.producerGroupName = producerGroupName;
	}

	public String getTransactionProducerGroupName() {
		return transactionProducerGroupName;
	}

	public void setTransactionProducerGroupName(String transactionProducerGroupName) {
		this.transactionProducerGroupName = transactionProducerGroupName;
	}
}


