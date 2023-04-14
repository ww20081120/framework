/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.rule.plugin.statemachine;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.annotation.NoTransLog;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.rule.core.AbstractFlowCompnentInterceptor;
import com.hbasesoft.framework.rule.core.FlowContext;
import com.hbasesoft.framework.rule.core.FlowHelper;
import com.hbasesoft.framework.rule.core.config.FlowConfig;
import com.hbasesoft.framework.rule.core.config.JsonConfigUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年8月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.rule.plugin.statemachine <br>
 */
@NoTransLog
public class StateMachineInterceptor extends AbstractFlowCompnentInterceptor {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param bean
     * @param flowContext
     * @return <br>
     */
    @Override
    public boolean before(final Serializable bean, final FlowContext flowContext) {
        FlowConfig config = flowContext.getFlowConfig();
        Map<String, Object> attrMap = config.getConfigAttrMap();

        if (attrMap.containsKey("stateMachine")) {
            Assert.isTrue(bean instanceof StateMachineFlowBean, ErrorCodeDef.FLOW_MUST_HAS_STATE);

            StateMachineFlowBean flowBean = (StateMachineFlowBean) bean;

            String currentEvent = flowBean.getEvent();
            Assert.notEmpty(currentEvent, ErrorCodeDef.EVENT_NOT_EMPTY);

            String state = (String) attrMap.get("begin");
            Assert.notEmpty(state, ErrorCodeDef.BEGIN_STATE_NOT_EMPTY);
            String end = (String) attrMap.get("end");
            Assert.notEmpty(end, ErrorCodeDef.END_STATE_NOT_EMPTY);
            JSONObject control = (JSONObject) attrMap.get("stateMachine");
            Assert.notEmpty(control, ErrorCodeDef.CONTROL_NOT_NULL);

            String currentState = flowBean.getState();
            if (StringUtils.isEmpty(currentState)) {
                currentState = state;
                flowBean.setState(currentState);
            }

            // 如果当前流程已经结束，则不往下继续走了
            if (!CommonUtil.match(end, currentState)) {

                JSONArray matchEvents = control.getJSONArray(currentState);
                Assert.notEmpty(matchEvents, ErrorCodeDef.STATE_NOT_MATCH, currentState);

                for (int i = 0, size = matchEvents.size(); i < size; i++) {
                    JSONObject eventObj = matchEvents.getJSONObject(i);
                    String action = eventObj.getString("action");
                    Assert.notEmpty(action, ErrorCodeDef.EVENT_NOT_EMPTY);
                    String endState = eventObj.getString("end");
                    String gError = eventObj.getString("error");

                    if (StringUtils.isEmpty(endState)) {
                        endState = currentState;
                    }

                    String errorState = eventObj.getString("error");
                    if (StringUtils.isEmpty(errorState)) {
                        errorState = StringUtils.isEmpty(gError) ? gError : currentState;
                    }

                    if (CommonUtil.match(action, currentEvent)) {
                        FlowConfig flowConfig = JsonConfigUtil.getFlowConfig(eventObj);
                        FlowContext newFlowContext = new FlowContext(flowConfig, flowContext.getExtendUtils(),
                            flowContext.getParamMap());
                        try {
                            FlowHelper.execute(flowBean, newFlowContext);

                            int code = newFlowContext.getCode();
                            if (code == 0) {
                                flowBean.setState(endState);
                            }
                            else {
                                flowBean.setState(getState(errorState, code));
                            }
                        }
                        catch (Exception e) {
                            LoggerUtil.error("flow process error.", e);

                            flowContext.setException(e);

                            if (errorState.indexOf(GlobalConstants.EQUAL_SPLITER) == -1) {
                                flowBean.setState(errorState);
                            }
                            else {
                                flowBean.setState(getState(errorState,
                                    (e instanceof FrameworkException ? ((FrameworkException) e).getCode().getCode()
                                        : ErrorCodeDef.SYSTEM_ERROR.getCode())));
                            }
                        }
                        return true;
                    }
                }
            }
            throw new ServiceException(ErrorCodeDef.EVENT_NOT_FOUND, currentState, currentEvent);
        }
        return true;
    }

    private String getState(final String errorState, final int code) {
        String[] errCodes = StringUtils.split(errorState, GlobalConstants.SPLITOR);
        String es = null;
        String codes = GlobalConstants.BLANK + code;
        for (String errCode : errCodes) {
            String[] codeAndState = StringUtils.split(errCode, GlobalConstants.EQUAL_SPLITER);
            if (codeAndState.length == 2 && CommonUtil.match(codeAndState[0], codes)) {
                es = codeAndState[1];
                break;
            }
        }
        Assert.notEmpty(es, ErrorCodeDef.ERROR_STATE_NOT_FOUND);
        return es;
    }

}
