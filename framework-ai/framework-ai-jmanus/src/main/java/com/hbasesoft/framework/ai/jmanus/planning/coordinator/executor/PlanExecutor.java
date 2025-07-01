package com.hbasesoft.framework.ai.jmanus.planning.coordinator.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;

import com.hbasesoft.framework.ai.jmanus.agent.AgentState;
import com.hbasesoft.framework.ai.jmanus.agent.BaseAgent;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.po.DynamicAgentPo;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.AgentService;
import com.hbasesoft.framework.ai.jmanus.llm.LlmService;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionPlan;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.ai.jmanus.recorder.entity.PlanExecutionRecord;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * 负责执行计划的类
 */
public class PlanExecutor {

    /** 为 executorParams 中使用的键定义静态常量字符串 */
    public static final String PLAN_STATUS_KEY = "planStatus";

    /**
     * 用于在初始化设置中标识当前步骤索引的键。 在执行步骤时，会将当前步骤的索引以该键存储到初始化设置中，供执行器使用。
     */
    public static final String CURRENT_STEP_INDEX_KEY = "currentStepIndex";

    /**
     * 用于在初始化设置中标识步骤文本描述的键。 该键对应的值为当前步骤的具体描述信息，会被传递给执行器。
     */
    public static final String STEP_TEXT_KEY = "stepText";

    /**
     * 用于在初始化设置中标识额外参数的键。 该键对应的值为执行计划中的额外参数，会在执行步骤时传递给执行器。
     */
    public static final String EXTRA_PARAMS_KEY = "extraParams";

    /**
     * 用于在初始化设置中标识当前步骤执行环境数据的键。 该键对应的值为当前步骤执行时的环境数据，供执行器参考使用。
     */
    public static final String EXECUTION_ENV_STRING_KEY = "current_step_env_data";

    /**
     * 编译一个正则表达式模式，用于匹配字符串开头的方括号及其内部内容。 该模式支持字符串开头存在空白字符，并且方括号内可以包含中文和其他任意字符。 匹配成功后，方括号内的内容会作为第一个捕获组被提取出来。 示例：对于字符串
     * "[示例] 这是一段测试内容"，匹配结果为 "示例"。
     */
    Pattern pattern = Pattern.compile("^\\s*\\[([^\\]]+)\\]");

    private final List<DynamicAgentPo> agents;

    protected final PlanExecutionRecorder recorder;

    private final AgentService agentService;

    private final LlmService llmService;

    public PlanExecutor(List<DynamicAgentPo> agents, PlanExecutionRecorder recorder, AgentService agentService,
        LlmService llmService) {
        this.agents = agents;
        this.recorder = recorder;
        this.agentService = agentService;
        this.llmService = llmService;
    }

    /**
     * 执行整个计划的所有步骤
     * 
     * @param context 执行上下文，包含用户请求和执行过程信息
     */
    public void executeAllSteps(ExecutionContext context) {
        BaseAgent executor = null;

        try {
            // 记录计划执行开始信息
            recordPlanExecutionStart(context);

            // 从执行上下文中获取执行计划
            ExecutionPlan plan = context.getPlan();

            // 从执行计划中获取所有执行步骤
            List<ExecutionStep> steps = plan.getSteps();

            if (CollectionUtils.isNotEmpty(steps)) {
                for (ExecutionStep step : steps) {
                    // 执行单个步骤并获取该步骤的执行器
                    BaseAgent executorinStep = executeStep(step, context);
                    if (executorinStep != null) {
                        executor = executorinStep;
                    }
                }
            }
            // 标记整个计划执行成功
            context.setSuccess(true);
        }
        finally {
            String planId = context.getPlanId();
            // 调用 LLM 服务清除该计划的代理内存
            llmService.clearAgentMemory(planId);
            if (executor != null) {
                executor.clearUp(planId);
            }
        }

    }

    /**
     * 执行单个步骤的方法。 该方法会根据步骤信息获取对应的执行器，初始化执行器设置，然后执行步骤并记录执行过程。
     * 
     * @param step 要执行的步骤信息，包含步骤类型、步骤描述等。
     * @param context 执行上下文，包含用户请求、执行计划等信息。
     * @return 执行该步骤的执行器对象，如果执行过程中出现问题则返回 null。
     */
    private BaseAgent executeStep(ExecutionStep step, ExecutionContext context) {
        try {

            // 从步骤需求中提取步骤类型
            String stepType = getStepFromStepReq(step.getStepRequirement());

            // 获取当前步骤的索引
            int stepIndex = step.getStepIndex();

            // 获取计划的执行状态字符串
            String planStatus = context.getPlan().getPlanExecutionStateStringFormat(true);

            // 获取步骤的文本描述
            String stepText = step.getStepRequirement();

            // 初始化执行器的设置参数
            Map<String, Object> initSettings = new HashMap<>();
            // 将计划状态添加到初始化设置中
            initSettings.put(PLAN_STATUS_KEY, planStatus);
            // 将当前步骤索引添加到初始化设置中
            initSettings.put(CURRENT_STEP_INDEX_KEY, String.valueOf(stepIndex));
            // 将步骤文本描述添加到初始化设置中
            initSettings.put(STEP_TEXT_KEY, stepText);
            // 将计划的执行参数添加到初始化设置中
            initSettings.put(EXTRA_PARAMS_KEY, context.getPlan().getExecutionParams());

            // 根据步骤类型获取对应的执行器
            BaseAgent executor = getExecutorForStep(stepType, context, initSettings);

            // 检查是否成功获取到执行器
            if (executor == null) {
                // 记录错误日志
                LoggerUtil.info("未找到对应步骤类型的执行器: {0}", stepType);
                // 设置步骤执行结果为未找到执行器
                step.setResult("找到对应步骤类型的执行器: " + stepType);
                return null;
            }

            // 将执行器关联到当前步骤
            step.setAgent(executor);

            // 设置执行器的状态为执行中
            executor.setState(AgentState.IN_PROGRESS);

            // 记录步骤开始执行
            recordStepStart(step, context);

            // 执行步骤并获取执行结果
            String stepResultStr = executor.run();

            // 设置步骤的执行结果
            step.setResult(stepResultStr);
            return executor;
        }
        catch (Exception e) {
            // 记录执行步骤时发生的错误日志
            LoggerUtil.error(e, "执行步骤失败{0}", e.getMessage());

            // 设置步骤执行结果为执行失败及错误信息
            step.setResult("执行失败: " + e.getMessage());
        }
        finally {
            // 无论步骤执行成功与否，都记录步骤执行结束
            recordStepEnd(step, context);
        }
        return null;
    }

