package com.hbasesoft.framework.ai.demo.jmanus.planning.model.vo;

import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.ai.demo.jmanus.agent.AgentState;
import com.hbasesoft.framework.ai.demo.jmanus.agent.BaseAgent;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ExecutionStep {

    private Integer stepIndex;

    private String stepRequirement;

    private String result;

    private BaseAgent agent;

    public AgentState getStatus() {
        return agent == null ? AgentState.NOT_STARTED : agent.getState();
    }

    /**
     * 以字符串形式获取当前步骤的信息
     * <p>
     * 此方法构建并返回一个字符串，该字符串包含了步骤索引、代理状态和步骤需求等信息
     * 它首先检查代理对象是否为空，如果不为空，则获取代理的状态并转换为字符串形式；
     * 如果为空，则将代理状态设置为“NOT_STARTED”然后，它使用StringBuilder来拼接步骤索引、
     * 代理状态和步骤需求，最后将StringBuilder对象转换为字符串并返回
     *
     * @return 当前步骤的详细信息字符串
     */
    public String getStepInStr() {
        // 初始化代理状态为null
        String agentState = null;

        // 检查代理对象是否不为空
        if (agent != null) {
            // 如果不为空，获取代理的状态并转换为字符串
            agentState = agent.getState().toString();
        } else {
            // 如果为空，设置代理状态为“NOT_STARTED”
            agentState = AgentState.NOT_STARTED.toString();
        }

        // 使用StringBuilder拼接步骤信息
        return new StringBuilder()
                .append(stepIndex)    // 添加步骤索引
                .append(". [")        // 添加分隔符和开头括号
                .append(agentState)   // 添加代理状态
                .append("] ")         // 添加结束括号和空格
                .append(stepRequirement)  // 添加步骤需求
                .toString();          // 转换为字符串并返回
    }

    public String toJson() {
        JSONObject obj = new JSONObject();
        obj.put("stepRequirement", stepRequirement);
        if (StringUtils.isNotEmpty(result)) {
            obj.put("result", result);
        }
        return obj.toJSONString();
    }

    /**
     * 从JSONObject解析并创建ExecutionStep对象
     *
     * @param stepNode JsonNode对象
     * @return 解析后的ExecutionStep对象
     */
    public static ExecutionStep fromJson(JSONObject stepNode) {
        ExecutionStep step = new ExecutionStep();
        step.setStepRequirement(stepNode.containsKey("stepRequirement") ? stepNode.getString("stepRequirement") : "未指定步骤");
        // 设置步骤索引（如果有）
        if (stepNode.containsKey("stepIndex")) {
            step.setStepIndex(stepNode.getInteger("stepIndex"));
        }
        // 设置步骤结果（如果有）
        if (stepNode.containsKey("result")) {
            step.setResult(stepNode.getString("result"));
        }
        return step;
    }
}
