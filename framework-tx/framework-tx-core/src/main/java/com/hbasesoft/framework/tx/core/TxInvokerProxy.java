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
import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;

/**
 * <Description> 代理执行类<br>
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

    private static TxClientInfoFactory clientInfoFactory;

    /**
     * Description: 注册代理<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo 客户端信息
     * @param invoker 具体的执行类
     * @return
     * @throws Throwable <br>
     */
    public static <T> T registInvoke(ClientInfo clientInfo, TxInvoker<T> invoker) {
        clientInfo.setClientInfo(getClientInfoFactory().getClientInfo());
        TxProducer sender = getSender();
        boolean flag = sender.registClient(clientInfo);
        T msg = invoker.invoke();
        if (flag) {
            sender.removeClient(clientInfo.getId());
        }
        return msg;
    }

    /**
     * Description: 执行代理方法 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param marker
     * @param invoker
     * @return
     * @throws Throwable <br>
     */
    public static <T> T invoke(String marker, TxInvoker<T> invoker) {
        return invoke(TxManager.getTraceId(), marker, invoker);
    }

    /**
     * Description: 执行代理方法 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param marker
     * @param invoker
     * @return
     * @throws Throwable <br>
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(String id, String mark, TxInvoker<T> invoker) {
        TxProducer sender = getSender();

        CheckInfo checkInfo = sender.check(id, mark);
        if (checkInfo == null) {
            T msg = invoker.invoke();
            checkInfo = new CheckInfo(id, mark);
            if (msg != null) {
                checkInfo.setResult(SerializationUtil.jdkSerial(msg));
            }
            sender.saveResult(checkInfo);
            return msg;
        }
        byte[] result = checkInfo.getResult();
        return result != null && result.length > 0 ? (T) SerializationUtil.jdkUnserial(result) : null;
    }

    static TxClientInfoFactory getClientInfoFactory() {
        synchronized (LOCK) {
            if (clientInfoFactory == null) {
                ServiceLoader<TxClientInfoFactory> loader = ServiceLoader.load(TxClientInfoFactory.class);
                Iterator<TxClientInfoFactory> it = loader.iterator();
                Assert.isTrue(it.hasNext(), ErrorCodeDef.TRANS_CLIENT_INFO_FACTORY_NOT_FOUND);
                clientInfoFactory = it.next();
            }
            return clientInfoFactory;
        }
    }

    static TxProducer getSender() {
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
