/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

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
        MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data));
        LoggerUtil.debug("触发[event={0},data={1}]事件通知", event, data);
    }

    public static void emmit(String event, EventData data, int seconds) {
        MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data), seconds);
        LoggerUtil.debug("触发[event={0},data={1}, delayTime={2}]事件通知", event, data, seconds);
    }

    public static void emmit(String event, EventData data, String produceModel) {
        MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data), produceModel);
        LoggerUtil.debug("触发[event={0},data={1}, produceModel={2}]事件通知", event, data, produceModel);
    }
}
