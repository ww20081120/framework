/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.demo.event;

import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.message.core.event.EventLinsener;
import com.hbasesoft.framework.message.rocketmq.factory.RocketmqFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月24日 <br>
 * @see com.hbasesoft.test.event <br>
 * @since V1.0<br>
 */
@Service
public class TestEventHandler implements EventLinsener {

	/**
	 * Description: <br>
	 *
	 * @return <br>
	 * @author 王伟<br>
	 * @taskId <br>
	 */
	@Override
	public String[] events() {
		return new String[]{
				"testEvent"
		};
	}

	/**
	 * Description: <br>
	 *
	 * @param event
	 * @param data  <br>
	 * @author 王伟<br>
	 * @taskId <br>
	 */
	@Override
	public void onEmmit(String event, EventData data) {
		System.out.println(event + ":" + data);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> subscriberSetting() {
		HashMap<String, Object> stringObjectHashMap = new HashMap<>();
		stringObjectHashMap.put(RocketmqFactory.CONSUME_TYPE, RocketmqFactory.ROCKET_MQ_PUBLISH_TYPE_ORDERLY);
		return stringObjectHashMap;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10000; i++) {

			EventData data = new EventData();
			data.put("key", "value" + i);
			EventEmmiter.emmit("testEvent", data);
		}
	}

}
