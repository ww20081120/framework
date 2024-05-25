/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.MessageHelper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月21日 <br>
 * @see com.hbasesoft.framework.message.core.event <br>
 * @since V1.0<br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventEmmiter {

    /**
     * Description: 触发事件<br>
     *
     * @param event <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    public static void emmit(final String event) {
        emmit(event, null);
    }

    /**
     * Description: 触发事件<br>
     *
     * @param event
     * @param data <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    public static void emmit(final String event, final Object data) {
        EventData<?> eventData = new EventData<>(data);
        List<EventInterceptor> interceptors = EventIntercetorHolder.getInterceptors(event);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            boolean flag = true;
            for (EventInterceptor interceptor : interceptors) {
                if (!interceptor.sendBefore(event, eventData, null, null)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                try {
                    MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(eventData));

                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendAfter(event, eventData, null, null);
                    }
                }
                catch (Exception e) {
                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendError(event, eventData, null, null, e);
                    }
                }
            }
        }
        else {
            MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(eventData));
        }
        LoggerUtil.debug("触发[event={0},data={1}]事件通知", event, eventData);
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
    public static void emmit(final String event, final Object data, final int seconds) {
        EventData<?> eventData = new EventData<>(data);

        List<EventInterceptor> interceptors = EventIntercetorHolder.getInterceptors(event);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            boolean flag = true;
            for (EventInterceptor interceptor : interceptors) {
                if (!interceptor.sendBefore(event, eventData, seconds, null)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                try {
                    MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(eventData), seconds);

                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendAfter(event, eventData, seconds, null);
                    }
                }
                catch (Exception e) {
                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendError(event, eventData, seconds, null, e);
                    }
                }
            }
        }
        else {
            MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(eventData), seconds);
        }
        LoggerUtil.debug("触发[event={0},data={1}, delayTime={2}]事件通知", event, eventData, seconds);
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
    public static void emmit(final String event, final Object data, final String produceModel) {
        EventData<?> eventData = new EventData<>(data);

        List<EventInterceptor> interceptors = EventIntercetorHolder.getInterceptors(event);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            boolean flag = true;
            for (EventInterceptor interceptor : interceptors) {
                if (!interceptor.sendBefore(event, eventData, null, produceModel)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                try {
                    MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(eventData),
                        produceModel);

                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendAfter(event, eventData, null, produceModel);
                    }
                }
                catch (Exception e) {
                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendError(event, eventData, null, produceModel, e);
                    }
                }
            }
        }
        else {
            MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(eventData), produceModel);
        }
        LoggerUtil.debug("触发[event={0},data={1}, produceModel={2}]事件通知", event, eventData, produceModel);
    }
}