    /**
     * 根据步骤类型获取对应的执行器。 该方法会遍历可用的智能代理列表，查找与步骤类型匹配的代理， 若找到匹配的代理，则使用代理服务创建动态基础代理对象作为执行器返回； 若未找到匹配的代理，则抛出
     * IllegalArgumentException 异常。
     * 
     * @param stepType 步骤类型，用于匹配对应的智能代理。
     * @param context 执行上下文，包含执行计划等信息。
     * @param initSettings 执行器的初始化设置，以键值对形式存储。
     * @return 与步骤类型匹配的 BaseAgent 执行器对象。
     * @throws 当未找到与步骤类型匹配的代理时抛出此异常。
     */
    private BaseAgent getExecutorForStep(String stepType, ExecutionContext context, Map<String, Object> initSettings) {
        // 遍历可用的智能代理列表，查找与步骤类型匹配的代理
        for (DynamicAgentPo agent : agents) {
            // 忽略大小写比较代理名称和步骤类型，判断是否匹配
            if (agent.getAgentName().equalsIgnoreCase(stepType)) {
                // 若匹配成功，使用代理服务创建动态基础代理对象并返回
                return agentService.createDynamicBaseAgent(agent.getAgentName(), context.getPlan().getPlanId(),
                    initSettings);
            }
        }

        // 若遍历完列表都未找到匹配的代理，抛出异常提示未找到对应执行器
        throw new IllegalArgumentException("未找到对应步骤类型的代理执行器，请检查代理列表: " + stepType);
    }

    /**
     * 从步骤需求字符串中提取步骤类型。 该方法使用预定义的正则表达式模式匹配步骤需求字符串开头的方括号内的内容， 若匹配成功，则将匹配内容去除首尾空格并转换为小写后返回； 若未匹配成功，则返回默认的代理类型
     * "DEFAULT_AGENT"。
     * 
     * @param stepRequirement 步骤需求字符串，可能包含方括号包裹的步骤类型信息。
     * @return 提取到的步骤类型字符串，若未匹配到则返回 "DEFAULT_AGENT"。
     */
    private String getStepFromStepReq(String stepRequirement) {
        // 使用预定义的正则表达式模式创建 Matcher 对象，用于匹配步骤需求字符串
        Matcher matcher = pattern.matcher(stepRequirement);
        // 检查是否匹配到符合正则表达式的内容
        if (matcher.find()) {
            // 若匹配成功，获取匹配到的第一个分组内容，去除首尾空格并转换为小写后返回
            return matcher.group(1).trim().toLowerCase();
        }
        // 若未匹配到符合条件的内容，返回默认的代理类型
        return "DEFAULT_AGENT";
    }

    private void recordStepStart(ExecutionStep step, ExecutionContext context) {
        // Update current step index in PlanExecutionRecord
        PlanExecutionRecord record = getOrCreatePlanExecutionRecord(context);
        if (record != null) {
            int currentStepIndex = step.getStepIndex();
            record.setCurrentStepIndex(currentStepIndex);
            retrieveExecutionSteps(context, record);
            getRecorder().recordPlanExecution(record);
        }
    }

    private void recordPlanExecutionStart(ExecutionContext context) {
        PlanExecutionRecord record = getOrCreatePlanExecutionRecord(context);

        record.setPlanId(context.getPlan().getPlanId());
        record.setStartTime(DateUtil.getCurrentDate());
        record.setTitle(context.getPlan().getTitle());
        record.setUserRequest(context.getUserRequest());

        retrieveExecutionSteps(context, record);
        getRecorder().recordPlanExecution(record);
    }

    private void recordStepEnd(ExecutionStep step, ExecutionContext context) {
        // Update step status in PlanExecutionRecord
        PlanExecutionRecord record = getOrCreatePlanExecutionRecord(context);
        if (record != null) {
            int currentStepIndex = step.getStepIndex();
            record.setCurrentStepIndex(currentStepIndex);
            // Retrieve all step statuses
            retrieveExecutionSteps(context, record);
            getRecorder().recordPlanExecution(record);
        }
    }

    private void retrieveExecutionSteps(ExecutionContext context, PlanExecutionRecord record) {
        List<String> steps = new ArrayList<>();
        for (ExecutionStep step : context.getPlan().getSteps()) {
            steps.add(step.getStepInStr());
        }
        record.setSteps(steps);
    }

    private PlanExecutionRecord getOrCreatePlanExecutionRecord(ExecutionContext context) {
        PlanExecutionRecord record = getRecorder().getExecutionRecord(context.getPlanId());
        if (record == null) {
            record = new PlanExecutionRecord(context.getPlanId());
        }
        getRecorder().recordPlanExecution(record);
        return record;
    }

    protected PlanExecutionRecorder getRecorder() {
        return recorder;
    }

}
