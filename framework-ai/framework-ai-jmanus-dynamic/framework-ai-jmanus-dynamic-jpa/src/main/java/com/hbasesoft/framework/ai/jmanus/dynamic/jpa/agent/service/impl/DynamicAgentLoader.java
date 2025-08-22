/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.agent.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.jmanus.config.ManusProperties;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.IDynamicAgentLoader;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.agent.dao.DynamicAgentDao;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.agent.po.DynamicAgentPo4Jpa;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.model.po.DynamicModelPo4Jpa;
import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.prompt.service.PromptService;
import com.hbasesoft.framework.ai.jmanus.llm.ILlmService;
import com.hbasesoft.framework.ai.jmanus.llm.StreamingResponseHandler;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.jmanus.planning.service.IUserInputService;
import com.hbasesoft.framework.ai.jmanus.recorder.PlanExecutionRecorder;

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

	private final DynamicAgentDao repository;

	private final ILlmService llmService;

	private final PlanExecutionRecorder recorder;

	private final ManusProperties properties;

	private final ToolCallingManager toolCallingManager;

	private final IUserInputService userInputService;

	private final PromptService promptService;

	private final StreamingResponseHandler streamingResponseHandler;

	@Value("${namespace.value: default}")
	private String namespace;

	public DynamicAgentLoader(DynamicAgentDao repository, @Lazy ILlmService llmService, PlanExecutionRecorder recorder,
			ManusProperties properties, @Lazy ToolCallingManager toolCallingManager, IUserInputService userInputService,
			PromptService promptService, StreamingResponseHandler streamingResponseHandler) {
		this.repository = repository;
		this.llmService = llmService;
		this.recorder = recorder;
		this.properties = properties;
		this.toolCallingManager = toolCallingManager;
		this.userInputService = userInputService;
		this.promptService = promptService;
		this.streamingResponseHandler = streamingResponseHandler;
	}

	public DynamicAgent loadAgent(String agentName, Map<String, Object> initialAgentSetting) {
		DynamicAgentPo4Jpa entity = repository.getByLambda(
				q -> q.eq(DynamicAgentPo4Jpa::getNamespace, namespace).eq(DynamicAgentPo4Jpa::getAgentName, agentName));
		if (entity == null) {
			throw new IllegalArgumentException("Agent not found: " + agentName);
		}
		return convert(entity, initialAgentSetting);
	}

	private DynamicAgent convert(DynamicAgentPo4Jpa entity, Map<String, Object> initialAgentSetting) {
		DynamicModelPo4Jpa model = entity.getModel();
		ModelConfig config = new ModelConfig();
		if (model != null) {
			BeanUtils.copyProperties(model, config);
		}

		return new DynamicAgent(llmService, recorder, properties, entity.getAgentName(), entity.getAgentDescription(),
				entity.getNextStepPrompt(), entity.getAvailableToolKeys(), toolCallingManager, initialAgentSetting,
				userInputService, promptService, config, streamingResponseHandler);
	}

	public List<DynamicAgent> getAllAgents() {
		return repository.queryByLambda(q -> q.eq(DynamicAgentPo4Jpa::getNamespace, namespace)).stream()
				.filter(entity -> Objects.equals(entity.getNamespace(), namespace)).map(t -> {
					return convert(t, null);
				}).toList();
	}

	@Override
	public List<DynamicAgent> getAgents(ExecutionContext context) {
		return IDynamicAgentLoader.super.getAgents(context);
	}

}
