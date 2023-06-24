/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

import java.util.HashMap;

import com.hbasesoft.framework.common.utils.CommonUtil;

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
public class EventData extends HashMap<String, Object> {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 2323769185641461392L;

    /** msgId */
    private String msgId;

    /** default key */
    private static final String DEFAULT_KEY = "__DATA";

    /**
     * 
     */
    public EventData() {
        super();
        this.msgId = CommonUtil.getTransactionID();
    }

    /**
     * @param data
     */
    public EventData(final Object data) {
        this(CommonUtil.getTransactionID(), data);
    }

    /**
     * @param msgId
     * @param data
     */
    public EventData(final String msgId, final Object data) {
        this.msgId = msgId;
        super.put(DEFAULT_KEY, data);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @return <br>
     */
    public String getParameter(final String key) {
        return (String) this.get(key);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T> T
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) super.get(DEFAULT_KEY);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param msgId <br>
     */
    public void setMsgId(final String msgId) {
        this.msgId = msgId;
    }
}
