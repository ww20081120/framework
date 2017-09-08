/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.workflow.plugin.rule;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.workflow.core.FlowBean;
import com.hbasesoft.framework.workflow.core.FlowComponentInterceptor;
import com.hbasesoft.framework.workflow.core.FlowContext;

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
@Component
public class ToolsInterceptor implements FlowComponentInterceptor {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public int order() {
        return -1;
    }

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
    public boolean before(FlowBean flowBean, FlowContext flowContext) {
        String tools = (String) flowContext.getFlowConfig().getConfigAttrMap().get("tools");
        if (CommonUtil.isNotEmpty(tools)) {
            String[] ts = StringUtils.split(tools, GlobalConstants.SPLITOR);
            if (CommonUtil.isNotEmpty(ts)) {
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
