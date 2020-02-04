/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.delay;

import java.io.Serializable;

import com.hbasesoft.framework.common.utils.CommonUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.delay <br>
 */
@Getter
@Setter
@AllArgsConstructor
public class DelayMessage implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1175519261018848513L;

    private String messageId;

    private String channel;

    private byte[] data;

    private int seconds;

    private long currentTime;

    public DelayMessage(String channel, byte[] data, int seconds) {
        this(CommonUtil.getTransactionID(), channel, data, seconds, System.currentTimeMillis());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String toString() {
        return new StringBuilder().append("{\"messageId\":\"").append(messageId).append("\",\"channel\":\"")
            .append(channel).append("\"}").toString();
    }
}
