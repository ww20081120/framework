/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallback;

import com.hbasesoft.framework.ai.agent.agent.AgentExecResult;
import com.hbasesoft.framework.ai.agent.agent.AgentState;
import com.hbasesoft.framework.ai.agent.agent.ReActAgent;
import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.ai.agent.dynamic.model.model.vo.ModelConfig;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.model.enums.PromptEnum;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.agent.llm.ILlmService;
import com.hbasesoft.framework.ai.agent.llm.StreamingResponseHandler;
import com.hbasesoft.framework.ai.agent.planning.IPlanningFactory.ToolCallBackContext;
import com.hbasesoft.framework.ai.agent.planning.executor.PlanExecutor;
import com.hbasesoft.framework.ai.agent.planning.service.IUserInputService;
import com.hbasesoft.framework.ai.agent.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.ai.agent.recorder.model.ExecutionStatus;
import com.hbasesoft.framework.ai.agent.recorder.model.ThinkActRecord;
import com.hbasesoft.framework.ai.agent.tool.ToolCallBiFunctionDef;
import com.hbasesoft.framework.ai.agent.tool.fromInput.FormInputTool;
import com.hbasesoft.framework.ai.agent.tool.terminate.TerminableTool;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import reactor.core.publisher.Flux;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.agent <br>
 */
public class DynamicAgent extends ReActAgent {

    private static final String CURRENT_STEP_ENV_DATA_KEY = "current_step_env_data";

    private final String agentName;

    private final String agentDescription;

    private final String nextStepPrompt;

    private ToolCallbackProvider toolCallbackProvider;

    private final List<String> availableToolKeys;

    private ChatResponse response;

    private StreamingResponseHandler.StreamingResult streamResult;

    private Prompt userPrompt;

    // Store current created ThinkActRecord ID for subsequent action recording
    private Long currentThinkActRecordId;

    private final ToolCallingManager toolCallingManager;

    private final IUserInputService userInputService;

    private final ModelConfig model;

    private final StreamingResponseHandler streamingResponseHandler;

    private int retryTimes = 1;

    public void clearUp(String planId) {
        Map<String, ToolCallBackContext> toolCallBackContext = toolCallbackProvider.getToolCallBackContext();
        for (ToolCallBackContext toolCallBack : toolCallBackContext.values()) {
            try {
                toolCallBack.getFunctionInstance().cleanup(planId);
            }
            catch (Exception e) {
                LoggerUtil.error("Error cleaning up tool callback context: {0}", e.getMessage(), e);
            }
        }
        // Also remove any pending form input tool for this planId
        if (userInputService != null) {
            userInputService.removeFormInputTool(planId);
        }
    }

    public DynamicAgent(ILlmService llmService, PlanExecutionRecorder planExecutionRecorder,
        IManusProperties manusProperties, String name, String description, String nextStepPrompt,
        List<String> availableToolKeys, ToolCallingManager toolCallingManager, Map<String, Object> initialAgentSetting,
        IUserInputService userInputService, PromptService promptService, ModelConfig model,
        StreamingResponseHandler streamingResponseHandler) {
        super(llmService, planExecutionRecorder, manusProperties, initialAgentSetting, promptService);
        this.agentName = name;
        this.agentDescription = description;
        this.nextStepPrompt = nextStepPrompt;
        this.availableToolKeys = new ArrayList<String>(availableToolKeys);
        if (!this.availableToolKeys.contains("terminate")) {
            this.availableToolKeys.add("terminate");
        }
        this.toolCallingManager = toolCallingManager;
        this.userInputService = userInputService;
        this.model = model;
        this.streamingResponseHandler = streamingResponseHandler;

        if (initialAgentSetting.containsKey("agent_retry_times")) {
            try {
                this.retryTimes = Integer.parseInt(initialAgentSetting.get("agent_retry_times").toString());
            }
            catch (Exception e) {
                LoggerUtil.warn("Invalid agent_retry_times value: {0}, using default 1",
                    initialAgentSetting.get("agent_retry_times"));
            }
        }

    }

