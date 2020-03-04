/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.rule.plugin.statemachine;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.rule.core.AbstractFlowCompnentInterceptor;
import com.hbasesoft.framework.rule.core.FlowContext;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年7月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.rule.plugin.statemachine <br>
 */
public class StateFlowInterceptor extends AbstractFlowCompnentInterceptor {

    /** skip flag holder */
    private ThreadLocal<Boolean> skipFlagHolder = ThreadLocal.withInitial(() -> true);

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
    public boolean before(Serializable flowBean, FlowContext flowContext) {
        String lcName = flowContext.getFlowConfig().getName();
        // 初始化节点
        if (lcName.endsWith("_1")) {
            skipFlagHolder.set(true);
        }

        if (flowBean instanceof StateFlowBean
            && flowContext.getFlowConfig().getConfigAttrMap().containsKey("stateFlow")) {
            StateFlowBean stateFlowBean = (StateFlowBean) flowBean;

            // 如果未设置lastComponent则一直执行
            if (StringUtils.isEmpty(stateFlowBean.getLastComponent())) {
                skipFlagHolder.set(false);
            }
            // 如果已经设置了lastComponent，但是skipFlag是true，，才能继续执行
            else if (skipFlagHolder.get()) {
                // 不是相同的name，不能执行
                if (!StringUtils.equals(stateFlowBean.getLastComponent(), lcName)) {
                    LoggerUtil.debug("当前记录的状态为{0}, 跳过组件{1}", stateFlowBean.getLastComponent(), lcName);
                    return false;
                }
                // 匹配到相同的name才能继续执行
                skipFlagHolder.set(false);
            }
            // 如果已经设置了lastComponent，但是skipFlag为false， 则忽略一直执行
            stateFlowBean.setLastComponent(lcName);
        }
        return true;
    }
}
