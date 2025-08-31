/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.planning.creator;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import com.hbasesoft.framework.ai.agent.config.IManusProperties;
import com.hbasesoft.framework.ai.agent.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.agent.dynamic.memory.advisor.CustomMessageChatMemoryAdvisor;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.model.enums.PromptEnum;
import com.hbasesoft.framework.ai.agent.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.agent.llm.ILlmService;
import com.hbasesoft.framework.ai.agent.llm.StreamingResponseHandler;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.agent.planning.model.vo.PlanInterface;
import com.hbasesoft.framework.ai.agent.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.ai.agent.tool.planning.PlanningToolInterface;
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
 * @see com.hbasesoft.framework.ai.agent.planning.creator <br>
 */

/**
 * The class responsible for creating the execution plan
 */
public class PlanCreator {

	private final List<DynamicAgent> agents;

	private final ILlmService llmService;

	private final PlanningToolInterface planningTool;

	protected final PlanExecutionRecorder recorder;

	private final PromptService promptService;

	private final IManusProperties manusProperties;

	private final StreamingResponseHandler streamingResponseHandler;

	public PlanCreator(List<DynamicAgent> agents, ILlmService llmService, PlanningToolInterface planningTool,
			PlanExecutionRecorder recorder, PromptService promptService, IManusProperties manusProperties,
			StreamingResponseHandler streamingResponseHandler) {
		this.agents = agents;
		this.llmService = llmService;
		this.planningTool = planningTool;
		this.recorder = recorder;
		this.promptService = promptService;
		this.manusProperties = manusProperties;
		this.streamingResponseHandler = streamingResponseHandler;
	}

	/**
	 * Create an execution plan based on the user request
	 * 
	 * @param context execution context, containing the user request and the
	 *                execution process information
	 * @return plan creation result
	 */
	public void createPlan(ExecutionContext context) {
		boolean useMemory = context.isUseMemory();
		String planId = context.getCurrentPlanId();
		if (planId == null || planId.isEmpty()) {
			throw new IllegalArgumentException("Plan ID cannot be null or empty");
		}
		try {
			// Build agent information
			String agentsInfo = buildAgentsInfo(agents);
			// Generate plan prompt
			String planPrompt = generatePlanPrompt(context.getUserRequest(), agentsInfo);

			PlanInterface executionPlan = null;
			String outputText = null;

			// Retry mechanism: up to 3 attempts until a valid execution plan is obtained
			int maxRetries = 3;
			for (int attempt = 1; attempt <= maxRetries; attempt++) {
				try {
					LoggerUtil.info("Attempting to create plan, attempt: {0}/{1}", attempt, maxRetries);

					// Use LLM to generate the plan
					PromptTemplate promptTemplate = new PromptTemplate(planPrompt);
					Prompt prompt = promptTemplate.create();

					ChatClientRequestSpec requestSpec = llmService.getPlanningChatClient().prompt(prompt)
							.toolCallbacks(List.of(planningTool.getFunctionToolCallback()));
					if (useMemory && attempt == 1) {
						requestSpec.advisors(memoryAdvisor -> memoryAdvisor.param(ChatMemory.CONVERSATION_ID,
								context.getMemoryId()));
						requestSpec.advisors(CustomMessageChatMemoryAdvisor
								.builder(llmService.getConversationMemory(manusProperties.getMaxMemory()),
										context.getUserRequest(), CustomMessageChatMemoryAdvisor.AdvisorType.BEFORE)
								.build());
					}

					// Use streaming response handler for plan creation
					Flux<ChatResponse> responseFlux = requestSpec.stream().chatResponse();
					String planCreationText = streamingResponseHandler.processStreamingTextResponse(responseFlux,
							"Plan creation", context.getCurrentPlanId());
					outputText = planCreationText;

					executionPlan = planningTool.getCurrentPlan();

					if (executionPlan != null) {
						// Set the user input part of the plan, for later storage and use.
						executionPlan.setUserRequest(context.getUserRequest());
						LoggerUtil.info("Plan created successfully on attempt {0}: {1}", attempt, executionPlan);
						break;
					} else {
						LoggerUtil.warn("Plan creation attempt {0} failed: planningTool.getCurrentPlan() returned null",
								attempt);
						if (attempt == maxRetries) {
							LoggerUtil.error("Failed to create plan after {0} attempts", maxRetries);
						}
					}
				} catch (Exception e) {
					LoggerUtil.warn("Exception during plan creation attempt {0}: {1}", attempt, e.getMessage());
					e.printStackTrace();
					if (attempt == maxRetries) {
						throw e;
					}
				}
			}

			PlanInterface currentPlan;
			// Check if plan was created successfully
			if (executionPlan != null) {
				currentPlan = planningTool.getCurrentPlan();
				currentPlan.setCurrentPlanId(planId);
				currentPlan.setRootPlanId(planId);
				currentPlan.setPlanningThinking(outputText);
			} else {
				throw new RuntimeException("Failed to create a valid execution plan after retries");
			}

			context.setPlan(currentPlan);

		} catch (Exception e) {
			LoggerUtil.error(e, "Error creating plan for request: {0}", context.getUserRequest());
			// Handle the exception
			throw new RuntimeException("Failed to create plan", e);
		}
	}

	/**
	 * Build the agent information string
	 * 
	 * @param agents agent list
	 * @return formatted agent information
	 */
	private String buildAgentsInfo(List<DynamicAgent> agents) {
		StringBuilder agentsInfo = new StringBuilder("Available Agents:\n");
		for (DynamicAgent agent : agents) {
			agentsInfo.append("- Agent Name: ").append(agent.getName()).append("\n  Description: ")
					.append(agent.getDescription()).append("\n");
		}
		return agentsInfo.toString();
	}

	/**
	 * Generate the plan prompt
	 * 
	 * @param request    user request
	 * @param agentsInfo agent information
	 * @return formatted prompt string
	 */
	private String generatePlanPrompt(String request, String agentsInfo) {
		// Escape special characters in request to prevent StringTemplate parsing errors
		String escapedRequest = escapeForStringTemplate(request);
		Map<String, Object> variables = Map.of("agentsInfo", agentsInfo, "request", escapedRequest);
		return promptService.renderPrompt(PromptEnum.PLANNING_PLAN_CREATION.getPromptName(), variables);
	}

	/**
	 * Escape special characters for StringTemplate engine
	 * 
	 * @param input input string
	 * @return escaped string
	 */
	private String escapeForStringTemplate(String input) {
		if (input == null) {
			return null;
		}
		// Escape characters that are special to StringTemplate
		// Note: Order matters - escape backslash first to avoid double-escaping
		return input.replace("\\", "\\\\").replace("$", "\\$").replace("<", "\\<").replace(">", "\\>")
				.replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]").replace("\"", "\\\"");
	}

}