    @Override
    protected boolean think() {
        collectAndSetEnvDataForTools();

        try {
            return executeWithRetry(retryTimes);
        }
        catch (Exception e) {
            LoggerUtil.error(e,
                String.format("🚨 Oops! The %s's thinking process hit a snag: %s", getName(), e.getMessage()));
            LoggerUtil.info("Exception occurred " + e.getMessage());

            // Record thinking failure
            PlanExecutionRecorder.PlanExecutionParams params = new PlanExecutionRecorder.PlanExecutionParams();
            params.setCurrentPlanId(getCurrentPlanId());
            params.setRootPlanId(getRootPlanId());
            params.setThinkActRecordId(getThinkActRecordId());
            params.setAgentName(getName());
            params.setAgentDescription(getDescription());
            params.setThinkInput(null);
            params.setThinkOutput(null);
            params.setActionNeeded(false);
            params.setToolName(null);
            params.setToolParameters(null);
            params.setModelName(null);
            params.setErrorMessage(e.getMessage());
            planExecutionRecorder.recordThinkingAndAction(params);

            return false;
        }

    }

    private boolean executeWithRetry(int maxRetries) throws Exception {
        int attempt = 0;
        while (attempt < maxRetries) {
            attempt++;
            Message systemMessage = getThinkMessage();
            // Use current env as user message
            Message currentStepEnvMessage = currentStepEnvMessage();
            // Record think message
            List<Message> thinkMessages = Arrays.asList(systemMessage, currentStepEnvMessage);
            String thinkInput = thinkMessages.toString();

            LoggerUtil.debug("Messages prepared for the prompt: {0}", thinkMessages);
            // Build current prompt. System message is the first message
            List<Message> messages = new ArrayList<>(Collections.singletonList(systemMessage));
            // Add history message.
            ChatMemory chatMemory = llmService.getAgentMemory(manusProperties.getMaxMemory());
            List<Message> historyMem = chatMemory.get(getCurrentPlanId());
            messages.addAll(historyMem);
            messages.add(currentStepEnvMessage);
            // Call the LLM
            ChatOptions chatOptions = OpenAiChatOptions.builder().internalToolExecutionEnabled(false)
                .parallelToolCalls(manusProperties.getParallelToolCalls()).build();
            userPrompt = new Prompt(messages, chatOptions);
            List<ToolCallback> callbacks = getToolCallList();
            ChatClient chatClient;
            if (model == null) {
                chatClient = llmService.getAgentChatClient();
            }
            else {
                chatClient = llmService.getDynamicChatClient(model);
            }
            // Use streaming response handler for better user experience and content
            // merging
            Flux<ChatResponse> responseFlux = chatClient.prompt(userPrompt).toolCallbacks(callbacks).stream()
                .chatResponse();
            streamResult = streamingResponseHandler.processStreamingResponse(responseFlux,
                "Agent " + getName() + " thinking", getCurrentPlanId());

            response = streamResult.getLastResponse();
            String modelName = response.getMetadata().getModel();

            // Use merged content from streaming handler
            List<ToolCall> toolCalls = streamResult.getEffectiveToolCalls();
            String responseByLLm = streamResult.getEffectiveText();

            LoggerUtil.info(String.format("✨ %s's thoughts: %s", getName(), responseByLLm));
            LoggerUtil.info(String.format("🛠️ %s selected %d tools to use", getName(), toolCalls.size()));

            if (!toolCalls.isEmpty()) {
                LoggerUtil.info(String.format("🧰 Tools being prepared: %s",
                    toolCalls.stream().map(ToolCall::name).collect(Collectors.toList())));

                // Record successful thinking and action preparation
                String toolName = toolCalls.get(0).name();
                String toolParameters = toolCalls.get(0).arguments();
                PlanExecutionRecorder.PlanExecutionParams params = new PlanExecutionRecorder.PlanExecutionParams();
                params.setCurrentPlanId(getCurrentPlanId());
                params.setRootPlanId(getRootPlanId());
                params.setThinkActRecordId(getThinkActRecordId());
                params.setAgentName(getName());
                params.setAgentDescription(getDescription());
                params.setThinkInput(thinkInput);
                params.setThinkOutput(responseByLLm);
                params.setActionNeeded(true);
                params.setToolName(toolName);
                params.setToolParameters(toolParameters);
                params.setModelName(modelName);
                params.setErrorMessage(null);
                currentThinkActRecordId = planExecutionRecorder.recordThinkingAndAction(params);

                return true;
            }
            LoggerUtil.warn("Attempt {0}: No tools selected. Retrying...", attempt);
        }

        // Record thinking failure (no tools selected)
        PlanExecutionRecorder.PlanExecutionParams params = new PlanExecutionRecorder.PlanExecutionParams();
        params.setCurrentPlanId(getCurrentPlanId());
        params.setRootPlanId(getRootPlanId());
        params.setThinkActRecordId(getThinkActRecordId());
        params.setAgentName(getName());
        params.setAgentDescription(getDescription());
        params.setThinkInput(null);
        params.setThinkOutput("No tools selected after retries");
        params.setActionNeeded(false);
        params.setToolName(null);
        params.setToolParameters(null);
        params.setModelName(null);
        params.setErrorMessage("Failed to select tools after " + maxRetries + " attempts");
        planExecutionRecorder.recordThinkingAndAction(params);

        return false;
    }

