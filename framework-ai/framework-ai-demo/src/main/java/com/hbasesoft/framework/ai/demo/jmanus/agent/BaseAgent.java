package com.hbasesoft.framework.ai.demo.jmanus.agent;

import com.hbasesoft.framework.ai.demo.jmanus.config.ManusProperties;
import com.hbasesoft.framework.ai.demo.jmanus.llm.LlmService;
import com.hbasesoft.framework.ai.demo.jmanus.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.ai.demo.jmanus.recorder.entity.AgentExecutionRecord;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract base class for implementing AI agents that can execute multi-step tasks.
 * 用于实现可执行多步骤任务的 AI 智能体的抽象基类。
 * This class provides the core functionality for managing agent state, conversation flow,
 * 该类提供了管理智能体状态、对话流程的核心功能，
 * and step-by-step execution of tasks.
 * 以及任务的逐步执行功能。
 *
 * <p>
 * The agent supports a finite number of execution steps and includes mechanisms for:
 * 该智能体支持有限数量的执行步骤，并且包含以下机制：
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
 *
 * <p>
 * Implementing classes must define:
 * 实现类必须定义以下方法：
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

    private int currentStep = 0;

    private AgentState state = AgentState.NOT_STARTED;

    private String planId = null;

    private int maxSteps;

    private LlmService llmService;

    private final ManusProperties manusProperties;

    private final Map<String, Object> initSettingData;

    protected PlanExecutionRecorder planExecutionRecorder;

    public BaseAgent(LlmService llmService, PlanExecutionRecorder planExecutionRecorder, ManusProperties manusProperties, Map<String, Object> initSettingAgentSetting) {
        this.llmService = llmService;
        this.planExecutionRecorder = planExecutionRecorder;
        this.manusProperties = manusProperties;
        this.maxSteps = manusProperties.getMaxSteps();
        this.initSettingData = Collections.unmodifiableMap(new HashMap<>(initSettingAgentSetting));
    }

    /**
     * 获取智能体的名称 实现要求： 1. 返回一个简短但具有描述性的名称
     * 2. 名称应该反映该智能体的主要功能或特性
     * 3. 名称应该是唯一的，便于日志和调试
     * <p>
     * 示例实现： - ToolCallAgent 返回 "ToolCallAgent" - BrowserAgent 返回 "BrowserAgent"
     *
     * @return 智能体的名称
     */
    public abstract String getName();

    /**
     * 获取智能体的详细描述
     * 实现要求： 1. 返回对该智能体功能的详细描述
     * 2. 描述应包含智能体的主要职责和能力
     * 3. 应说明该智能体与其他智能体的区别
     * <p>
     * 示例实现： - ToolCallAgent: "负责管理和执行工具调用的智能体，支持多工具组合调用" - ReActAgent: "实现思考(Reasoning)和行动(Acting)交替执行的智能体"
     *
     * @return 智能体的详细描述文本
     */
    public abstract String getDescription();


    /**
     * 添加思考提示到消息列表中，构建智能体的思考链
     * <p>
     * 实现要求： 1. 根据当前上下文和状态生成合适的系统提示词 2. 提示词应该指导智能体如何思考和决策 3. 可以递归地构建提示链，形成层次化的思考过程 4.
     * 返回添加的系统提示消息对象
     * <p>
     * 子类实现参考： 1. ReActAgent: 实现基础的思考-行动循环提示 2. ToolCallAgent: 添加工具选择和执行相关的提示
     *
     * @return 添加的系统提示消息对象
     */
    protected Message getThinkMessage() {
        // 获取操作系统信息
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");

        // 获取当前日期时间，格式为yyyy-MM-dd
        String currentDate = DateUtil.format(DateUtil.getCurrentDate(), DateUtil.DATE_FORMAT_10);
        boolean isDebugModel = manusProperties.getBrowserDebug();
        String detailOutput = "";
        if (isDebugModel) {
            detailOutput = """
                    1. 使用工具调用时，必须给出解释说明，说明使用这个工具的理由和背后的思考
                    2. 简述过去的所有步骤已经都做了什么事
                    """;
        } else {
            detailOutput = """
                    1. 使用工具调用时，不需要额外的任何解释说明！
                    2. 不要在工具调用前提供推理或描述！
                    """;
        }

        String stepPrompt = """
                - SYSTEM INFORMATION:
                OS: %s %s (%s)
                
                - Current Date:
                %s
                - 全局计划信息：
                {planStatus}
                
                - 当前要做的步骤要求（这个步骤是需要当前智能体完成的！）：
                STEP {currentStepIndex} :{stepText}
                
                - 当前步骤的上下文信息：
                {extraParams}
                
                重要说明：
                %s
                3. 做且只做当前要做的步骤要求中的内容
                4. 如果当前要做的步骤要求已做完，则调用terminate工具来完成当前步骤
                5. 全局目标 是用来有个全局认识的，不要在当前步骤中去完成这个全局目标
                
                """.formatted(osName, osVersion, osArch, currentDate, detailOutput);

        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(stepPrompt);

        Message systemMessage = promptTemplate.createMessage(getInitSettingData());
        return systemMessage;
    }

    /**
     * 获取下一步操作的提示消息
     * <p>
     * 实现要求： 1. 生成引导智能体执行下一步操作的提示消息 2. 提示内容应该基于当前执行状态和上下文 3. 消息应该清晰指导智能体要执行什么任务
     * <p>
     * 子类实现参考： 1. ToolCallAgent：返回工具选择和调用相关的提示 2. ReActAgent：返回思考或行动决策相关的提示
     *
     * @return 下一步操作的提示消息对象
     */
    protected abstract Message getNextStepWithEnvMessage();


    /**
     * 获取智能体可用的工具回调列表。 此方法用于定义智能体在执行任务过程中可以调用的工具集合。 实现类需要根据智能体的具体功能和需求，返回相应的工具回调列表。
     *
     * @return 包含工具回调对象的列表，每个工具回调对象代表一个可调用的工具。
     */
    protected abstract List<ToolCallback> getToolCallList();


    /**
     * 启动智能体执行多步骤任务。 该方法会按照预定义的最大步骤数执行任务，直到任务完成、达到最大步骤数或出现异常。 在执行过程中会记录执行状态和结果，并在结束时清理智能体内存。
     *
     * @return 任务执行的最终结果字符串，如果结果列表为空则返回空字符串。
     * @throws IllegalStateException 如果智能体当前状态不是 IN_PROGRESS 则抛出此异常。
     * @throws Exception             如果执行过程中发生异常，会重新抛出该异常。
     */
    public String run() {
        // 重置当前步骤计数为 0
        currentStep = 0;

        // 检查智能体状态是否为 IN_PROGRESS，若不是则抛出异常
        if (state != AgentState.IN_PROGRESS) {
            throw new IllegalStateException("智能体无法从当前状态启动: " + state);
        }

        // 创建智能体执行记录对象，记录任务执行信息
        AgentExecutionRecord agentRecord = new AgentExecutionRecord(getPlanId(), getName(), getDescription());

        // 设置智能体执行的最大步骤数
        agentRecord.setMaxSteps(maxSteps);

        // 设置智能体当前状态到记录中
        agentRecord.setStatus(state.toString());

        // 如果计划 ID 不为空且记录器存在，则记录智能体执行信息
        if (planId != null && planExecutionRecorder != null) {
            planExecutionRecorder.recordAgentExecution(planId, agentRecord);
        }

        // 用于存储每一步执行结果的列表
        List<String> results = new ArrayList<>();

        try {

            // 将智能体状态设置为执行中
            state = AgentState.IN_PROGRESS;
            // 更新执行记录中的状态
            agentRecord.setStatus(state.toString());

            // 循环执行步骤，直到达到最大步骤数或任务完成
            while (currentStep < maxSteps && !state.equals(AgentState.COMPLETED)) {
                // 增加当前步骤计数
                currentStep++;
                // 记录当前执行的轮次信息
                LoggerUtil.info("正在执行第 {0}/{1} 轮", currentStep, maxSteps);

                // 执行当前步骤并获取执行结果
                AgentExecResult stepResult = step();

                // 检查智能体是否处于卡住状态
                if (isStuck()) {
                    // 处理智能体卡住状态
                    handleStuckState(agentRecord);
                } else {
                    // 记录当前步骤执行后的智能体状态
                    LoggerUtil.info("智能体状态: {0}", stepResult.getState());
                    // 更新智能体的全局状态
                    state = stepResult.getState();
                }

                // 将当前步骤的执行结果添加到结果列表中
                results.add("Round " + currentStep + ": " + stepResult.getResult());

                // 更新执行记录中的当前步骤数
                agentRecord.setCurrentStep(currentStep);
            }

            // 如果达到最大步骤数，将终止信息添加到结果列表中
            if (currentStep >= maxSteps) {
                results.add("终止: 已达到最大步骤数 (" + maxSteps + ")");
            }

            // 设置执行记录的结束时间
            agentRecord.setEndTime(DateUtil.getCurrentDate());
            // 设置执行记录的最终状态
            agentRecord.setStatus(state.toString());
            // 设置执行记录的完成状态
            agentRecord.setCompleted(state.equals(AgentState.COMPLETED));
            // 计算任务执行耗时（秒）
            int executionTimeSeconds = DateUtil.between(agentRecord.getStartTime(), agentRecord.getEndTime(), DateUtil.SECOND);
            // 根据执行结果生成状态描述
            String status = agentRecord.isCompleted() ? "成功" : (agentRecord.isStuck() ? "执行卡住" : "未完成");
            // 设置执行记录的最终结果描述
            agentRecord.setResult(String.format("执行%s [耗时%d秒] [消耗步骤%d] ", status, executionTimeSeconds, currentStep));

        } catch (Exception e) {
            LoggerUtil.error(e);
            // 将异常信息记录到执行记录中
            agentRecord.setErrorMessage(e.getMessage());
            // 设置执行记录的完成状态为失败
            agentRecord.setCompleted(false);
            // 设置执行记录的结束时间
            agentRecord.setEndTime(DateUtil.getCurrentDate());
            // 设置执行记录的最终结果描述，包含错误信息
            agentRecord.setResult(String.format("执行失败 [错误: %s]", e.getMessage()));
            // 将执行失败信息添加到结果列表中
            results.add("Execution failed: " + e.getMessage());
            // 重新抛出异常，让上层调用者知道发生了错误
            throw e;
        } finally {
            // 执行结束后，将智能体状态重置为已完成
            state = AgentState.COMPLETED;
            // 更新执行记录的最终状态
            agentRecord.setStatus(state.toString());
            // 清理当前计划 ID 对应的智能体内存
            llmService.clearAgentMemory(planId);
        }
        // 返回最终结果，如果结果列表为空则返回空字符串
        return results.isEmpty() ? "" : results.get(results.size() - 1);
    }

    /**
     * 执行智能体的单个步骤操作。 此方法是抽象方法，要求子类必须实现，
     * 用于定义智能体在每个执行步骤中的核心逻辑。
     * 每个步骤的执行逻辑可能包括思考、决策、调用工具等操作。
     *
     * @return 一个 AgentExecResult 对象，包含该步骤的执行结果字符串和智能体执行完此步骤后的状态。
     */
    protected abstract AgentExecResult step();

    /**
     * 清理与指定计划 ID 相关的资源和状态。
     * 此方法为抽象方法，要求子类必须实现，用于定义清理操作的具体逻辑。
     * 清理操作可能包括释放内存、关闭连接、删除临时文件等，确保系统资源被正确回收。
     *
     * @param planId 要清理资源和状态的计划的唯一标识符。
     */
    public abstract void clearUp(String planId);

    /**
     * 检查是否处于卡住状态
     */
    protected boolean isStuck() {
        // 目前判断是如果三次没有调用工具就认为是卡住了，就退出当前step。
        List<Message> memoryEntries = llmService.getAgentMemory().get(getPlanId());
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

    public String getPlanId() {
        return planId;
    }

    /**
     * 处理智能体卡住状态的方法。
     * 当检测到智能体连续多次没有调用工具时，会判定为卡住状态，调用此方法进行处理。
     *
     * @param agentRecord 智能体执行记录对象，用于记录执行过程中的状态和信息。
     */
    private void handleStuckState(AgentExecutionRecord agentRecord) {
        // 记录警告日志，提示检测到智能体卡住，原因是缺少工具调用
        LoggerUtil.warn("检测到智能体卡住 - 缺少工具调用");

        // 结束当前步骤，将智能体状态设置为已完成
        setState(AgentState.COMPLETED);

        // 构建卡住状态的提示信息，包含当前步骤号和执行状态
        String stuckPrompt = """
                检测到智能体响应缺少必需的工具调用。
                请确保每个响应至少包含一个工具调用来推进任务。
                当前步骤: %d
                执行状态: 强制终止
                """.formatted(currentStep);

        // 更新智能体执行记录
        // 标记智能体处于卡住状态
        agentRecord.setStuck(true);
        // 将卡住提示信息作为错误信息记录到执行记录中
        agentRecord.setErrorMessage(stuckPrompt);
        // 更新执行记录中的状态为当前智能体状态
        agentRecord.setStatus(state.toString());

        // 记录错误日志，输出卡住提示信息
        LoggerUtil.error(stuckPrompt);
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    public AgentState getState() {
        return state;
    }

    /**
     * 获取智能体的数据上下文
     * <p>
     * 使用说明：1.返回智能体在执行过程中需要的所有上下文数据
     * 2.数据可包含：- 当前执行状态 - 步骤信息 - 中间结果 - 配置参数
     * 3.数据在run()方法执行时通过setData()设置
     * <p>
     * 不要修改这个方法的实现，如果你需要传递上下文，继承并修改setData方法，这样可以提高getData()的效率
     *
     * @return 包含智能体上下文数据的Map对象
     */
    protected final Map<String, Object> getInitSettingData() {
        return initSettingData;
    }

    public ManusProperties getManusProperties() {
        return manusProperties;
    }

}