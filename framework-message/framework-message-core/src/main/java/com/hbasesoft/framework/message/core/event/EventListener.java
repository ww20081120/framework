/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

import java.io.Closeable;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.message.core.MessageSubscriber;
import com.hbasesoft.framework.tracing.core.TraceLogUtil;

/**
 * <Description> <br>
 * 
 * @param <T> 数据对象
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.event <br>
 */
public interface EventListener<T> extends MessageSubscriber {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String[] events();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @param data <br>
     */
    void onEmmit(String event, T data);

    /**
     * Description: 是否为订阅消息<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    default boolean subscriber() {
        return false;
    }

    /**
     * Description: 接收到消息<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data <br>
     */
    @SuppressWarnings("unchecked")
    default void onMessage(final String channel, final byte[] data) {
        long current = System.currentTimeMillis();
        try (Closeable scope = TraceLogUtil.before(current, channel, new Object[] {
            DataUtil.base64Encode(data)
        })) {
            EventData<T> eventData = SerializationUtil.unserial(EventData.class, data);
            LoggerUtil.debug("[{0}]接收到[event={1},data={2}]事件", Thread.currentThread().threadId(), channel, eventData);

            List<EventInterceptor> interceptors = EventIntercetorHolder.getInterceptors(channel);
            if (CollectionUtils.isNotEmpty(interceptors)) {
                boolean flag = true;
                for (EventInterceptor interceptor : interceptors) {
                    if (!interceptor.receiveBefore(channel, eventData)) {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    try {
                        onEmmit(channel, eventData.getData());
                        for (int i = interceptors.size() - 1; i >= 0; i--) {
                            interceptors.get(i).receiveAfter(channel, eventData);
                        }
                    }
                    catch (Exception e) {
                        for (int i = interceptors.size() - 1; i >= 0; i--) {
                            interceptors.get(i).receiveError(channel, eventData, e);
                        }
                    }
                }
            }
            else {
                onEmmit(channel, eventData.getData());
            }
            TraceLogUtil.afterReturning(current, channel, null);
        }
        catch (Throwable e) {
            LoggerUtil.error(e);
            TraceLogUtil.afterThrowing(current, channel, e);
        }
    }

    /**
     * Description: 开始订阅 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param subscribeChannels <br>
     */
    default void onSubscribe(String channel, int subscribeChannels) {
        LoggerUtil.debug("开始监听{0}事件，监听者数目{1}", channel, subscribeChannels);
    }

    /**
     * Description: 取消订阅 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param subscribedChannels <br>
     */
    default void onUnsubscribe(String channel, int subscribedChannels) {
        LoggerUtil.debug("取消监听{0}事件，监听者数目{1}", channel, subscribedChannels);
    }
}
