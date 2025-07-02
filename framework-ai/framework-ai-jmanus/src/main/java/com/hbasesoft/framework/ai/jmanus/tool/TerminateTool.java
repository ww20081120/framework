/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.tool;

import org.springframework.ai.chat.model.ToolContext;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.hbasesoft.framework.ai.jmanus.tool.code.ToolExecuteResult;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.tool <br>
 */
public class TerminateTool implements ToolCallBiFunctionDef<TerminateTool.TerminateInput> {

    private static String PARAMETERS = """
        {
          "type" : "object",
          "properties" : {
            "message" : {
              "type" : "string",
              "description" : "终结当前步骤的信息，你需要在这个终结信息里尽可能多的包含所有相关的事实和数据，详细描述执行结果和状态，包含所有收集到的相关事实和数据，关键发现和观察。这个终结信息将作为当前步骤的最终输出，并且应该足够全面，以便为后续步骤或其他代理提供完整的上下文与关键事实。无需输出浏览器可交互元素索引，因为索引会根据页面的变化而变化。"
            }
          },
          "required" : [ "message" ]
        }
        """;

    public static final String NAME = "终止";

    private static final String DESCRIPTION = """

        使用全面的总结消息终止当前执行步骤。
        此消息将作为当前步骤的最终输出，应包含以下内容：

        - 详细的执行结果和状态
        - 收集到的所有相关事实和数据
        - 关键发现和观察结果
        - 重要见解和结论
        - 任何可行的建议

        总结应足够详尽，以便为后续步骤或其他代理提供完整的上下文和关键事实。

        """;

    private String planId;

    private String lastTerminationMessage = "";

    private boolean isTerminated = false;

    private String terminationTimestamp = "";

    public static DashScopeApi.FunctionTool getToolFunctionTool() {
        DashScopeApi.FunctionTool.Function function = new DashScopeApi.FunctionTool.Function(DESCRIPTION, NAME,
            PARAMETERS);
        return new DashScopeApi.FunctionTool(function);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TerminateInput {
        private String message;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param input
     * @param u
     * @return <br>
     */
    @Override
    public ToolExecuteResult apply(TerminateInput input, ToolContext u) {
        String message = input.getMessage();
        LoggerUtil.info("终止消息: {0}", message);
        this.lastTerminationMessage = message;
        this.isTerminated = true;
        this.terminationTimestamp = java.time.LocalDateTime.now().toString();
        return new ToolExecuteResult(message);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getServiceGroup() {
        return "default-service-group";
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
        return NAME;
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
        return DESCRIPTION;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getParameters() {
        return PARAMETERS;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Class<TerminateInput> getInputType() {
        return TerminateInput.class;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public boolean isReturnDirect() {
        return true;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param planId <br>
     */
    @Override
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getCurrentToolStateString() {
        return String.format("""
            终止工具状态：
            - 当前状态: %s
            - 上次终止情况: %s
            - 终止消息: %s
            - 时间戳: %s
            """, isTerminated ? "🛑 已终止" : "⚡ 运行中", isTerminated ? "流程已终止" : "无终止记录",
            lastTerminationMessage.isEmpty() ? "无" : lastTerminationMessage,
            terminationTimestamp.isEmpty() ? "无" : terminationTimestamp);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param planId <br>
     */
    @Override
    public void cleanup(String planId) {
        // do nothing
    }
}
