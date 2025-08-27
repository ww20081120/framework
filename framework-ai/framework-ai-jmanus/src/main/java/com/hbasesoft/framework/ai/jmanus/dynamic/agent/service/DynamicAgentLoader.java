/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.jmanus.config.ManusProperties;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.vo.AgentConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.jmanus.llm.ILlmService;
import com.hbasesoft.framework.ai.jmanus.llm.StreamingResponseHandler;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.jmanus.planning.service.IUserInputService;
import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;
import com.hbasesoft.framework.common.utils.ContextHolder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.impl <br>
 */
@Service
public class DynamicAgentLoader implements IDynamicAgentLoader {

	private final ILlmService llmService;

	private final PlanExecutionRecorder recorder;

	private final ManusProperties properties;

	private final ToolCallingManager toolCallingManager;

	private final IUserInputService userInputService;

	private final PromptService promptService;

	private final StreamingResponseHandler streamingResponseHandler;

	private AgentService agentService;

	@Value("${namespace.value:default}")
	private String namespace;

	public DynamicAgentLoader(@Lazy ILlmService llmService, PlanExecutionRecorder recorder, ManusProperties properties,
			@Lazy ToolCallingManager toolCallingManager, IUserInputService userInputService,
			PromptService promptService, StreamingResponseHandler streamingResponseHandler) {
		// this.agentService = agentService;
		this.llmService = llmService;
		this.recorder = recorder;
		this.properties = properties;
		this.toolCallingManager = toolCallingManager;
		this.userInputService = userInputService;
		this.promptService = promptService;
		this.streamingResponseHandler = streamingResponseHandler;
	}

	public DynamicAgent loadAgent(String agentName, Map<String, Object> initialAgentSetting) {
		AgentConfig agentConfig = getAgentService().getAgentByName(agentName, agentName);
		return convert(agentConfig, initialAgentSetting);
	}

	private DynamicAgent convert(AgentConfig agentConfig, Map<String, Object> initialAgentSetting) {
		ModelConfig config = agentConfig.getModel();

		return new DynamicAgent(llmService, recorder, properties, agentConfig.getName(), agentConfig.getDescription(),
				agentConfig.getNextStepPrompt(), agentConfig.getAvailableTools(), toolCallingManager,
				initialAgentSetting, userInputService, promptService, config, streamingResponseHandler);
	}

	public List<DynamicAgent> getAllAgents() {
		return getAgentService().getAllAgentsByNamespace(namespace).stream().map(t -> {
			return convert(t, new java.util.HashMap<String, Object>());
		}).toList();
	}

	@Override
	public List<DynamicAgent> getAgents(ExecutionContext context) {
		return IDynamicAgentLoader.super.getAgents(context);
	}
	
	private AgentService getAgentService() {
		if (agentService == null) {
			agentService = ContextHolder.getContext().getBean(AgentService.class);
		}
		return agentService;
	}

}