    private List<ThinkActRecord.ActToolInfo> createActToolInfoList(List<ToolCall> toolCalls) {
        List<ThinkActRecord.ActToolInfo> actToolInfoList = new ArrayList<>();
        for (ToolCall toolCall : toolCalls) {
            ThinkActRecord.ActToolInfo actToolInfo = new ThinkActRecord.ActToolInfo(toolCall.name(),
                toolCall.arguments(), toolCall.id());
            actToolInfoList.add(actToolInfo);
            if (!manusProperties.getParallelToolCalls()) {
                break;
            }
        }
        return actToolInfoList;
    }

    @Override
    protected AgentExecResult act() {
        ToolExecutionResult toolExecutionResult = null;
        String lastToolCallResult = null;
        List<ThinkActRecord.ActToolInfo> actToolInfoList = null;

        try {
            List<ToolCall> toolCalls = streamResult.getEffectiveToolCalls();

            // Create ActToolInfo list
            actToolInfoList = createActToolInfoList(toolCalls);

            // Execute tool calls
            toolExecutionResult = toolCallingManager.executeToolCalls(userPrompt, response);
            processMemory(toolExecutionResult);

            // Get tool response messages
            ToolResponseMessage toolResponseMessage = (ToolResponseMessage) toolExecutionResult.conversationHistory()
                .get(toolExecutionResult.conversationHistory().size() - 1);

            // Set execution result for each tool
            setActToolInfoResults(actToolInfoList, toolResponseMessage.getResponses());

            // Get execution result of the last tool
            if (!toolResponseMessage.getResponses().isEmpty()) {
                lastToolCallResult = toolResponseMessage.getResponses()
                    .get(toolResponseMessage.getResponses().size() - 1).responseData();
            }

            LoggerUtil.info(String.format("🔧 Tool %s's executing result: %s", getName(), lastToolCallResult));

            // Handle special tool type logic - only check the first tool
            ToolCall firstToolCall = toolCalls.get(0);
            String firstToolName = firstToolCall.name();
            ToolCallBiFunctionDef<?> toolInstance = getToolCallBackContext(firstToolName).getFunctionInstance();

            // Handle FormInputTool logic
            if (toolInstance instanceof FormInputTool) {
                AgentExecResult formResult = handleFormInputTool((FormInputTool) toolInstance, actToolInfoList);
                if (formResult != null) {
                    return formResult;
                }
            }

            // Handle TerminableTool logic
            if (toolInstance instanceof TerminableTool) {
                TerminableTool terminableTool = (TerminableTool) toolInstance;
                if (terminableTool.canTerminate()) {
                    LoggerUtil.info("TerminableTool can terminate for planId: {0}", getCurrentPlanId());
                    userInputService.removeFormInputTool(getCurrentPlanId());

                    // Record successfully completed action result
                    recordActionResult(actToolInfoList, lastToolCallResult, ExecutionStatus.FINISHED, null, false);

                    return new AgentExecResult(lastToolCallResult, AgentState.COMPLETED);
                }
                else {
                    LoggerUtil.info("TerminableTool cannot terminate yet for planId: {0}", getCurrentPlanId());
                }
            }

            // Record successful action result
            recordActionResult(actToolInfoList, lastToolCallResult, ExecutionStatus.RUNNING, null, false);

            return new AgentExecResult(lastToolCallResult, AgentState.IN_PROGRESS);
        }
        catch (Exception e) {
            LoggerUtil.error(e);
            LoggerUtil.info("Exception occurred", e);

            // Record failed action result
            List<ToolCall> toolCalls = streamResult.getEffectiveToolCalls();
            if (toolCalls != null && !toolCalls.isEmpty()) {
                actToolInfoList = createActToolInfoList(toolCalls);
            }
            StringBuilder errorMessage = new StringBuilder("Error executing tools: ");
            errorMessage.append(e.getMessage());

            String firstToolcall = actToolInfoList != null && !actToolInfoList.isEmpty()
                ? actToolInfoList.get(0).getParameters().toString()
                : "unknown";
            errorMessage.append("  . llm return param :  ").append(firstToolcall);

            recordActionResult(actToolInfoList, errorMessage.toString(), ExecutionStatus.RUNNING,
                errorMessage.toString(), false);

            userInputService.removeFormInputTool(getCurrentPlanId()); // Clean up on error
            processMemory(toolExecutionResult); // Process memory even on error
            return new AgentExecResult(e.getMessage(), AgentState.FAILED);
        }
    }

