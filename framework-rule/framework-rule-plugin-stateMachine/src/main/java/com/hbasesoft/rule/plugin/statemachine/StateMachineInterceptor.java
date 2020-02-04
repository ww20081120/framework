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
     * @param flowBean
     * @param flowContext
     * @return <br>
     */
    @Override
    public boolean before(Serializable bean, FlowContext flowContext) {
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
                    String event = eventObj.getString("event");
                    Assert.notEmpty(event, ErrorCodeDef.EVENT_NOT_EMPTY);
                    String endState = eventObj.getString("end");
                    String gError = eventObj.getString("error");

                    if (StringUtils.isEmpty(endState)) {
                        endState = currentState;
                    }

                    String errorState = eventObj.getString("error");
                    if (StringUtils.isEmpty(errorState)) {
                        errorState = StringUtils.isEmpty(gError) ? gError : currentState;
                    }

                    if (CommonUtil.match(event, currentEvent)) {
                        FlowConfig flowConfig = JsonConfigUtil.getFlowConfig(eventObj);
                        FlowContext newFlowContext = new FlowContext(flowConfig, flowContext.getExtendUtils(),
                            flowContext.getParamMap());
                        try {
                            FlowHelper.execute(flowBean, newFlowContext);
                            flowBean.setState(endState);
                        }
                        catch (Exception e) {
                            LoggerUtil.error("flow process error.", e);

                            String code = GlobalConstants.BLANK
                                + (e instanceof FrameworkException ? ((FrameworkException) e).getCode()
                                    : ErrorCodeDef.SYSTEM_ERROR_10001);

                            if (errorState.indexOf(GlobalConstants.EQUAL_SPLITER) == -1) {
                                flowBean.setState(errorState);
                            }
                            else {
                                String[] errCodes = StringUtils.split(errorState, GlobalConstants.SPLITOR);
                                String es = null;
                                for (String errCode : errCodes) {
                                    String[] codeAndState = StringUtils.split(errCode, GlobalConstants.EQUAL_SPLITER);
                                    if (codeAndState.length == 2 && CommonUtil.match(codeAndState[0], code)) {
                                        es = codeAndState[1];
                                        break;
                                    }
                                }

                                Assert.notEmpty(es, ErrorCodeDef.ERROR_STATE_NOT_FOUND);
                                flowBean.setState(es);
                            }
                        }
                        return false;
                    }
                }

                throw new ServiceException(ErrorCodeDef.EVENT_NOT_FOUND, currentState, currentEvent);

            }
            return false;
        }
        return true;

    }

}
