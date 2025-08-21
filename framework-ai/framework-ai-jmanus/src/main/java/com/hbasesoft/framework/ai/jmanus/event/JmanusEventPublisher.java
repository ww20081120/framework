/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.event <br>
 */

@Component
public class JmanusEventPublisher {

	// Listeners cannot be dynamically registered, no need for thread safety
	private Map<Class<? extends JmanusEvent>, List<JmanusListener<? super JmanusEvent>>> listeners = new HashMap<>();

	public void publish(JmanusEvent event) {
		Class<? extends JmanusEvent> eventClass = event.getClass();
		for (Map.Entry<Class<? extends JmanusEvent>, List<JmanusListener<? super JmanusEvent>>> entry : listeners
			.entrySet()) {
			// Parent classes can also be notified here
			if (entry.getKey().isAssignableFrom(eventClass)) {
				for (JmanusListener<? super JmanusEvent> listener : entry.getValue()) {
					try {
						listener.onEvent(event);
					}
					catch (Exception e) {
						LoggerUtil.error(e, "Error occurred while processing event: {0}", e.getMessage());
					}
				}
			}
		}
	}

	void registerListener(Class<? extends JmanusEvent> eventClass, JmanusListener<? super JmanusEvent> listener) {
		List<JmanusListener<? super JmanusEvent>> jmanusListeners = listeners.get(eventClass);
		if (jmanusListeners == null) {
			List<JmanusListener<? super JmanusEvent>> list = new ArrayList<>();
			list.add(listener);
			listeners.put(eventClass, list);
		}
		else {
			jmanusListeners.add(listener);
		}
	}

}