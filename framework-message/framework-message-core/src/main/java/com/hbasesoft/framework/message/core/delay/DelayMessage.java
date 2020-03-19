/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.delay;

import java.io.Serializable;

import com.hbasesoft.framework.common.utils.CommonUtil;

import lombok.AllArgsConstructor;

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
@AllArgsConstructor
public class DelayMessage implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1175519261018848513L;

    /** message Id */
    private String messageId;

    /** channel */
    private String channel;

    /** data */
    private byte[] data;

    /** seconds */
    private int seconds;

    /** currentTime */
    private long currentTime;

    /**
     * @param channel
     * @param data
     * @param seconds
     */
    public DelayMessage(final String channel, final byte[] data, final int seconds) {
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param messageId <br>
     */
    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel <br>
     */
    public void setChannel(final String channel) {
        this.channel = channel;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param data <br>
     */
    public void setData(final byte[] data) {
        this.data = data;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param seconds <br>
     */
    public void setSeconds(final int seconds) {
        this.seconds = seconds;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param currentTime <br>
     */
    public void setCurrentTime(final long currentTime) {
        this.currentTime = currentTime;
    }

}
