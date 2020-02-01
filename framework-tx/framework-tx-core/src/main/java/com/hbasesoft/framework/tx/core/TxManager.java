/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;

/**
 * <Description> 事务的管理工厂 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 21, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.core <br>
 */
public final class TxManager {

    private static final Object LOCK = new Object();

    /**
     * 存放traceId
     */
    private static TransIdGeneratorFactory transIdGeneratorFactory;

    /**
     * 存放所有的方法
     */
    private static Map<String, Method> proxyMethod = new HashMap<>();

    /**
     * 存放所有的对象
     */
    private static Map<String, Object> proxyObject = new HashMap<>();

    /**
     * Description: 获取TraceId<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String getTraceId() {
        return getTransIdGeneratorFactory().getTraceId();
    }

    /**
     * Description: 重新执行方法<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param mark
     * @param context
     * @param args
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException <br>
     */
    public static void execute(String mark, Map<String, String> context, Object[] args)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object obj = proxyObject.get(mark);
        Assert.notNull(obj, ErrorCodeDef.TRASACTION_RETRY_SENDER_NOT_FOUND, mark);
        Method method = proxyMethod.get(mark);
        Assert.notNull(method, ErrorCodeDef.TRASACTION_RETRY_SENDER_NOT_FOUND, mark);
        method.invoke(obj, args);
    }

    /**
     * Description: 注册客户端<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param mark
     * @param obj
     * @param method <br>
     */
    public static void regist(String mark, Object obj, Method method) {
        proxyObject.put(mark, obj);
        proxyMethod.put(mark, method);
    }

    /**
     * Description: 获取mark的方法<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param method
     * @return <br>
     */
    public static String getMarker(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName()).append('.').append(method.getName()).append('(');
        Class<?>[] types = method.getParameterTypes();
        if (types != null && types.length > 0) {
            for (int i = 0, len = types.length; i < len; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(types[i].getName());
            }
        }
        sb.append(')');
        return sb.toString();
    }

    private static TransIdGeneratorFactory getTransIdGeneratorFactory() {
        synchronized (LOCK) {
            if (transIdGeneratorFactory == null) {
                ServiceLoader<TransIdGeneratorFactory> producerLoader = ServiceLoader
                    .load(TransIdGeneratorFactory.class);
                Iterator<TransIdGeneratorFactory> it = producerLoader.iterator();
                Assert.isTrue(it.hasNext(), ErrorCodeDef.TRANS_ID_GENERATOR_FACTORY_NOT_FOUND);
                transIdGeneratorFactory = it.next();
            }
            return transIdGeneratorFactory;
        }
    }
}
