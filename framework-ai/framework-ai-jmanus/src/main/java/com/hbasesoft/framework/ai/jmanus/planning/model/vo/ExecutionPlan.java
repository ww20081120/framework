package com.hbasesoft.framework.ai.jmanus.planning.model.vo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.ai.jmanus.agent.AgentState;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 计划实体类， 用于管理执行计划的相关信息
 */
@Data
public class ExecutionPlan {
    /**
     * 执行计划类，用于定义和管理一个执行计划的详细信息
     */
    private String planId; // 计划ID，用于唯一标识一个执行计划

    private String title; // 计划标题，概括执行计划的主题

    private String planningThinking; // 规划思路，记录制定计划时的思考和依据

    private String executionParams; // 执行参数，存储执行计划所需的参数配置

    private List<ExecutionStep> steps; // 执行步骤列表，包含完成计划所需的具体步骤

    /**
     * 构造方法，初始化执行计划的基本信息
     *
     * @param planId 计划ID，用于唯一标识一个执行计划
     * @param title  计划标题，概括执行计划的主题
     */
    public ExecutionPlan(String planId, String title) {
        this.planId = planId;
        this.title = title;
        this.steps = new ArrayList<>(); // 初始化执行步骤列表为空的ArrayList
        this.executionParams = ""; // 初始化执行参数为空字符串
    }

    /**
     * 添加执行步骤到执行步骤列表中
     *
     * @param step 要添加的执行步骤，不能为空
     *             <p>
     *             此方法将一个执行步骤对象添加到内部维护的执行步骤列表中
     *             它允许在执行流程中动态地增加步骤
     */
    public void addStep(ExecutionStep step) {
        this.steps.add(step); // 添加一个执行步骤到执行步骤列表中
    }

    public void removeStep(ExecutionStep step) {
        this.steps.remove(step); // 添加一个执行步骤到执行步骤列表中
    }

    public int getStepCount() {
        return this.steps.size(); // 获取执行步骤列表的长度，即执行步骤的个数
    }

    /**
     * 获取计划执行状态的字符串格式
     * 此方法构建并返回一个字符串，该字符串包含了计划的执行参数和全局步骤计划的状态
     * 如果只显示已完成和第一个正在进行中的步骤，则通过参数控制
     *
     * @param onlyCompletedAndFirstInProgress 如果为true，仅显示已完成的步骤和第一个正在进行中的步骤；否则，显示所有步骤
     * @return 包含执行参数和全局步骤计划状态的字符串
     */
    public String getPlanExecutionStateStringFormat(boolean onlyCompletedAndFirstInProgress) {
        StringBuilder state = new StringBuilder();
        state.append("\n- 执行参数: ").append("\n");
        // 根据executionParams是否为空，添加相应的信息到state中
        if (StringUtils.isNotEmpty(executionParams)) {
            state.append(executionParams).append("\n\n");
        } else {
            state.append("未提供执行参数。\n\n");
        }

        // 添加全局步骤计划的状态到state中
        state.append("- 全局步骤计划:\n");
        state.append(getStepsExecutionStateStringFormat(onlyCompletedAndFirstInProgress));

        return state.toString();
    }

    /**
     * 获取步骤执行状态的字符串格式
     *
     * @param onlyCompletedAndFirstInProgress 当为true时，只输出所有已完成的步骤和第一个进行中的步骤
     * @return 格式化的步骤执行状态字符串
     */
    public String getStepsExecutionStateStringFormat(boolean onlyCompletedAndFirstInProgress) {
        StringBuilder state = new StringBuilder();
        boolean foundInProgress = false;

        for (int i = 0; i < steps.size(); i++) {
            ExecutionStep step = steps.get(i);

            // 如果onlyCompletedAndFirstInProgress为true，则只显示COMPLETED状态的步骤和第一个IN_PROGRESS状态的步骤
            if (onlyCompletedAndFirstInProgress) {
                // 如果是COMPLETED状态，始终显示
                if (step.getStatus() == AgentState.COMPLETED) {
                    // 什么都不做，继续显示
                } else if (step.getStatus() == AgentState.IN_PROGRESS && !foundInProgress) {
                    foundInProgress = true;
                }
                // 其他所有情况（不是COMPLETED且不是第一个IN_PROGRESS）
                else {
                    continue;  // 跳过不符合条件的步骤
                }
            }

            String symbol = switch (step.getStatus()) {
                case COMPLETED -> "[completed]";
                case IN_PROGRESS -> "[in_progress]";
                case BLOCKED -> "[blocked]";
                case NOT_STARTED -> "[not_started]";
                default -> "[ ]";
            };

            state.append(i + 1)
                    .append(".  **步骤 ")
                    .append(i)
                    .append(":**\n")
                    .append("    *   **状态:** ")
                    .append(symbol)
                    .append("\n")
                    .append("    *   **操作:** ")
                    .append(step.getStepRequirement())
                    .append("\n");

            String result = step.getResult();
            if (StringUtils.isNotEmpty(result)) {
                state.append("    *   **结果:** ").append(result).append("\n\n");
            }
        }
        return state.toString();
    }

    /**
     * 获取所有步骤执行状态的字符串格式（兼容旧版本）
     *
     * @return 格式化的步骤执行状态字符串
     */
    public String getStepsExecutionStateStringFormat() {
        return getStepsExecutionStateStringFormat(false);
    }

    public String toJson() {
        JSONObject obj = new JSONObject();
        obj.put("planId", planId);
        obj.put("title", title);
        obj.put("steps", steps.stream().map(ExecutionStep::toJson).map(JSONObject::parseObject).collect(Collectors.toList()));
        return obj.toJSONString();
    }

    /**
     * 从JSON字符串解析并创建ExecutionPlan对象
     *
     * @param planJson  JSON字符串
     * @param newPlanId 新的计划ID（可选，如果提供将覆盖JSON中的planId）
     * @return 解析后的ExecutionPlan对象
     * @throws Exception 如果解析失败则抛出异常
     */
    public static ExecutionPlan fromJson(String planJson, String newPlanId) throws Exception {
        JSONObject rootNode = JSONObject.parseObject(planJson);

        // 获取计划标题
        String title = rootNode.containsKey("title") ? rootNode.getString("title") : "来自模板的计划";

        // 使用新的计划ID或从JSON中获取
        String planId = StringUtils.isNotEmpty(newPlanId) ? newPlanId : (rootNode.containsKey("planId") ? rootNode.getString("planId") : "unknown-plan");

        // 创建新的ExecutionPlan对象
        ExecutionPlan plan = new ExecutionPlan(planId, title);
        if (rootNode.containsKey("steps")) {
            JSONArray steps = rootNode.getJSONArray("steps");
            if (CollectionUtils.isNotEmpty(steps)) {
                int stepIndex = 0;
                for (int i = 0; i < steps.size(); i++) {
                    // 调用ExecutionStep的fromJson方法创建步骤
                    ExecutionStep step = ExecutionStep.fromJson(steps.getJSONObject(i));
                    Integer stepIndexValFromJson = step.getStepIndex();
                    if (stepIndexValFromJson != null) {
                        stepIndex = stepIndexValFromJson;
                    } else {
                        step.setStepIndex(stepIndex);
                    }
                    plan.addStep(step);
                    stepIndex++;
                }
            }
        }
        return plan;
    }
}
