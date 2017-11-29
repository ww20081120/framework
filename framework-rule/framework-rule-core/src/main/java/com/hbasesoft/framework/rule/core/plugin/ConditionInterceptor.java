/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.plugin;

import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.rule.core.AbstractFlowCompnentInterceptor;
import com.hbasesoft.framework.rule.core.FlowBean;
import com.hbasesoft.framework.rule.core.FlowContext;

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
public class ConditionInterceptor extends AbstractFlowCompnentInterceptor {

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
        if (StringUtils.isNotEmpty(condition)) {
            Binding binding = new Binding(flowContext.getParamMap());
            if (MapUtils.isNotEmpty(flowContext.getExtendUtils())) {
                for (Entry<String, Object> util : flowContext.getExtendUtils().entrySet()) {
                    binding.setProperty(util.getKey(), util.getValue());
                }
            }
            binding.setProperty("flowBean", flowBean);
            GroovyShell shell = new GroovyShell(binding);
            Object value = shell.evaluate(condition);
            return value != null && "true".equals(value.toString().toLowerCase());
        }
        return true;
    }
}
