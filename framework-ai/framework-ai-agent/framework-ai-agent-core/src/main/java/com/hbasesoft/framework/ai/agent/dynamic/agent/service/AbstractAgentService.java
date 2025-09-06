/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.agent.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.hbasesoft.framework.ai.agent.agent.BaseAgent;
import com.hbasesoft.framework.ai.agent.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.agent.dynamic.agent.ToolCallbackProvider;
import com.hbasesoft.framework.ai.agent.dynamic.agent.vo.Tool;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.service.IMcpService;
import com.hbasesoft.framework.ai.agent.planning.IPlanningFactory;
import com.hbasesoft.framework.ai.agent.planning.IPlanningFactory.ToolCallBackContext;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import lombok.RequiredArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.agent.service <br>
 */
@RequiredArgsConstructor
public abstract class AbstractAgentService implements AgentService {

    private final IPlanningFactory planningFactory;

    private final IDynamicAgentLoader dynamicAgentLoader;

    private final IMcpService mcpService;

    @Override
    public BaseAgent createDynamicBaseAgent(String name, String planId, String rootPlanId,
        Map<String, Object> initialAgentSetting, String expectedReturnInfo) {

        LoggerUtil.info("Create new BaseAgent: {0}, planId: {1}", name, planId);

        try {
            // Load existing Agent through dynamicAgentLoader
            DynamicAgent agent = dynamicAgentLoader.loadAgent(name, initialAgentSetting);

            // Set planId
            agent.setCurrentPlanId(planId);
            agent.setRootPlanId(rootPlanId);
            // Set tool callback mapping
            Map<String, ToolCallBackContext> toolCallbackMap = planningFactory.toolCallbackMap(planId, rootPlanId,
                expectedReturnInfo);
            agent.setToolCallbackProvider(new ToolCallbackProvider() {

                @Override
                public Map<String, ToolCallBackContext> getToolCallBackContext() {
                    return toolCallbackMap;
                }
            });
            return agent;
        }
        catch (Exception e) {
            LoggerUtil.error("Failed to create dynamic base agent: " + name, e);
            throw new RuntimeException("Failed to create dynamic base agent: " + name, e);
        }
    }

    public List<Tool> getAvailableTools() {

        String uuid = UUID.randomUUID().toString();
        String expectedReturnInfo = "dummyColumn1, dummyColumn2";
        try {
            Map<String, ToolCallBackContext> toolcallContext = planningFactory.toolCallbackMap(uuid, uuid,
                expectedReturnInfo);
            return toolcallContext.entrySet().stream().map(entry -> {
                Tool tool = new Tool();
                tool.setKey(entry.getKey());
                tool.setName(entry.getKey()); // You might want to provide a more friendly
                // name
                tool.setDescription(entry.getValue().getFunctionInstance().getDescription());
                tool.setEnabled(true);
                tool.setServiceGroup(entry.getValue().getFunctionInstance().getServiceGroup());
                return tool;
            }).collect(Collectors.toList());
        }
        finally {
            mcpService.close(uuid);
        }
    }

}