    /**
     * Set act tool info results for all executed tools
     */
    private void setActToolInfoResults(List<ThinkActRecord.ActToolInfo> actToolInfoList,
        List<ToolResponseMessage.ToolResponse> responses) {
        for (ToolResponseMessage.ToolResponse toolResponse : responses) {
            String curToolResp = toolResponse.responseData();
            LoggerUtil.info(String.format("🔧 Tool %s's executing result: %s", getName(), curToolResp));

            // Find corresponding ActToolInfo and set result
            for (ThinkActRecord.ActToolInfo actToolInfo : actToolInfoList) {
                if (actToolInfo.getId().equals(toolResponse.id())) {
                    actToolInfo.setResult(curToolResp);
                    break;
                }
            }

            if (!manusProperties.getParallelToolCalls()) {
                break;
            }
        }
    }

    /**
     * Handle FormInputTool specific logic
     */
    private AgentExecResult handleFormInputTool(FormInputTool formInputTool,
        List<ThinkActRecord.ActToolInfo> actToolInfoList) {
        // Check if the tool is waiting for user input
        if (formInputTool.getInputState() == FormInputTool.InputState.AWAITING_USER_INPUT) {
            LoggerUtil.info("FormInputTool is awaiting user input for planId: {0}", getCurrentPlanId());
            userInputService.storeFormInputTool(getCurrentPlanId(), formInputTool);
            // Wait for user input or timeout
            waitForUserInputOrTimeout(formInputTool);

            // After waiting, check the state again
            if (formInputTool.getInputState() == FormInputTool.InputState.INPUT_RECEIVED) {
                LoggerUtil.info("User input received for planId: {0}", getCurrentPlanId());

                UserMessage userMessage = UserMessage.builder()
                    .text("User input received for form: " + formInputTool.getCurrentToolStateString()).build();
                processUserInputToMemory(userMessage);

                // Update the result in actToolInfoList
                if (!actToolInfoList.isEmpty()) {
                    actToolInfoList.get(0).setResult(formInputTool.getCurrentToolStateString());
                }
            }
            else if (formInputTool.getInputState() == FormInputTool.InputState.INPUT_TIMEOUT) {
                LoggerUtil.warn("Input timeout occurred for FormInputTool for planId: {0}", getCurrentPlanId());

                UserMessage userMessage = UserMessage.builder().text("Input timeout occurred for form: ").build();
                processUserInputToMemory(userMessage);
                userInputService.removeFormInputTool(getCurrentPlanId());

                // Record input timeout action result
                recordActionResult(actToolInfoList, "Input timeout occurred", ExecutionStatus.RUNNING,
                    "Input timeout occurred for FormInputTool", false);

                return new AgentExecResult("Input timeout occurred.", AgentState.IN_PROGRESS);
            }
        }
        return null;
    }

