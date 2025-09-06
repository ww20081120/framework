/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.planning.finalizer;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.ai.agent.dynamic.memory.advisor.CustomMessageChatMemoryAdvisor;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.model.enums.PromptEnum;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.agent.llm.ILlmService;
import com.hbasesoft.framework.ai.agent.llm.StreamingResponseHandler;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.agent.planning.model.vo.PlanInterface;
import com.hbasesoft.framework.ai.agent.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import reactor.core.publisher.Flux;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.planning.finalizer <br>
 */
public class PlanFinalizer {

    private final ILlmService llmService;

    protected final PlanExecutionRecorder recorder;

    private final PromptService promptService;

    private final IManusProperties manusProperties;

    private final StreamingResponseHandler streamingResponseHandler;

    public PlanFinalizer(ILlmService llmService, PlanExecutionRecorder recorder, PromptService promptService,
        IManusProperties manusProperties, StreamingResponseHandler streamingResponseHandler) {
        this.llmService = llmService;
        this.recorder = recorder;
        this.promptService = promptService;
        this.manusProperties = manusProperties;
        this.streamingResponseHandler = streamingResponseHandler;
    }

    /**
     * Generate the execution summary of the plan
     * 
     * @param context execution context, containing the user request and the execution process information
     */
    public void generateSummary(ExecutionContext context) {
        if (context == null || context.getPlan() == null) {
            throw new IllegalArgumentException("ExecutionContext or its plan cannot be null");
        }
        if (!context.isNeedSummary()) {
            LoggerUtil.info("No need to generate summary, use code generate summary instead");
            String summary = context.getPlan().getPlanExecutionStateStringFormat(false);
            context.setResultSummary(summary);
            recordPlanCompletion(context, summary);
            return;

        }
        PlanInterface plan = context.getPlan();
        String executionDetail = plan.getPlanExecutionStateStringFormat(false);
        try {
            String userRequest = context.getUserRequest();

            Message combinedMessage = promptService.createUserMessage(
                PromptEnum.PLANNING_PLAN_FINALIZER.getPromptName(),
                Map.of("executionDetail", executionDetail, "userRequest", userRequest));

            Prompt prompt = new Prompt(List.of(combinedMessage));

            ChatClient.ChatClientRequestSpec requestSpec = llmService.getPlanningChatClient().prompt(prompt);
            if (context.isUseMemory()) {
                requestSpec
                    .advisors(memoryAdvisor -> memoryAdvisor.param(ChatMemory.CONVERSATION_ID, context.getMemoryId()));
                requestSpec.advisors(CustomMessageChatMemoryAdvisor
                    .builder(llmService.getConversationMemory(manusProperties.getMaxMemory()), context.getUserRequest(),
                        CustomMessageChatMemoryAdvisor.AdvisorType.AFTER)
                    .build());
            }

            // Use streaming response handler for summary generation
            Flux<ChatResponse> responseFlux = requestSpec.stream().chatResponse();
            String summary = streamingResponseHandler.processStreamingTextResponse(responseFlux, "Summary generation",
                context.getCurrentPlanId());
            context.setResultSummary(summary);

            recordPlanCompletion(context, summary);
            LoggerUtil.info("Generated summary: {0}", summary);
        }
        catch (Exception e) {
            LoggerUtil.error("Error generating summary with LLM", e);
            throw new RuntimeException("Failed to generate summary", e);
        }
    }

    /**
     * Record plan completion with the given context and summary
     * 
     * @param context Execution context
     * @param summary Plan execution summary
     */
    private void recordPlanCompletion(ExecutionContext context, String summary) {
        if (context == null || context.getPlan() == null) {
            LoggerUtil.warn("Cannot record plan completion: context or plan is null");
            return;
        }

        String currentPlanId = context.getPlan().getCurrentPlanId();
        String rootPlanId = context.getPlan().getRootPlanId();
        Long thinkActRecordId = context.getThinkActRecordId();

        recorder.recordPlanCompletion(currentPlanId, rootPlanId, thinkActRecordId, summary);
    }

    /**
     * Generate direct LLM response for simple requests
     * 
     * @param context execution context containing the user request
     */
    public void generateDirectResponse(ExecutionContext context) {
        if (context == null || context.getUserRequest() == null) {
            throw new IllegalArgumentException("ExecutionContext or user request cannot be null");
        }

        String userRequest = context.getUserRequest();
        LoggerUtil.info("Generating direct response for user request: {0}", userRequest);

        try {
            // Create a simple prompt for direct response
            Message directMessage = promptService.createUserMessage(PromptEnum.DIRECT_RESPONSE.getPromptName(),
                Map.of("userRequest", userRequest));

            Prompt prompt = new Prompt(List.of(directMessage));
            ChatClient.ChatClientRequestSpec requestSpec = llmService.getPlanningChatClient().prompt(prompt);

            if (context.isUseMemory()) {
                requestSpec
                    .advisors(memoryAdvisor -> memoryAdvisor.param(ChatMemory.CONVERSATION_ID, context.getMemoryId()));
                requestSpec.advisors(CustomMessageChatMemoryAdvisor
                    .builder(llmService.getConversationMemory(manusProperties.getMaxMemory()), context.getUserRequest(),
                        CustomMessageChatMemoryAdvisor.AdvisorType.AFTER)
                    .build());
            }

            // Use streaming response handler for direct response generation
            Flux<ChatResponse> responseFlux = requestSpec.stream().chatResponse();
            String directResponse = streamingResponseHandler.processStreamingTextResponse(responseFlux,
                "Direct response", context.getCurrentPlanId());
            context.setResultSummary(directResponse);

            recordPlanCompletion(context, directResponse);
            LoggerUtil.info("Generated direct response: {0}", directResponse);

        }
        catch (Exception e) {
            LoggerUtil.error("Error generating direct response for request: {0}", userRequest, e);
            throw new RuntimeException("Failed to generate direct response", e);
        }
    }

}
