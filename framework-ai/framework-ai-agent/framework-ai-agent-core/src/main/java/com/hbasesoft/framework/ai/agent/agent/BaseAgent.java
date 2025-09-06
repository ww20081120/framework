package com.hbasesoft.framework.ai.agent.agent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.tool.ToolCallback;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.model.enums.PromptEnum;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.agent.llm.ILlmService;
import com.hbasesoft.framework.ai.agent.llm.LlmService;
import com.hbasesoft.framework.ai.agent.planning.IPlanningFactory.ToolCallBackContext;
import com.hbasesoft.framework.ai.agent.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.ai.agent.recorder.model.ExecutionStatus;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * An abstract base class for implementing AI agents that can execute multi-step tasks. 用于实现可执行多步骤任务的 AI 智能体的抽象基类。 This
 * class provides the core functionality for managing agent state, conversation flow, 该类提供了管理智能体状态、对话流程的核心功能， and
 * step-by-step execution of tasks. 以及任务的逐步执行功能。
 * <p>
 * The agent supports a finite number of execution steps and includes mechanisms for: 该智能体支持有限数量的执行步骤，并且包含以下机制：
 * <ul>
 * <li>State management (idle, running, finished)</li>
 * <li>状态管理（空闲、运行中、已完成）</li>
 * <li>Conversation tracking</li>
 * <li>对话跟踪</li>
 * <li>Step limitation and monitoring</li>
 * <li>步骤限制和监控</li>
 * <li>Thread-safe execution</li>
 * <li>线程安全的执行</li>
 * <li>Stuck-state detection and handling</li>
 * <li>卡住状态的检测和处理</li>
 * </ul>
 * <p>
 * Implementing classes must define: 实现类必须定义以下方法：
 * <ul>
 * <li>{@link #getName()} - Returns the agent's name</li>
 * <li>{@link #getName()} - 返回智能体的名称</li>
 * <li>{@link #getDescription()} - Returns the agent's description</li>
 * <li>{@link #getDescription()} - 返回智能体的描述信息</li>
 * <li>{@link #getThinkMessage()} - Implements the thinking chain logic</li>
 * <li>{@link #getThinkMessage()} - 实现思考链逻辑</li>
 * <li>{@link #getNextStepWithEnvMessage()} - Provides the next step's prompt template</li>
 * <li>{@link #getNextStepWithEnvMessage()} - 提供下一步的提示模板</li>
 * <li>{@link #step()} - Implements the core logic for each execution step</li>
 * <li>{@link #step()} - 实现每个执行步骤的核心逻辑</li>
 * </ul>
 *
 * @see AgentState
 * @see LlmService
 */
public abstract class BaseAgent {

    private String currentPlanId = null;

    private String rootPlanId = null;

    // Think-act record ID for sub-plan executions triggered by tool calls
    private Long thinkActRecordId = null;

    private AgentState state = AgentState.NOT_STARTED;

    protected ILlmService llmService;

    protected final IManusProperties manusProperties;

    protected final PromptService promptService;

    private int maxSteps;

    private int currentStep = 0;

    // Change the data map to an immutable object and initialize it properly
    private final Map<String, Object> initSettingData;

    private Map<String, Object> envData = new HashMap<>();

    protected PlanExecutionRecorder planExecutionRecorder;

    public abstract void clearUp(String planId);

    /**
     * Get the name of the agent Implementation requirements: 1. Return a short but descriptive name 2. The name should
     * reflect the main functionality or characteristics of the agent 3. The name should be unique for easy logging and
     * debugging Example implementations: - ToolCallAgent returns "ToolCallAgent" - BrowserAgent returns "BrowserAgent"
     * 
     * @return The name of the agent
     */
    public abstract String getName();

    /**
     * Get the detailed description of the agent Implementation requirements: 1. Return a detailed description of the
     * agent's functionality 2. The description should include the agent's main responsibilities and capabilities 3.
     * Should explain how this agent differs from other agents Example implementations: - ToolCallAgent: "Agent
     * responsible for managing and executing tool calls, supporting multi-tool combination calls" - ReActAgent: "Agent
     * that implements alternating execution of reasoning and acting"
     * 
     * @return The detailed description text of the agent
     */
    public abstract String getDescription();

    /**
     * Add thinking prompts to the message list to build the agent's thinking chain Implementation requirements: 1.
     * Generate appropriate system prompts based on current context and state 2. Prompts should guide the agent on how
     * to think and make decisions 3. Can recursively build prompt chains to form hierarchical thinking processes 4.
     * Return the added system prompt message object Subclass implementation reference: 1. ReActAgent: Implement basic
     * thinking-action loop prompts 2. ToolCallAgent: Add tool selection and execution related prompts
     * 
     * @return The added system prompt message object
     */
    protected Message getThinkMessage(int execTimes) {
        // Get operating system information
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");

        // Get current date time, format as yyyy-MM-dd
        String currentDateTime = java.time.LocalDate.now().toString(); // Format as
                                                                       // yyyy-MM-dd
        boolean isDebugModel = manusProperties.getDebugDetail();
        String detailOutput = "";
        if (isDebugModel) {
            detailOutput = promptService.getPromptByName("AGENT_DEBUG_DETAIL_OUTPUT").getPromptContent();
        }
        else {
            detailOutput = promptService.getPromptByName("AGENT_NORMAL_OUTPUT").getPromptContent();
        }
        String parallelToolCallsResponse = "";
        if (manusProperties.getParallelToolCalls()) {
            parallelToolCallsResponse = promptService.getPromptByName("AGENT_PARALLEL_TOOL_CALLS_RESPONSE")
                .getPromptContent();
        }
        Map<String, Object> variables = new HashMap<>(getInitSettingData());
        variables.put("osName", osName);
        variables.put("osVersion", osVersion);
        variables.put("osArch", osArch);
        variables.put("currentDateTime", currentDateTime);
        variables.put("detailOutput", detailOutput);
        variables.put("parallelToolCallsResponse", parallelToolCallsResponse);
        variables.put("exec_times", execTimes);


        return promptService.createSystemMessage(PromptEnum.AGENT_STEP_EXECUTION.getPromptName(), variables);
    }

    /**
     * Get the next step prompt message Implementation requirements: 1. Generate a prompt message that guides the agent
     * to perform the next step 2. The prompt should be based on the current execution state and context 3. The message
     * should clearly guide the agent on what task to perform Subclass implementation reference: 1. ToolCallAgent:
     * Return prompts related to tool selection and execution 2. ReActAgent: Return prompts related to reasoning or
     * action decision
     * 
     * @return The next step prompt message object
     */
    protected abstract Message getNextStepWithEnvMessage();

    public abstract List<ToolCallback> getToolCallList();

    public abstract ToolCallBackContext getToolCallBackContext(String toolKey);

    public BaseAgent(ILlmService llmService, PlanExecutionRecorder planExecutionRecorder,
        IManusProperties manusProperties, Map<String, Object> initialAgentSetting, PromptService promptService) {
        this.llmService = llmService;
        this.planExecutionRecorder = planExecutionRecorder;
        this.manusProperties = manusProperties;
        this.promptService = promptService;
        this.maxSteps = manusProperties.getMaxSteps();
        if (initialAgentSetting.containsKey("agent_max_steps")) {
            try {
                this.maxSteps = Integer.parseInt(initialAgentSetting.get("agent_max_steps").toString());
            }
            catch (Exception e) {
                LoggerUtil.warn("Invalid agent_max_steps value: {0}, using default {1}",
                    initialAgentSetting.get("agent_max_steps"), manusProperties.getMaxSteps());
            }
        }
        this.initSettingData = new HashMap<>(initialAgentSetting);
    }

    public String run() {
        currentStep = 0;
        if (state != AgentState.IN_PROGRESS) {
            throw new IllegalStateException("Cannot run agent from state: " + state);
        }

        LocalDateTime startTime = LocalDateTime.now();
        List<String> results = new ArrayList<>();
        boolean completed = false;
        boolean stuck = false;
        String errorMessage = null;
        String finalResult = null;

        try {
            state = AgentState.IN_PROGRESS;

            while (currentStep < maxSteps && !state.equals(AgentState.COMPLETED)) {
                currentStep++;
                LoggerUtil.info("Executing round {0}/{1}", currentStep, maxSteps);

                AgentExecResult stepResult = step(currentStep);

                if (isStuck()) {
                    handleStuckState();
                    stuck = true;
                }
                else {
                    // Update global state for consistency
                    LoggerUtil.info("Agent state: {0}", stepResult.getState());
                    state = stepResult.getState();
                }

                results.add("Round " + currentStep + ": " + stepResult.getResult());
            }

            if (currentStep >= maxSteps) {
                results.add("Terminated: Reached max rounds (" + maxSteps + ")");
            }

            completed = state.equals(AgentState.COMPLETED) && !stuck;

            // Calculate execution time in seconds
            LocalDateTime endTime = LocalDateTime.now();
            long executionTimeSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
            String status = completed ? "Success" : (stuck ? "Execution stuck" : "Incomplete");
            finalResult = String.format("Execution %s [Duration: %d seconds] [Steps consumed: %d] ", status,
                executionTimeSeconds, currentStep);

        }
        catch (Exception e) {
            LoggerUtil.error("Agent execution failed", e);
            errorMessage = e.getMessage();
            completed = false;
            LocalDateTime endTime = LocalDateTime.now();
            finalResult = String.format("Execution failed [Error: %s]", e.getMessage());
            results.add("Execution failed: " + e.getMessage());

            // Record execution at the end - even for failures
            if (currentPlanId != null && planExecutionRecorder != null) {
                PlanExecutionRecorder.PlanExecutionParams params = new PlanExecutionRecorder.PlanExecutionParams();
                params.setCurrentPlanId(currentPlanId);
                params.setRootPlanId(rootPlanId);
                params.setThinkActRecordId(thinkActRecordId);
                params.setAgentName(getName());
                params.setAgentDescription(getDescription());
                params.setMaxSteps(maxSteps);
                params.setActualSteps(currentStep);
                params.setStatus(
                    stuck ? ExecutionStatus.IDLE : (completed ? ExecutionStatus.FINISHED : ExecutionStatus.RUNNING));
                params.setErrorMessage(errorMessage);
                params.setResult(finalResult);
                params.setStartTime(startTime);
                params.setEndTime(endTime);
                planExecutionRecorder.recordCompleteAgentExecution(params);
            }

            throw e; // Re-throw the exception to let the caller know that an error
                     // occurred
        }
        finally {
            state = AgentState.COMPLETED; // Reset state after execution
            llmService.clearAgentMemory(currentPlanId);
        }

        // Record execution at the end - only once
        if (currentPlanId != null && planExecutionRecorder != null) {
            LocalDateTime endTime = LocalDateTime.now();
            PlanExecutionRecorder.PlanExecutionParams params = new PlanExecutionRecorder.PlanExecutionParams();
            params.setCurrentPlanId(currentPlanId);
            params.setRootPlanId(rootPlanId);
            params.setThinkActRecordId(thinkActRecordId);
            params.setAgentName(getName());
            params.setAgentDescription(getDescription());
            params.setMaxSteps(maxSteps);
            params.setActualSteps(currentStep);
            params.setStatus(
                stuck ? ExecutionStatus.IDLE : (completed ? ExecutionStatus.FINISHED : ExecutionStatus.RUNNING));
            params.setErrorMessage(errorMessage);
            params.setResult(finalResult);
            params.setStartTime(startTime);
            params.setEndTime(endTime);
            planExecutionRecorder.recordCompleteAgentExecution(params);
        }

        return results.isEmpty() ? "" : results.get(results.size() - 1);
    }

    protected abstract AgentExecResult step(int currentStep);

    private void handleStuckState() {
        LoggerUtil.warn("Agent stuck detected - Missing tool calls");

        // End current step
        setState(AgentState.COMPLETED);

        String stuckPrompt = """
            Agent response detected missing required tool calls.
            Please ensure each response includes at least one tool call to progress the task.
            Current step: %d
            Execution status: Force terminated
            """.formatted(currentStep);

        LoggerUtil.error(stuckPrompt);
    }

    /**
     * Check if the agent is stuck
     * 
     * @return true if the agent is stuck, false otherwise
     */
    protected boolean isStuck() {
        // Currently, if the agent does not call the tool three times, it is considered
        // stuck and the current step is exited.
        List<Message> memoryEntries = llmService.getAgentMemory(manusProperties.getMaxMemory()).get(getCurrentPlanId());
        int zeroToolCallCount = 0;
        for (Message msg : memoryEntries) {
            if (msg instanceof AssistantMessage) {
                AssistantMessage assistantMsg = (AssistantMessage) msg;
                if (assistantMsg.getToolCalls() == null || assistantMsg.getToolCalls().isEmpty()) {
                    zeroToolCallCount++;
                }
            }
        }
        return zeroToolCallCount >= 3;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    public String getCurrentPlanId() {
        return currentPlanId;
    }

    public void setCurrentPlanId(String planId) {
        this.currentPlanId = planId;
    }

    public void setRootPlanId(String rootPlanId) {
        this.rootPlanId = rootPlanId;
    }

    public String getRootPlanId() {
        return rootPlanId;
    }

    public Long getThinkActRecordId() {
        return thinkActRecordId;
    }

    public void setThinkActRecordId(Long thinkActRecordId) {
        this.thinkActRecordId = thinkActRecordId;
    }

    /**
     * Check if this agent is executing a sub-plan triggered by a tool call
     * 
     * @return true if this is a sub-plan execution, false otherwise
     */
    public boolean isSubPlanExecution() {
        return thinkActRecordId != null;
    }

    public AgentState getState() {
        return state;
    }

    /**
     * Get the data context of the agent Implementation requirements: 1. Return all the context data needed for the
     * agent's execution 2. Data can include: - Current execution state - Step information - Intermediate results -
     * Configuration parameters 3. Data is set through setData() when run() is executed Do not modify the implementation
     * of this method. If you need to pass context, inherit and modify setData() to improve getData() efficiency.
     * 
     * @return A Map object containing the agent's context data
     */
    protected final Map<String, Object> getInitSettingData() {
        return initSettingData;
    }

    public IManusProperties getManusProperties() {
        return manusProperties;
    }

    public Map<String, Object> getEnvData() {
        return envData;
    }

    public void setEnvData(Map<String, Object> envData) {
        this.envData = Collections.unmodifiableMap(new HashMap<>(envData));
    }

}