    /**
     * Record action result with simplified parameters
     */
    private void recordActionResult(List<ThinkActRecord.ActToolInfo> actToolInfoList, String actionResult,
        ExecutionStatus status, String errorMessage, boolean subPlanCreated) {

        String toolName = null;
        String toolParameters = null;
        String actionDescription = "Tool execution";

        if (actToolInfoList != null && !actToolInfoList.isEmpty()) {
            ThinkActRecord.ActToolInfo firstTool = actToolInfoList.get(0);
            toolName = firstTool.getName();
            toolParameters = firstTool.getParameters();
            actionDescription = "Executing tool: " + toolName;
        }

        PlanExecutionRecorder.PlanExecutionParams params = new PlanExecutionRecorder.PlanExecutionParams();
        params.setCurrentPlanId(getCurrentPlanId());
        params.setRootPlanId(getRootPlanId());
        params.setThinkActRecordId(getThinkActRecordId());
        params.setCreatedThinkActRecordId(currentThinkActRecordId);
        params.setActionDescription(actionDescription);
        params.setActionResult(actionResult);
        params.setStatus(status);
        params.setErrorMessage(errorMessage);
        params.setToolName(toolName);
        params.setToolParameters(toolParameters);
        params.setSubPlanCreated(subPlanCreated);
        params.setActToolInfoList(actToolInfoList);

        planExecutionRecorder.recordActionResult(params);
    }

    private void processUserInputToMemory(UserMessage userMessage) {
        if (userMessage != null && userMessage.getText() != null) {
            // Process the user message to update memory
            String userInput = userMessage.getText();

            if (!StringUtils.isBlank(userInput)) {
                // Add user input to memory

                llmService.getAgentMemory(manusProperties.getMaxMemory()).add(getCurrentPlanId(), userMessage);

            }
        }
    }

    private void processMemory(ToolExecutionResult toolExecutionResult) {
        if (toolExecutionResult == null) {
            return;
        }
        // Process the conversation history to update memory
        List<Message> messages = toolExecutionResult.conversationHistory();
        if (messages.isEmpty()) {
            return;
        }
        // clear current plan memory
        llmService.getAgentMemory(manusProperties.getMaxMemory()).clear(getCurrentPlanId());
        for (Message message : messages) {
            // exclude all system message
            if (message instanceof SystemMessage) {
                continue;
            }
            // exclude env data message
            if (message instanceof UserMessage userMessage
                && userMessage.getMetadata().containsKey(CURRENT_STEP_ENV_DATA_KEY)) {
                continue;
            }
            // only keep assistant message and tool_call message
            llmService.getAgentMemory(manusProperties.getMaxMemory()).add(getCurrentPlanId(), message);
        }
    }

    @Override
    public String getName() {
        return this.agentName;
    }

    @Override
    public String getDescription() {
        return this.agentDescription;
    }

    @Override
    protected Message getNextStepWithEnvMessage() {
        if (StringUtils.isBlank(this.nextStepPrompt)) {
            return new UserMessage("");
        }
        PromptTemplate promptTemplate = new SystemPromptTemplate(this.nextStepPrompt);
        Message userMessage = promptTemplate.createMessage(getMergedData());
        return userMessage;
    }

    private Map<String, Object> getMergedData() {
        Map<String, Object> data = new HashMap<>();
        data.putAll(getInitSettingData());
        data.put(PlanExecutor.EXECUTION_ENV_STRING_KEY, convertEnvDataToString());
        return data;
    }

    @Override
    protected Message getThinkMessage() {
        Message baseThinkPrompt = super.getThinkMessage();
        Message nextStepWithEnvMessage = getNextStepWithEnvMessage();
        SystemMessage thinkMessage = new SystemMessage("""
            <SystemInfo>
            %s
            </SystemInfo>

            <AgentInfo>
            %s
            </AgentInfo>
            """.formatted(baseThinkPrompt.getText(), nextStepWithEnvMessage.getText()));
        return thinkMessage;
    }

    /**
     * Current step env data
     * 
     * @return User message for current step environment data
     */
    private Message currentStepEnvMessage() {
        Message stepEnvMessage = promptService.createUserMessage(PromptEnum.AGENT_CURRENT_STEP_ENV.getPromptName(),
            getMergedData());
        // mark as current step env data
        stepEnvMessage.getMetadata().put(CURRENT_STEP_ENV_DATA_KEY, Boolean.TRUE);
        return stepEnvMessage;
    }

    public ToolCallBackContext getToolCallBackContext(String toolKey) {
        Map<String, ToolCallBackContext> toolCallBackContext = toolCallbackProvider.getToolCallBackContext();
        if (toolCallBackContext.containsKey(toolKey)) {
            return toolCallBackContext.get(toolKey);
        }
        else {
            LoggerUtil.warn("Tool callback for {0} not found in the map.", toolKey);
            return null;
        }
    }

