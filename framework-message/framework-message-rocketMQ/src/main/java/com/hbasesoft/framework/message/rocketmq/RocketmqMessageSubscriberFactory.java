
package com.hbasesoft.framework.message.rocketmq;

import com.hbasesoft.framework.common.GlobalConstants;
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
 * @see com.framework.message.rocketmq <br>
 */
public class RocketmqMessageSubscriberFactory implements MessageSubcriberFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hbasesoft.framework.message.core.MessageSubcriberFactory#getName()
	 */
	@Override
	public String getName() {
		return RocketmqAutoConfiguration.ROCKET_MQ_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hbasesoft.framework.message.core.MessageSubcriberFactory#registSubscriber
	 * (java.lang.String, com.hbasesoft.framework.message.core.MessageSubscriber)
	 */
	@Override
	public void registSubscriber(String channel, MessageSubscriber subscriber) {
		subscriber.onSubscribe(channel, 1); // TODO
		subscriber.onMessage(channel, GlobalConstants.BLANK.getBytes());
		// subscriber.onUnsubscribe(channel, 1); // TODO
	}

}
