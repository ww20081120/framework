/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.StartupListenerAdapter;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.core.MessageSubcriberFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.event <br>
 */
public class EventHandlerStartupLinstener extends StartupListenerAdapter {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @throws FrameworkException <br>
     */
    @Override
    public void complete(ApplicationContext context) throws FrameworkException {
        Map<String, EventLinsener> eventLinseners = context.getBeansOfType(EventLinsener.class);
        if (CommonUtil.isNotEmpty(eventLinseners)) {
            MessageSubcriberFactory factory = MessageHelper.createMessageSubcriberFactory();
            for (Entry<String, EventLinsener> entry : eventLinseners.entrySet()) {
                EventLinsener linsener = entry.getValue();
                String[] events = linsener.events();
                if (CommonUtil.isNotEmpty(events)) {
                    for (String channel : linsener.events()) {
                        factory.registSubscriber(channel, linsener);
                    }
                }
            }
        }
    }

}
