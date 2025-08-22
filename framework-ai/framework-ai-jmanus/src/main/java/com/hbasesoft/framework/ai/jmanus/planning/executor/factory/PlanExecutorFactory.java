/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.planning.executor.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.jmanus.config.IManusProperties;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.AgentService;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.IDynamicAgentLoader;
import com.hbasesoft.framework.ai.jmanus.llm.ILlmService;
import com.hbasesoft.framework.ai.jmanus.planning.executor.DirectResponseExecutor;
import com.hbasesoft.framework.ai.jmanus.planning.executor.MapReducePlanExecutor;
import com.hbasesoft.framework.ai.jmanus.planning.executor.PlanExecutor;
import com.hbasesoft.framework.ai.jmanus.planning.executor.PlanExecutorInterface;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.PlanInterface;
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
 * @see com.hbasesoft.framework.ai.jmanus.planning.executor.factory <br>
 */

@Component
public class PlanExecutorFactory implements IPlanExecutorFactory {

	private final IDynamicAgentLoader dynamicAgentLoader;

	private final ILlmService llmService;

	private final AgentService agentService;

	private final PlanExecutionRecorder recorder;

	private final IManusProperties manusProperties;

	private final ObjectMapper objectMapper;

	public PlanExecutorFactory(IDynamicAgentLoader dynamicAgentLoader, ILlmService llmService,
			AgentService agentService, PlanExecutionRecorder recorder, IManusProperties manusProperties,
			ObjectMapper objectMapper) {
		this.dynamicAgentLoader = dynamicAgentLoader;
		this.llmService = llmService;
		this.agentService = agentService;
		this.recorder = recorder;
		this.manusProperties = manusProperties;
		this.objectMapper = objectMapper;
	}

	/**
	 * Create the appropriate executor based on plan type
	 * 
	 * @param plan The execution plan containing type information
	 * @return The appropriate PlanExecutorInterface implementation
	 * @throws IllegalArgumentException if plan type is not supported
	 */
	public PlanExecutorInterface createExecutor(PlanInterface plan) {
		if (plan == null) {
			throw new IllegalArgumentException("Plan cannot be null");
		}

		// Check if this is a direct response plan first
		if (plan.isDirectResponse()) {
			LoggerUtil.info("Creating direct response executor for plan: {0}", plan.getCurrentPlanId());
			return createDirectResponseExecutor();
		}

		String planType = plan.getPlanType();
		if (planType == null || planType.trim().isEmpty()) {
			LoggerUtil.warn("Plan type is null or empty, defaulting to simple executor for plan: {0}", plan.getCurrentPlanId());
			planType = "simple";
		}

		LoggerUtil.info("Creating executor for plan type: {0} (planId: {1})", planType, plan.getCurrentPlanId());

		return switch (planType.toLowerCase()) {
		case "simple" -> createSimpleExecutor();
		case "advanced" -> createAdvancedExecutor();
		default -> {
			LoggerUtil.warn("Unknown plan type: {0}, defaulting to simple executor", planType);
			yield createSimpleExecutor();
		}
		};
	}

	/**
	 * Create a simple plan executor for basic sequential execution
	 * 
	 * @return PlanExecutor instance for simple plans
	 */
	private PlanExecutorInterface createSimpleExecutor() {
		LoggerUtil.debug("Creating simple plan executor");
		List<DynamicAgent> agents = dynamicAgentLoader.getAllAgents();
		return new PlanExecutor(agents, recorder, agentService, llmService, manusProperties);
	}

	/**
	 * Create a direct response executor for handling direct response plans
	 * 
	 * @return DirectResponseExecutor instance for direct response plans
	 */
	private PlanExecutorInterface createDirectResponseExecutor() {
		LoggerUtil.debug("Creating direct response executor");
		List<DynamicAgent> agents = dynamicAgentLoader.getAllAgents();
		return new DirectResponseExecutor(agents, recorder, agentService, llmService, manusProperties);
	}

	/**
	 * Create an advanced plan executor for MapReduce execution
	 * 
	 * @return MapReducePlanExecutor instance for advanced plans
	 */
	private PlanExecutorInterface createAdvancedExecutor() {
		LoggerUtil.debug("Creating advanced MapReduce plan executor");
		List<DynamicAgent> agents = dynamicAgentLoader.getAllAgents();
		return new MapReducePlanExecutor(agents, recorder, agentService, llmService, manusProperties, objectMapper);
	}

	/**
	 * Get supported plan types
	 * 
	 * @return Array of supported plan type strings
	 */
	public String[] getSupportedPlanTypes() {
		return new String[] { "simple", "advanced", "direct" };
	}

	/**
	 * Check if a plan type is supported
	 * 
	 * @param planType The plan type to check
	 * @return true if the plan type is supported, false otherwise
	 */
	public boolean isPlanTypeSupported(String planType) {
		if (planType == null) {
			return false;
		}
		String normalizedType = planType.toLowerCase();
		return "simple".equals(normalizedType) || "advanced".equals(normalizedType) || "direct".equals(normalizedType);
	}

	/**
	 * Create executor with explicit plan type (useful for testing or special cases)
	 * 
	 * @param planType The explicit plan type to use
	 * @param planId   Plan ID for logging purposes
	 * @return The appropriate PlanExecutorInterface implementation
	 */
	public PlanExecutorInterface createExecutorByType(String planType, String planId) {
		LoggerUtil.info("Creating executor for explicit plan type: {0} (planId: {1})", planType, planId);

		if (planType == null || planType.trim().isEmpty()) {
			planType = "simple";
		}

		return switch (planType.toLowerCase()) {
		case "simple" -> createSimpleExecutor();
		case "advanced" -> createAdvancedExecutor();
		case "direct" -> createDirectResponseExecutor();
		default -> {
			LoggerUtil.warn("Unknown explicit plan type: {0}, defaulting to simple executor", planType);
			yield createSimpleExecutor();
		}
		};
	}

}
