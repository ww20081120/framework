/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.alibaba.fastjson2.JSONArray;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.core.util.ArgsSerializationUtil;

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

    /** logger */
    private static final Logger LOGGER = new Logger("TxLogger");

    /** LOCK */
    private static final Object LOCK = new Object();

    /** sender */
    private static TxProducer sender;

    /** clientInfoFactory */
    private static TxClientInfoFactory clientInfoFactory;

    /**
     * Description: 注册代理<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo 客户端信息
     * @param invoker 具体的执行类
     * @param <T> T
     * @return T
     * @throws Throwable <br>
     */
    public static <T> T registInvoke(final ClientInfo clientInfo, final TxInvoker<T> invoker) {
        clientInfo.setClientInfo(getClientInfoFactory().getClientInfo());
        TxProducer sd = getSender();

        Object[] args = ArgsSerializationUtil.unserialArgs(clientInfo.getArgs());
        LOGGER.info("registClient|{0}|{1}|{2}|{3}|{4}|{5}", clientInfo.getId(), clientInfo.getMark(),
            clientInfo.getContext(), args == null ? GlobalConstants.BLANK : JSONArray.toJSONString(args),
            clientInfo.getMaxRetryTimes(), clientInfo.getRetryConfigs());

        boolean flag = sd.registClient(clientInfo);

        T msg = invoker.invoke();
        if (flag) {
            sd.removeClient(clientInfo.getId());
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
     * @param <T> T
     * @return T
     * @throws Throwable <br>
     */
    public static <T> T invoke(final String marker, final TxInvoker<T> invoker) {
        return invoke(TxManager.getTraceId(), marker, invoker);
    }

    /**
     * Description:执行代理方法 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param marker
     * @param invoker
     * @param <T> T
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(final String id, final String marker, final TxInvoker<T> invoker) {
        TxProducer sd = getSender();

        CheckInfo checkInfo = sd.check(id, marker);
        if (checkInfo == null) {
            T msg = invoker.invoke();
            checkInfo = new CheckInfo(id, marker);
            if (msg != null) {
                checkInfo.setResult(SerializationUtil.jdkSerial(msg));
            }
            sd.saveResult(checkInfo);
            return msg;
        }
        byte[] result = checkInfo.getResult();
        return result != null && result.length > 0 ? (T) SerializationUtil.jdkUnserial(result) : null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static TxClientInfoFactory getClientInfoFactory() {
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static TxProducer getSender() {
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
