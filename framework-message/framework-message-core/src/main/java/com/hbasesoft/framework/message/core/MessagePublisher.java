/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月19日 <br>
 * @see com.hbasesoft.framework.message.core <br>
 * @since V1.0<br>
 */
public interface MessagePublisher {

	String getName();

	/**
	 * Description: 发布<br>
	 *
	 * @param channel
	 * @param data    <br>
	 * @author 王伟<br>
	 * @taskId <br>
	 */
	void publish(String channel, byte[] data);

	/**
	 * publish:设定延迟消费 <br/>
	 *
	 * @param channel
	 * @param data
	 * @param delayLevel
	 * @author 大刘杰
	 * @since JDK 1.8
	 */
	default void publish(String channel, byte[] data, Long delayTime) {
	}

	/**
	 * publish:指定消费模式 <br/>
	 *
	 * @param channel
	 * @param data
	 * @param delayLevel
	 * @author 大刘杰
	 * @since JDK 1.8
	 */
	default void publish(String channel, byte[] data, String produceModel) {
	}
}
