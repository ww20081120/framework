/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.tx;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.event.EventIntercetorHolder;
import com.hbasesoft.framework.message.core.event.EventListener;
import com.hbasesoft.framework.tx.core.annotation.Tx;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jul 9, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.tx <br>
 */
public class TxEventStartupListener implements StartupListener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void complete(final ApplicationContext context) {
        Map<String, Object> beans = context.getBeansWithAnnotation(Tx.class);
        if (MapUtils.isNotEmpty(beans)) {
            TxEventInterceptor interceptor = new TxEventInterceptor();

            for (Entry<String, Object> entry : beans.entrySet()) {
                if (entry.getValue() instanceof EventListener) {
                    EventListener<?> eventLinsener = (EventListener<?>) entry.getValue();
                    Tx tx = AnnotationUtils.findAnnotation(eventLinsener.getClass(), Tx.class);

                    if (tx != null) {
                        EventIntercetorHolder.registInterceptor(interceptor, eventLinsener.events());
                        for (String event : eventLinsener.events()) {
                            TxEventHolder.put(event, tx);
                            LoggerUtil.info("success regist {0} TxEventInterceptor", event);
                        }
                    }
                }
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public LoadOrder getOrder() {
        return LoadOrder.MIDDLE;
    }
}
