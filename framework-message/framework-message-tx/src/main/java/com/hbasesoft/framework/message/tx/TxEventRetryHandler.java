/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.tx;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.tx.core.annotation.Tx;

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
@Component
public class TxEventRetryHandler {

    /** 事件注册 */
    public static final String TX_EVENT_RETRY_HANDLER = "TX_EVENT_RETRY_HANDLER_";

    /**
     * Description: 触发事件<br>
     *
     * @param event
     * @param data <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Tx(name = TX_EVENT_RETRY_HANDLER + "emmit1")
    public void emmit1(final String event, final EventData<?> data) {
        EventEmmiter.emmit(event, data);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @param data
     * @param seconds <br>
     */
    @Tx(name = TX_EVENT_RETRY_HANDLER + "emmit2")
    public void emmit2(final String event, final EventData<?> data, final int seconds) {
        EventEmmiter.emmit(event, data, seconds);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @param data
     * @param produceModel <br>
     */
    @Tx(name = TX_EVENT_RETRY_HANDLER + "emmit3")
    public void emmit3(final String event, final EventData<?> data, final String produceModel) {
        EventEmmiter.emmit(event, data, produceModel);
    }
}
