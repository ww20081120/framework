/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.rule.plugin.event;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.annotation.NoTransLog;
import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.rule.core.AbstractFlowCompnentInterceptor;
import com.hbasesoft.framework.rule.core.FlowContext;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.rule.plugin.event <br>
 */
@NoTransLog
public class EventComponentInterceptor extends AbstractFlowCompnentInterceptor {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowBean
     * @param flowContext
     * @return <br>
     */
    @Override
    public boolean before(final Serializable flowBean, final FlowContext flowContext) {
        String event = (String) flowContext.getFlowConfig().getConfigAttrMap().get("beforeEvent");
        if (StringUtils.isNotEmpty(event)) {
            EventData data = new EventData();
            data.putAll(flowContext.getParamMap());
            data.put("flowBean", flowBean);
            EventEmmiter.emmit(event, data);
        }
        return true;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowBean
     * @param flowContext <br>
     */
    @Override
    public void after(final Serializable flowBean, final FlowContext flowContext) {
        String event = (String) flowContext.getFlowConfig().getConfigAttrMap().get("event");
        if (StringUtils.isNotEmpty(event)) {
            EventData data = new EventData();
            data.putAll(flowContext.getParamMap());
            data.put("flowBean", flowBean);
            EventEmmiter.emmit(event, data);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param e
     * @param flowBean
     * @param flowContext <br>
     */
    @Override
    public void error(final Exception e, final Serializable flowBean, final FlowContext flowContext) {
        String event = (String) flowContext.getFlowConfig().getConfigAttrMap().get("errorEvent");
        if (StringUtils.isNotEmpty(event)) {
            EventData data = new EventData();
            data.putAll(flowContext.getParamMap());
            data.put("flowBean", flowBean);
            data.put("errorCode",
                e instanceof FrameworkException ? ((FrameworkException) e).getCode() : ErrorCodeDef.SYSTEM_ERROR);
            EventEmmiter.emmit(event, data);
        }
    }
}
