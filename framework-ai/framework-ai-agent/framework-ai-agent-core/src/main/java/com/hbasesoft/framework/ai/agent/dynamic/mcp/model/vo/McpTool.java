/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo;

import java.util.Map;

import org.springframework.ai.tool.ToolCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.service.IMcpStateHolderService;
import com.hbasesoft.framework.ai.agent.tool.AbstractBaseTool;
import com.hbasesoft.framework.ai.agent.tool.ToolExecuteResult;
import com.hbasesoft.framework.ai.agent.tool.innerStorage.ISmartContentSavingService;
import com.hbasesoft.framework.ai.agent.tool.innerStorage.SmartProcessResult;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo <br>
 */
public class McpTool extends AbstractBaseTool<Map<String, Object>> {

    private final ToolCallback toolCallback;

    private final ObjectMapper objectMapper;

    private String serviceNameString;

    private IMcpStateHolderService mcpStateHolderService;

    private ISmartContentSavingService smartContentSavingService;

    public McpTool(ToolCallback toolCallback, String serviceNameString, String planId,
        IMcpStateHolderService mcpStateHolderService, ISmartContentSavingService smartContentSavingService,
        ObjectMapper objectMapper) {
        this.toolCallback = toolCallback;
        this.objectMapper = objectMapper;
        this.serviceNameString = serviceNameString;
        this.currentPlanId = planId;
        this.mcpStateHolderService = mcpStateHolderService;
        this.smartContentSavingService = smartContentSavingService;
    }

    @Override
    public String getName() {
        return toolCallback.getToolDefinition().name();
    }

    @Override
    public String getDescription() {
        return toolCallback.getToolDefinition().description();
    }

    @Override
    public String getParameters() {
        return toolCallback.getToolDefinition().inputSchema();
    }

    @Override
    public Class<Map<String, Object>> getInputType() {
        return (Class<Map<String, Object>>) (Class<?>) Map.class;
    }

    @Override
    public String getCurrentToolStateString() {
        McpState mcpState = mcpStateHolderService.getMcpState(currentPlanId);
        if (mcpState != null) {
            return mcpState.getState();
        }
        return "";
    }

    @Override
    public ToolExecuteResult run(Map<String, Object> inputMap) {
        // Convert Map to JSON string, as ToolCallback expects string input
        String jsonInput;
        try {
            jsonInput = objectMapper.writeValueAsString(inputMap);
        }
        catch (JsonProcessingException e) {
            return new ToolExecuteResult("Error: Failed to serialize input to JSON - " + e.getMessage());
        }

        String result = toolCallback.call(jsonInput, null);
        if (result == null) {
            result = "";
        }

        SmartProcessResult smartProcessResult = smartContentSavingService.processContent(currentPlanId, result,
            getName());
        result = smartProcessResult.getSummary();
        // Here we can store the result to McpStateHolderService
        McpState mcpState = mcpStateHolderService.getMcpState(currentPlanId);
        if (mcpState == null) {
            mcpState = new McpState();
            mcpStateHolderService.setMcpState(currentPlanId, mcpState);
        }
        mcpState.setState(result);

        return new ToolExecuteResult(result);
    }

    @Override
    public void cleanup(String planId) {
        mcpStateHolderService.removeMcpState(planId);
    }

    @Override
    public String getServiceGroup() {
        return serviceNameString;
    }

}