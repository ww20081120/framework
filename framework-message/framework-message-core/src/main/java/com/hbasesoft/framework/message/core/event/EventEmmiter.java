/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

import java.util.Arrays;

import com.hbasesoft.framework.common.utils.bean.SerializationUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.MessageHelper;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.event <br>
 */
public final class EventEmmiter {

    private EventEmmiter() {
    }

    /**
     * Description: 触发事件<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event <br>
     */
    public static void emmit(String event) {
        emmit(event, new EventData());
    }

    /**
     * Description: 触发事件<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @param data <br>
     */
    public static void emmit(String event, EventData data) {
        byte[] bdata = SerializationUtil.serial(data);
        System.out.println(Arrays.toString(bdata));
        MessageHelper.createMessageQueue().push(event, bdata);
        LoggerUtil.info("触发[event={0},data={1}]事件", event, data);
    }

    /**
     * Description: 触发通知<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event <br>
     */
    public static void subscriber(String event) {
        subscriber(event, new EventData());
    }

    /**
     * Description: 触发通知<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @param data <br>
     */
    public static void subscriber(String event, EventData data) {
        MessageHelper.createMessagePublisher().publish(event, SerializationUtil.serial(data));
        LoggerUtil.info("触发[event={0},data={1}]事件通知", event, data);
    }
}
