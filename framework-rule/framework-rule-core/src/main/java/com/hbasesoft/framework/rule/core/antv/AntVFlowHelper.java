/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.antv;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.rule.core.FlowComponent;
import com.hbasesoft.framework.rule.core.FlowContext;
import com.hbasesoft.framework.rule.core.FlowHelper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> 支持antDesgin flow流程图的流程执行器 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年12月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.core.antv <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AntVFlowHelper {

    /** flowHelper */
    private static FlowHelper flowHelper = FlowHelper.getFlowHelper();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param bean
     * @param flowConfig
     * @return <br>
     */
    public static <T extends Serializable> String flowStart(final T bean, final String flowConfig) {
        return AntVFlowHelper.flowStart(bean, JSONObject.parseObject(flowConfig), null);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param bean
     * @param flowConfig
     * @return <br>
     */
    public static <T extends Serializable> String flowStart(final T bean, final JSONObject flowConfig) {
        return AntVFlowHelper.flowStart(bean, flowConfig, null);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param bean
     * @param flowConfig
     * @param currentNode
     * @return <br>
     */
    public static <T extends Serializable> String flowStart(final T bean, final JSONObject flowConfig,
        final String currentNode) {

        Assert.notNull(bean, ErrorCodeDef.PARAM_NOT_NULL, "FlowBean");
        Assert.notNull(flowConfig, ErrorCodeDef.FLOW_NOT_MATCH, bean);

        AntVFlowConfig config = AntVFlowConfig.parse(flowConfig);
        try {
            return AntVFlowHelper.execute(bean, new FlowContext(config), currentNode);
        }
        catch (Exception e) {
            LoggerUtil.error("flow process error.", e);
            FrameworkException fe = e instanceof FrameworkException ? (FrameworkException) e
                : new FrameworkException(e);
            throw fe;
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param flowBean
     * @param flowContext
     * @param currentNode
     * @return 最后的节点
     * @throws Exception <br>
     */
    public static <T extends Serializable> String execute(final T flowBean, final FlowContext flowContext,
        final String currentNode) throws Exception {
        AntVFlowConfig flowConfig = (AntVFlowConfig) flowContext.getFlowConfig();
        Assert.notNull(flowConfig, ErrorCodeDef.FLOW_COMPONENT_NOT_FOUND);

        // 设置初始节点
        if (StringUtils.isNotEmpty(currentNode)) {
            flowConfig.setCurrentNode(currentNode);
        }

        String node = null;
        if (flowHelper.before(flowBean, flowContext)) {
            try {
                FlowComponent<T> component = flowConfig.getComponent();
                while (component != null) {
                    // 执行流程组件
                    boolean flag = component.process(flowBean, flowContext);
                    if (flag) {
                        // 执行组件后置拦截
                        component.afterProcess(flowBean, flowContext, null);
                    }
                    flowConfig.setNext(flag);
                    node = flowConfig.getName();
                    component = flowConfig.getComponent();
                }
                // 执行后置拦截
                flowHelper.after(flowBean, flowContext);
            }
            catch (Exception e) {
                flowContext.setFlowConfig(flowConfig);
                // 执行异常拦截
                flowHelper.error(e, flowBean, flowContext);
                throw e;
            }
        }
        return node;
    }
}
