/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.planning.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hbasesoft.framework.ai.jmanus.agent.AgentState;
import com.hbasesoft.framework.ai.jmanus.agent.BaseAgent;
import com.hbasesoft.framework.ai.jmanus.config.IManusProperties;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.po.DynamicAgentPo;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.AgentService;
import com.hbasesoft.framework.ai.jmanus.llm.ILlmService;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.planning.executor <br>
 */

public abstract class AbstractPlanExecutor implements PlanExecutorInterface {

	protected final PlanExecutionRecorder recorder;

	// Pattern to match square brackets at the beginning of a string, supports
	// Chinese and
	// other characters
	protected final Pattern pattern = Pattern.compile("^\\s*\\[([^\\]]+)\\]");

	protected final List<DynamicAgentPo> agents;

	protected final AgentService agentService;

	protected ILlmService llmService;

	protected final IManusProperties manusProperties;

	// Define static final strings for the keys used in executorParams
	public static final String PLAN_STATUS_KEY = "planStatus";

	public static final String CURRENT_STEP_INDEX_KEY = "currentStepIndex";

	public static final String STEP_TEXT_KEY = "stepText";

	public static final String EXTRA_PARAMS_KEY = "extraParams";

	public static final String EXECUTION_ENV_STRING_KEY = "current_step_env_data";

	public AbstractPlanExecutor(List<DynamicAgentPo> agents, PlanExecutionRecorder recorder,
			AgentService agentService, ILlmService llmService, IManusProperties manusProperties) {
		this.agents = agents;
		this.recorder = recorder;
		this.agentService = agentService;
		this.llmService = llmService;
		this.manusProperties = manusProperties;
	}

	/**
	 * General logic for executing a single step.
	 * 
	 * @param step    The execution step
	 * @param context The execution context
	 * @return The step executor
	 */
	protected BaseAgent executeStep(ExecutionStep step, ExecutionContext context) {
		try {
			String stepType = getStepFromStepReq(step.getStepRequirement());
			int stepIndex = step.getStepIndex();
			String expectedReturnInfo = step.getTerminateColumns();

			String planStatus = context.getPlan().getPlanExecutionStateStringFormat(true);
			String stepText = step.getStepRequirement();

			Map<String, Object> initSettings = new HashMap<>();
			initSettings.put(PLAN_STATUS_KEY, planStatus);
			initSettings.put(CURRENT_STEP_INDEX_KEY, String.valueOf(stepIndex));
			initSettings.put(STEP_TEXT_KEY, stepText);
			initSettings.put(EXTRA_PARAMS_KEY, context.getPlan().getExecutionParams());

			BaseAgent executor = getExecutorForStep(stepType, context, initSettings, expectedReturnInfo);
			if (executor == null) {
				LoggerUtil.error("No executor found for step type: {0}", stepType);
				step.setResult("No executor found for step type: " + stepType);
				return null;
			}

			step.setAgent(executor);
			executor.setState(AgentState.IN_PROGRESS);

			recorder.recordStepStart(step, context);
			String stepResultStr = executor.run();
			step.setResult(stepResultStr);

			return executor;
		} catch (Exception e) {
			LoggerUtil.error("Error executing step: {0}", step.getStepRequirement(), e);
			step.setResult("Execution failed: " + e.getMessage());
		} finally {
			recorder.recordStepEnd(step, context);
		}
		return null;
	}

	/**
	 * Extract the step type from the step requirement string.
	 */
	protected String getStepFromStepReq(String stepRequirement) {
		Matcher matcher = pattern.matcher(stepRequirement);
		if (matcher.find()) {
			return matcher.group(1).trim().toLowerCase();
		}
		return "DEFAULT_AGENT";
	}

	/**
	 * Get the executor for the step.
	 */
	protected BaseAgent getExecutorForStep(String stepType, ExecutionContext context, Map<String, Object> initSettings,
			String expectedReturnInfo) {
		for (DynamicAgentPo agent : agents) {
			if (agent.getAgentName().equalsIgnoreCase(stepType)) {
				BaseAgent executor = agentService.createDynamicBaseAgent(agent.getAgentName(),
						context.getPlan().getCurrentPlanId(), context.getPlan().getRootPlanId(), initSettings,
						expectedReturnInfo);
				// Set thinkActRecordId from context for sub-plan executions
				if (context.getThinkActRecordId() != null) {
					executor.setThinkActRecordId(context.getThinkActRecordId());
				}
				return executor;
			}
		}
		throw new IllegalArgumentException(
				"No Agent Executor found for step type, check your agents list : " + stepType);
	}

	protected PlanExecutionRecorder getRecorder() {
		return recorder;
	}

	/**
	 * Parse columns string by splitting with comma or Chinese comma.
	 * 
	 * @param columnsInString the columns string to parse
	 * @return list of column names
	 */
	protected List<String> parseColumns(String columnsInString) {
		List<String> columns = new ArrayList<>();
		if (columnsInString == null || columnsInString.trim().isEmpty()) {
			return columns;
		}

		// Split by comma (,) or Chinese comma (，)
		String[] parts = columnsInString.split("[,，]");
		for (String part : parts) {
			String trimmed = part.trim();
			if (!trimmed.isEmpty()) {
				columns.add(trimmed);
			}
		}

		return columns;
	}

	/**
	 * Cleanup work after execution is completed.
	 */
	protected void performCleanup(ExecutionContext context, BaseAgent lastExecutor) {
		String planId = context.getCurrentPlanId();
		llmService.clearAgentMemory(planId);
		if (lastExecutor != null) {
			lastExecutor.clearUp(planId);
		}
	}

}
