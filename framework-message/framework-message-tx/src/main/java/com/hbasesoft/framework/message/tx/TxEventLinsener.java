/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.tx;

import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventLinsener;
import com.hbasesoft.framework.tx.core.TxInvokerProxy;
import com.hbasesoft.framework.tx.core.TxManager;
import com.hbasesoft.framework.tx.core.TxProducer;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 4, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.tx <br>
 */
public interface TxEventLinsener extends EventLinsener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data <br>
     */
    @Override
    default void onMessage(String channel, byte[] data) {
        EventData eventData = SerializationUtil.unserial(EventData.class, data);
        String id = eventData.getMsgId();
        TxProducer sender = TxInvokerProxy.getSender();
        if (sender.containClient(id)) {
            try {
                TxManager.setTraceId(id);
                LoggerUtil.debug("[{0}]接收到[event={1},data={2}]事件", Thread.currentThread().getId(), channel, eventData);
                onEmmit(channel, eventData);
                sender.removeClient(id);
            }
            finally {
                TxManager.setTraceId(null);
            }
        }
    }
}
