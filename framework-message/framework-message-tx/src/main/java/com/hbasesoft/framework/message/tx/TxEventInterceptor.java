/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.tx;

import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventInterceptor;
import com.hbasesoft.framework.tx.core.TxInvokerProxy;
import com.hbasesoft.framework.tx.core.TxManager;
import com.hbasesoft.framework.tx.core.TxProducer;
import com.hbasesoft.framework.tx.core.annotation.Tx;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.core.util.ArgsSerializationUtil;

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
public class TxEventInterceptor implements EventInterceptor {

    /** Number */
    private static final int NUM_5 = 5;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param eventData
     * @param seconds
     * @param produceModel
     * @return <br>
     */
    @Override
    public boolean sendBefore(final String channel, final EventData<?> eventData, final Integer seconds,
        final String produceModel) {
        if (seconds != null) {
            TxInvokerProxy.registInvoke(getClientInfo(channel, eventData.getMsgId(), new Object[] {
                channel, eventData, seconds
            }, "emmit2"), () -> 0);
        }
        else if (produceModel != null) {
            TxInvokerProxy.registInvoke(getClientInfo(channel, eventData.getMsgId(), new Object[] {
                channel, eventData, produceModel
            }, "emmit3"), () -> 0);
        }
        else {
            TxInvokerProxy.registInvoke(getClientInfo(channel, eventData.getMsgId(), new Object[] {
                channel, eventData
            }, "emmit1"), () -> 0);
        }
        return true;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param eventData
     * @return <br>
     */
    @Override
    public boolean receiveBefore(final String channel, final EventData<?> eventData) {
        String id = eventData.getTracerId();
        TxProducer sender = TxInvokerProxy.getSender();
        if (sender.containClient(id)) {
            TxManager.setTraceId(id);
            return true;
        }
        return false;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param eventData <br>
     */
    @Override
    public void receiveAfter(final String channel, final EventData<?> eventData) {
        String id = TxManager.getTraceId();
        if (id != null) {
            TxManager.setTraceId(null);
            TxProducer sender = TxInvokerProxy.getSender();
            sender.removeClient(id);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param eventData
     * @param e <br>
     */
    @Override
    public void receiveError(final String channel, final EventData<?> eventData, final Exception e) {
        TxManager.setTraceId(null);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param id
     * @param args
     * @param method
     * @return <br>
     */
    private static ClientInfo getClientInfo(final String channel, final String id, final Object[] args,
        final String method) {
        ClientInfo clientInfo = new ClientInfo(id, TxEventRetryHandler.TX_EVENT_RETRY_HANDLER + method);
        clientInfo.setArgs(ArgsSerializationUtil.serializeArgs(args));
        clientInfo.setClientInfo(TxInvokerProxy.getClientInfoFactory().getClientInfo());

        Tx tx = TxEventHolder.get(channel);
        if (tx != null) {
            clientInfo.setMaxRetryTimes(tx.maxRetryTimes());
            clientInfo.setRetryConfigs(tx.retryConfigs());
        }
        else {
            clientInfo.setMaxRetryTimes(NUM_5);
            clientInfo.setRetryConfigs("5,10,30,60,120,720");
        }

        return clientInfo;
    }
}
