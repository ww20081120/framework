package com.hbasesoft.framework.ai.jmanus.tool;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionPlan;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.jmanus.tool.code.ToolExecuteResult;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <Description> 计划工具类 <br>
 *
 * @author wangwei<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年6月16日 <br>
 * @see com.hbasesoft.framework.ai.jmanus.tool <br>
 * @since V1.0<br>
 */
public class PlanningTool implements ToolCallBiFunctionDef<PlanningTool.PlanningInput> {

    /**
     * 当前执行计划
     */
    private ExecutionPlan currentPlan;

    private static final String DESCRIPTION = "用于管理任务的计划工具";

    private static final String NAME = "计划任务";

    private static final String PARAMETERS = """
        {
            "type": "object",
            "properties": {
                "command": {
                    "description": "创建一个执行计划，可用命令: create",
                    "enum": [
                        "create"
                    ],
                    "type": "string"
                },
                "title": {
                    "description": "计划的标题",
                    "type": "string"
                },
                "steps": {
                    "description": "执行计划的步骤列表",
                    "type": "array",
                    "items": {
                        "type": "string"
                    }
                }
            },
            "required": [
            	"command",
            	"title",
            	"steps"
            ]
        }
        """;

    public DashScopeApi.FunctionTool getToolDefinition() {
        return new DashScopeApi.FunctionTool(new DashScopeApi.FunctionTool.Function(DESCRIPTION, NAME, PARAMETERS));
    }

    // Parameterized FunctionToolCallback with appropriate types.

    /**
     * 获取函数工具回调对象。 该方法使用构建器模式创建并返回一个 FunctionToolCallback 实例， 用于处理从 String 类型输入到 ToolExecuteResult 类型输出的工具回调逻辑。
     *
     * @return FunctionToolCallback 实例，封装了工具的相关信息和执行逻辑。
     */
    public static FunctionToolCallback<?, ToolExecuteResult> getFunctionToolCallback(PlanningTool toolInstance) {
        // 使用 FunctionToolCallback 的构建器开始构建实例，传入工具名称和当前 PlanningTool 实例
        // 由于 PlanningTool 实现了 Function<String, ToolExecuteResult> 接口，可作为工具执行逻辑
        return FunctionToolCallback.builder(toolInstance.getName(), toolInstance)
            // 设置工具的描述信息
            .description(toolInstance.getDescription())
            // 设置输入参数的模式信息
            .inputSchema(toolInstance.getParameters())
            // 指定工具输入参数的类型为 PlanningInput
            .inputType(toolInstance.getInputType())
            // 设置工具的元数据，returnDirect(true) 表示工具执行结果会直接返回
            .toolMetadata(ToolMetadata.builder().returnDirect(toolInstance.isReturnDirect()).build())
            // 调用 build 方法完成 FunctionToolCallback 实例的构建
            .build();
    }

    public ToolExecuteResult run(PlanningInput input) {
        try {
            String command = input.getCommand();
            String planId = input.getPlanId();
            String title = input.getTitle();
            List<String> steps = input.getSteps();

            return switch (command) {
                case "create" -> createPlan(planId, title, steps);
                default -> {
                    LoggerUtil.info("收到无效的命令：{0}", command);
                    throw new IllegalArgumentException("无效的命令：" + command);
                }
            };

        }
        catch (Exception e) {
            LoggerUtil.error("执行计划工具时发生错误", e);
            return new ToolExecuteResult("Error executing planning tool: {0}", e.getMessage());
        }
    }

    private ToolExecuteResult createPlan(String planId, String title, List<String> steps) {
        if (StringUtils.isEmpty(title) || CollectionUtils.isEmpty(steps)) {
            LoggerUtil.info("创建计划时缺少必要参数：planId={0}, title={1}, steps={2}", planId, title, steps);
            return new ToolExecuteResult("创建计划时缺少必要参数!");
        }

        ExecutionPlan plan = new ExecutionPlan(planId, title);
        // 使用新的createExecutionStep方法创建并添加步骤
        int index = 0;
        for (String step : steps) {
            plan.addStep(createExecutionStep(step, index++));
        }
        this.currentPlan = plan;
        return new ToolExecuteResult(new StringBuilder("计划创建成功：").append(planId).append('\n')
            .append(plan.getPlanExecutionStateStringFormat(false)).toString());
    }

    private ExecutionStep createExecutionStep(String step, int index) {
        ExecutionStep executionStep = new ExecutionStep();
        executionStep.setStepIndex(index);
        executionStep.setStepRequirement(step);
        return executionStep;
    }

    public String getCurrentPlanId() {
        return currentPlan != null ? currentPlan.getPlanId() : null;
    }

    public ExecutionPlan getCurrentPlan() {
        return currentPlan;
    }

    /**
     * Internal input class for defining planning tool input parameters
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanningInput {

        private String command;

        private String planId;

        private String title;

        private List<String> steps;
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
    public ToolExecuteResult apply(PlanningInput input, ToolContext u) {
        return run(input);
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
    public Class<PlanningInput> getInputType() {
        return PlanningInput.class;
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
        // Implementation can be added if needed
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
        if (currentPlan != null) {
            return "当前计划: " + currentPlan.getPlanExecutionStateStringFormat(false);
        }
        return "无活跃计划";
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
        // Implementation can be added if needed
    }
}
