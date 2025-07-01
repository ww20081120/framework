package com.hbasesoft.framework.ai.jmanus.agent;

import com.hbasesoft.framework.ai.jmanus.agent.AgentExecResult;
import com.hbasesoft.framework.ai.jmanus.config.ManusProperties;
import com.hbasesoft.framework.ai.jmanus.llm.LlmService;
import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;

import java.util.Map;

/**
 * ReAct(Reasoning + Acting) 模式的智能体基类 实现了思考（Reasoning）和行动（Acting）交替执行的智能体模式
 */
public abstract class ReActAgent extends BaseAgent {


    /**
     * 构造函数
     *
     * @param llmService              LLM服务实例， 用于处理自然语言交互
     * @param planExecutionRecorder   计划执行记录器， 用于记录计划执行过程
     * @param manusProperties         Manus属性配置， 用于获取配置信息
     * @param initSettingAgentSetting 初始设置， 用于初始化智能体的设置信息
     */
    public ReActAgent(LlmService llmService, PlanExecutionRecorder planExecutionRecorder, ManusProperties manusProperties, Map<String, Object> initSettingAgentSetting) {
        super(llmService, planExecutionRecorder, manusProperties, initSettingAgentSetting);
    }

    /**
     * 执行思考过程，判断是否需要采取行动
     * <p>
     * 子类实现要求：1.分析当前状态和上下文
     * 2. 进行逻辑推理，得出下一步行动的策略
     * 3.返回是否需要执行行动
     * <p>
     * 示例实现：- 如果需要调用工具，返回true - 如果当前步骤已经完成， 返回false
     *
     * @return true表示需要执行行动， false表示不需要执行行动
     */
    protected abstract boolean think();

    /**
     * 执行具体的行动
     * <p>
     * 子类实现要求： 1.基于think()的决策执行具体操作
     * 2. 可以是工具调用、状态更新等具体行为
     * 3. 返回执行结构的描述信息
     * <p>
     * 示例实现： - ToolCallAgent：执行选定的工具调用 - BrowserAgent：执行浏览器操作
     *
     * @return 行动执行的结果描述
     */
    protected abstract AgentExecResult act();

    @Override
    protected AgentExecResult step() {
        boolean shouldAct = think();
        if (!shouldAct) {
            AgentExecResult result = new AgentExecResult("思考完成 - 无需执行操作", AgentState.IN_PROGRESS);
            return result;
        }
        return act();
    }
}
