/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 10, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.core <br>
 */
public final class TxInvokerProxy {

    private static final Object LOCK = new Object();

    private static TxProducer sender;

    public static <T> T registInvoke(ClientInfo clientInfo, TxInvoker invoker) throws Throwable {
        TxProducer sender = getSender();
        TxManager.setTraceId(clientInfo.getId());
        sender.registClient(clientInfo);
        T msg = invoker.invoke();
        sender.removeClient(clientInfo.getId());
        return msg;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invoke(String marker, TxInvoker invoker) throws Throwable {
        TxProducer sender = getSender();

        CheckInfo checkInfo = sender.registMsg(TxManager.getTraceId(), marker);
        if (checkInfo.getFlag() != 0) {
            T msg = invoker.invoke();
            checkInfo.setResult(msg);
            sender.saveResult(checkInfo);
            return msg;
        }
        return (T) checkInfo.getResult();
    }

    private static TxProducer getSender() {
        synchronized (LOCK) {
            if (sender == null) {
                ServiceLoader<TxProducer> producerLoader = ServiceLoader.load(TxProducer.class);
                Iterator<TxProducer> it = producerLoader.iterator();
                Assert.isTrue(it.hasNext(), ErrorCodeDef.TRASCATION_SENDER_NOT_FOUND);
                sender = it.next();
            }
            return sender;
        }
    }
}
