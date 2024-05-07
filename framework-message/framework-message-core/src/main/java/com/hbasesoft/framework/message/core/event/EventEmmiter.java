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
    public static void emmit(final String event, final EventData data) {
        List<EventInterceptor> interceptors = EventIntercetorHolder.getInterceptors(event);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            boolean flag = true;
            for (EventInterceptor interceptor : interceptors) {
                if (!interceptor.sendBefore(event, data, null, null)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                try {
                    MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data));

                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendAfter(event, data, null, null);
                    }
                }
                catch (Exception e) {
                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendError(event, data, null, null, e);
                    }
                }
            }
        }
        else {
            MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data));
        }
        LoggerUtil.debug("触发[event={0},data={1}]事件通知", event, data);
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
    public static void emmit(final String event, final EventData data, final int seconds) {
        List<EventInterceptor> interceptors = EventIntercetorHolder.getInterceptors(event);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            boolean flag = true;
            for (EventInterceptor interceptor : interceptors) {
                if (!interceptor.sendBefore(event, data, seconds, null)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                try {
                    MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data), seconds);

                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendAfter(event, data, seconds, null);
                    }
                }
                catch (Exception e) {
                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendError(event, data, seconds, null, e);
                    }
                }
            }
        }
        else {
            MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data), seconds);
        }
        LoggerUtil.debug("触发[event={0},data={1}, delayTime={2}]事件通知", event, data, seconds);
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
    public static void emmit(final String event, final EventData data, final String produceModel) {
        List<EventInterceptor> interceptors = EventIntercetorHolder.getInterceptors(event);
        if (CollectionUtils.isNotEmpty(interceptors)) {
            boolean flag = true;
            for (EventInterceptor interceptor : interceptors) {
                if (!interceptor.sendBefore(event, data, null, produceModel)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                try {
                    MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data), produceModel);

                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendAfter(event, data, null, produceModel);
                    }
                }
                catch (Exception e) {
                    for (int i = interceptors.size() - 1; i >= 0; i--) {
                        interceptors.get(i).sendError(event, data, null, produceModel, e);
                    }
                }
            }
        }
        else {
            MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data), produceModel);
        }
        LoggerUtil.debug("触发[event={0},data={1}, produceModel={2}]事件通知", event, data, produceModel);
    }
}
