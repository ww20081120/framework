/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.tool.ToolCallback;

import com.hbasesoft.framework.ai.jmanus.agent.AgentExecResult;
import com.hbasesoft.framework.ai.jmanus.agent.ReActAgent;
import com.hbasesoft.framework.ai.jmanus.config.ManusProperties;
import com.hbasesoft.framework.ai.jmanus.llm.LlmService;
import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.agent <br>
 */
public class DynamicAgent extends ReActAgent {

    /** 
     * @param llmService
     * @param planExecutionRecorder
     * @param manusProperties
     * @param initSettingAgentSetting 
     */ 
    public DynamicAgent(LlmService llmService, PlanExecutionRecorder planExecutionRecorder,
        ManusProperties manusProperties, Map<String, Object> initSettingAgentSetting) {
        super(llmService, planExecutionRecorder, manusProperties, initSettingAgentSetting);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    protected boolean think() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    protected AgentExecResult act() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    protected Message getNextStepWithEnvMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public List<ToolCallback> getToolCallList() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param planId <br>
     */
    @Override
    public void clearUp(String planId) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param planId <br>
     */ 
    public void setPlanId(String planId) {
        // TODO Auto-generated method stub
        
    }

    /**
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param toolCallbackProvider <br>
     */ 
    public void setToolCallbackProvider(ToolCallbackProvider toolCallbackProvider) {
        // TODO Auto-generated method stub
        
    }

}
