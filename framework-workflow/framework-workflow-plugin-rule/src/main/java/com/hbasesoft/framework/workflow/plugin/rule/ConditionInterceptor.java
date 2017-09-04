/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.workflow.plugin.rule;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.workflow.core.FlowBean;
import com.hbasesoft.framework.workflow.core.FlowComponentInterceptor;
import com.hbasesoft.framework.workflow.core.FlowContext;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * <Description> 条件组合<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.plugin.rule <br>
 */
@Component
public class ConditionInterceptor implements FlowComponentInterceptor {

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
        String condition = (String) flowContext.getFlowConfig().getConfigAttrMap().get("condition");
        if (CommonUtil.isNotEmpty(condition)) {
            Binding binding = new Binding(flowContext.getParamMap());
            binding.setProperty("flowBean", flowBean);
            GroovyShell shell = new GroovyShell(binding);
            Object value = shell.evaluate(condition);
            return value != null && "true".equals(value.toString().toLowerCase());
        }
        return true;
    }
}