    @Override
    public List<ToolCallback> getToolCallList() {
        List<ToolCallback> toolCallbacks = new ArrayList<>();
        Map<String, ToolCallBackContext> toolCallBackContext = toolCallbackProvider.getToolCallBackContext();
        for (String toolKey : availableToolKeys) {
            if (toolCallBackContext.containsKey(toolKey)) {
                ToolCallBackContext toolCallback = toolCallBackContext.get(toolKey);
                if (toolCallback != null) {
                    toolCallbacks.add(toolCallback.getToolCallback());
                }
            }
            else {
                LoggerUtil.warn("Tool callback for {0} not found in the map.", toolKey);
            }
        }
        return toolCallbacks;
    }

    public void addEnvData(String key, String value) {
        Map<String, Object> data = super.getInitSettingData();
        if (data == null) {
            throw new IllegalStateException("Data map is null. Cannot add environment data.");
        }
        data.put(key, value);
    }

    public void setToolCallbackProvider(ToolCallbackProvider toolCallbackProvider) {
        this.toolCallbackProvider = toolCallbackProvider;
    }

    protected String collectEnvData(String toolCallName) {
        ToolCallBackContext context = toolCallbackProvider.getToolCallBackContext().get(toolCallName);
        if (context != null) {
            return context.getFunctionInstance().getCurrentToolStateString();
        }
        // If corresponding tool callback context is not found, return empty string
        return "";
    }

    public void collectAndSetEnvDataForTools() {

        Map<String, Object> toolEnvDataMap = new HashMap<>();

        Map<String, Object> oldMap = getEnvData();
        toolEnvDataMap.putAll(oldMap);

        // Overwrite old data with new data
        for (String toolKey : availableToolKeys) {
            String envData = collectEnvData(toolKey);
            toolEnvDataMap.put(toolKey, envData);
        }
        LoggerUtil.debug("Collected tool environment data: {0}", toolEnvDataMap);

        setEnvData(toolEnvDataMap);
    }

    public String convertEnvDataToString() {
        StringBuilder envDataStringBuilder = new StringBuilder();

        for (String toolKey : availableToolKeys) {
            Object value = getEnvData().get(toolKey);
            if (value == null || value.toString().isEmpty()) {
                continue; // Skip tools with no data
            }
            envDataStringBuilder.append(toolKey).append(" context information:\n");
            envDataStringBuilder.append("    ").append(value.toString()).append("\n");
        }

        return envDataStringBuilder.toString();
    }

    // Add a method to wait for user input or handle timeout.
    private void waitForUserInputOrTimeout(FormInputTool formInputTool) {
        LoggerUtil.info("Waiting for user input for planId: {0}...", getCurrentPlanId());
        long startTime = System.currentTimeMillis();
        // Get timeout from ManusProperties and convert to milliseconds
        long userInputTimeoutMs = getManusProperties().getUserInputTimeout() * 1000L;

        while (formInputTool.getInputState() == FormInputTool.InputState.AWAITING_USER_INPUT) {
            if (System.currentTimeMillis() - startTime > userInputTimeoutMs) {
                LoggerUtil.warn("Timeout waiting for user input for planId: {0}", getCurrentPlanId());
                formInputTool.handleInputTimeout(); // This will change its state to
                // INPUT_TIMEOUT
                break;
            }
            try {
                // Poll for input state change. In a real scenario, this might involve
                // a more sophisticated mechanism like a Future or a callback from the UI.
                TimeUnit.MILLISECONDS.sleep(500); // Check every 500ms
            }
            catch (InterruptedException e) {
                LoggerUtil.warn("Interrupted while waiting for user input for planId: {0}", getCurrentPlanId());
                Thread.currentThread().interrupt();
                formInputTool.handleInputTimeout(); // Treat interruption as timeout for
                // simplicity
                break;
            }
        }
        if (formInputTool.getInputState() == FormInputTool.InputState.INPUT_RECEIVED) {
            LoggerUtil.info("User input received for planId: {0}", getCurrentPlanId());
        }
        else if (formInputTool.getInputState() == FormInputTool.InputState.INPUT_TIMEOUT) {
            LoggerUtil.warn("User input timed out for planId: {0}", getCurrentPlanId());
        }
    }
}
