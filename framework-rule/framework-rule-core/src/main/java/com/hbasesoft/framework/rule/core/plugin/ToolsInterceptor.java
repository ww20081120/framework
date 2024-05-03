/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.plugin;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.annotation.NoTracerLog;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.rule.core.AbstractFlowCompnentInterceptor;
import com.hbasesoft.framework.rule.core.FlowContext;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月6日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.plugin.tools <br>
 */
@NoTracerLog
public class ToolsInterceptor extends AbstractFlowCompnentInterceptor {

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
        String tools = (String) flowContext.getFlowConfig().getConfigAttrMap().get("tools");
        if (StringUtils.isNotEmpty(tools)) {
            String[] ts = StringUtils.split(tools, GlobalConstants.SPLITOR);
            if (ArrayUtils.isNotEmpty(ts)) {
                Map<String, Object> toolMap = flowContext.getExtendUtils();
                for (String t : ts) {
                    if (!toolMap.containsKey(t)) {
                        Object tool = ContextHolder.getContext().getBean(t);
                        toolMap.put(t, tool);
                    }
                }
            }
        }
        return true;
    }
}
