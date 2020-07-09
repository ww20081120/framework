/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

import com.hbasesoft.framework.common.annotation.NoTransLog;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jul 9, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core <br>
 */
@NoTransLog
public interface EventInterceptor {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data
     * @return <br>
     */
    default boolean sendBefore(String channel, EventData eventData) {
        return true;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data <br>
     */
    default void sendAfter(String channel, EventData eventData) {
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
    default void sendError(String channel, EventData eventData, Exception e) {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data
     * @return <br>
     */
    default boolean receiveBefore(String channel, EventData eventData) {
        return true;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel
     * @param data <br>
     */
    default void receiveAfter(String channel, EventData eventData) {
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
    default void receiveError(String channel, EventData eventData, Exception e) {
    }

    /**
     * Description: 顺序<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    default int order() {
        return 0;
    }
}
