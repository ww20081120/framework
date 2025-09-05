package com.hbasesoft.framework.ai.agent.agent;

import java.util.Map;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.agent.llm.ILlmService;
import com.hbasesoft.framework.ai.agent.recorder.PlanExecutionRecorder;

/**
 * ReAct(Reasoning + Acting) 模式的智能体基类 实现了思考（Reasoning）和行动（Acting）交替执行的智能体模式
 */
public abstract class ReActAgent extends BaseAgent {

    /**
     * Constructor
     * 
     * @param llmService LLM service instance for handling natural language interactions
     * @param planExecutionRecorder plan execution recorder for recording execution process
     * @param manusProperties Manus configuration properties
     */

    public ReActAgent(ILlmService llmService, PlanExecutionRecorder planExecutionRecorder,
        IManusProperties manusProperties, Map<String, Object> initialAgentSetting, PromptService promptService) {
        super(llmService, planExecutionRecorder, manusProperties, initialAgentSetting, promptService);
    }

    /**
     * Execute thinking process and determine whether action needs to be taken Subclass implementation requirements: 1.
     * Analyze current state and context 2. Perform logical reasoning to decide on next action 3. Return whether action
     * execution is needed Example implementation: - Return true if tools need to be called - Return false if current
     * step is completed
     * 
     * @return true indicates action execution is needed, false indicates no action is currently needed
     */
    protected abstract boolean think();

    /**
     * Execute specific actions Subclass implementation requirements: 1. Execute specific operations based on think()
     * decisions 2. Can be tool calls, state updates, or other specific behaviors 3. Return description of execution
     * results Example implementations: - ToolCallAgent: execute selected tool calls - BrowserAgent: execute browser
     * operations
     * 
     * @return description of action execution results
     */
    protected abstract AgentExecResult act();

    /**
     * Execute a complete think-act step
     * 
     * @return returns thinking complete message if no action is needed, otherwise returns action execution result
     */
    @Override
    public AgentExecResult step() {

        boolean shouldAct = think();
        if (!shouldAct) {
            AgentExecResult result = new AgentExecResult("Thinking complete - no action needed",
                AgentState.IN_PROGRESS);

            return result;
        }
        return act();
    }
}
