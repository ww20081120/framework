/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.tx;

import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.tx.core.TxInvokerProxy;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.core.util.ArgsSerializationUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TxEventEmmiter {

    /** Number */
    private static final int NUM_5 = 5;

    /**
     * Description: 触发事件<br>
     *
     * @param event <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    public static void emmit(String event) {
        emmit(event, new EventData());
    }

    /**
     * Description: 触发事件<br>
     *
     * @param event
     * @param data <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    public static void emmit(String event, EventData data) {
        TxInvokerProxy.registInvoke(getClientInfo(data.getMsgId(), new Object[] {
            event, data
        }, "emmit1"), () -> 0);
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
    public static void emmit(String event, EventData data, int seconds) {
        TxInvokerProxy.registInvoke(getClientInfo(data.getMsgId(), new Object[] {
            event, data, seconds
        }, "emmit2"), () -> 0);
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
    public static void emmit(String event, EventData data, String produceModel) {
        TxInvokerProxy.registInvoke(getClientInfo(data.getMsgId(), new Object[] {
            event, data, produceModel
        }, "emmit3"), () -> 0);
        EventEmmiter.emmit(event, data, produceModel);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param args
     * @param method
     * @return <br>
     */
    private static ClientInfo getClientInfo(String id, Object[] args, String method) {
        ClientInfo clientInfo = new ClientInfo(id, TxEventRetryHandler.TX_EVENT_RETRY_HANDLER + method);
        clientInfo.setArgs(ArgsSerializationUtil.serializeArgs(args));
        clientInfo.setClientInfo(TxInvokerProxy.getClientInfoFactory().getClientInfo());
        clientInfo.setMaxRetryTimes(NUM_5);
        clientInfo.setRetryConfigs("5,10,30,60,120,720");
        return clientInfo;
    }
}
