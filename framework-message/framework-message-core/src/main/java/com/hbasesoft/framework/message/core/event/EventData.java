/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

import java.io.Serializable;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.tracing.core.TraceLogUtil;

import lombok.Data;

/**
 * <Description> <br>
 * 
 * @param <T> 数据类型
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.event <br>
 */
@Data
public class EventData<T> implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 2323769185641461392L;

    /** tracerId */
    private final String tracerId;

    /** msgId */
    private final String msgId;

    /** data */
    private T data;

    /** 
     *  
     */
    public EventData() {
        this.msgId = CommonUtil.getTransactionID();
        this.tracerId = TraceLogUtil.getTraceId();
    }

    /**
     * @param data
     */
    public EventData(final T data) {
        this.msgId = CommonUtil.getTransactionID();
        this.tracerId = TraceLogUtil.getTraceId();
        this.data = data;
    }

    /**
     * @param data
     */
    public EventData(final EventData<T> data) {
        this.msgId = data.getMsgId();
        this.tracerId = data.getTracerId();
        this.data = data.getData();
    }

    /**
     * @param msgId
     * @param data
     */
    EventData(final String msgId, final T data) {
        this.msgId = msgId;
        this.tracerId = TraceLogUtil.getTraceId();
        this.data = data;
    }

}
