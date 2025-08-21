/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.jmanus.agent.BaseAgent;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.ToolCallbackProvider;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.dao.DynamicAgentDao;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.po.DynamicAgentPo;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.vo.AgentConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.vo.Tool;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.AgentService;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.IDynamicAgentLoader;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.IMcpService;
import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.po.DynamicModelPo;
import com.hbasesoft.framework.ai.jmanus.dynamic.model.model.vo.ModelConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.model.vo.NamespaceConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.namespace.service.NamespaceService;
import com.hbasesoft.framework.ai.jmanus.llm.ILlmService;
import com.hbasesoft.framework.ai.jmanus.planning.IPlanningFactory;
import com.hbasesoft.framework.ai.jmanus.planning.IPlanningFactory.ToolCallBackContext;
import com.hbasesoft.framework.ai.jmanus.tool.terminate.TerminateTool;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.impl <br>
 */

@Service
public class AgentServiceImpl implements AgentService {

	private final IDynamicAgentLoader dynamicAgentLoader;

	private final DynamicAgentDao repository;

	private final IPlanningFactory planningFactory;

	private final IMcpService mcpService;

	private final NamespaceService namespaceService;

	@Autowired
	@Lazy
	private ILlmService llmService;

	@Autowired
	@Lazy
	private ToolCallingManager toolCallingManager;

	@Autowired
	public AgentServiceImpl(@Lazy IDynamicAgentLoader dynamicAgentLoader, DynamicAgentDao repository,
			@Lazy IPlanningFactory planningFactory, @Lazy IMcpService mcpService, NamespaceService namespaceService) {
		this.dynamicAgentLoader = dynamicAgentLoader;
		this.repository = repository;
		this.planningFactory = planningFactory;
		this.mcpService = mcpService;
		this.namespaceService = namespaceService;
	}

	@Override
	public List<AgentConfig> getAllAgentsByNamespace(String namespace) {
		List<DynamicAgentPo> entities;
		if (namespace == null || namespace.trim().isEmpty()) {
			// If namespace is null or empty, use default namespace
			namespace = "default";
			LoggerUtil.info("Namespace not specified, using default namespace: {0}", namespace);
		}
		String ns = namespace.trim();
		entities = repository.queryByLambda(q -> q.eq(DynamicAgentPo::getNamespace, ns));
		return entities.stream().map(this::mapToAgentConfig).collect(Collectors.toList());
	}

	@Override
	public AgentConfig getAgentById(String id) {
		DynamicAgentPo entity = repository.get(Long.parseLong(id));
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "agent");
		return mapToAgentConfig(entity);
	}

	@Override
	public AgentConfig createAgent(AgentConfig config) {
		try {
			// Set default namespace if namespace is null or empty
			if (config.getNamespace() == null || config.getNamespace().trim().isEmpty()) {
				String defaultNamespace = getDefaultNamespace();
				config.setNamespace(defaultNamespace);
				LoggerUtil.info("Namespace not specified for Agent: {0}, using default namespace: {1}",
						config.getName(), defaultNamespace);
			}

			// Check if an Agent with the same name already exists
			DynamicAgentPo existingAgent = repository
					.getByLambda(q -> q.eq(DynamicAgentPo::getAgentName, config.getName()));
			if (existingAgent != null) {
				LoggerUtil.info("Found Agent with same name: {0}, updating Agent", config.getName());
				config.setId(existingAgent.getId().toString());
				return updateAgent(config);
			}

			DynamicAgentPo entity = new DynamicAgentPo();
			entity = mergePrompts(entity, config.getName());
			updateEntityFromConfig(entity, config);
			repository.save(entity);
			LoggerUtil.info("Successfully created new Agent: {0}", config.getName());
			return mapToAgentConfig(entity);
		} catch (Exception e) {
			LoggerUtil.warn("Exception occurred during Agent creation: {0}, error message: {1}", config.getName(),
					e.getMessage());
			// If it's a uniqueness constraint violation exception, try returning the
			// existing Agent
			if (e.getMessage() != null && e.getMessage().contains("Unique")) {
				DynamicAgentPo existingAgent = repository
						.getByLambda(q -> q.eq(DynamicAgentPo::getAgentName, config.getName()));
				if (existingAgent != null) {
					LoggerUtil.info("Return existing Agent: {0}", config.getName());
					return mapToAgentConfig(existingAgent);
				}
			}
			throw e;
		}
	}

	@Override
	public AgentConfig updateAgent(AgentConfig config) {
		DynamicAgentPo entity = repository.get(config.getId());
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "agent");
		updateEntityFromConfig(entity, config);
		repository.save(entity);
		return mapToAgentConfig(entity);
	}

	@Override
	public void deleteAgent(String id) {
		DynamicAgentPo entity = repository.get(id);
		Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, "agent");

		// Protect built-in agents from deletion
		if (Boolean.TRUE.equals(entity.getBuiltIn())) {
			throw new IllegalArgumentException("Cannot delete built-in Agent: " + entity.getAgentName());
		}

		repository.deleteById(Long.parseLong(id));
	}

	public List<Tool> getAvailableTools() {

		String uuid = UUID.randomUUID().toString();
		String expectedReturnInfo = "dummyColumn1, dummyColumn2";
		try {
			Map<String, ToolCallBackContext> toolcallContext = planningFactory.toolCallbackMap(uuid, uuid,
					expectedReturnInfo);
			return toolcallContext.entrySet().stream().map(entry -> {
				Tool tool = new Tool();
				tool.setKey(entry.getKey());
				tool.setName(entry.getKey()); // You might want to provide a more friendly
				// name
				tool.setDescription(entry.getValue().getFunctionInstance().getDescription());
				tool.setEnabled(true);
				tool.setServiceGroup(entry.getValue().getFunctionInstance().getServiceGroup());
				return tool;
			}).collect(Collectors.toList());
		} finally {
			mcpService.close(uuid);
		}
	}

	private AgentConfig mapToAgentConfig(DynamicAgentPo entity) {
		AgentConfig config = new AgentConfig();
		entity = mergePrompts(entity, entity.getAgentName());
		config.setId(entity.getId().toString());
		config.setName(entity.getAgentName());
		config.setDescription(entity.getAgentDescription());
		config.setSystemPrompt(entity.getSystemPrompt());
		config.setNextStepPrompt(entity.getNextStepPrompt());
		config.setAvailableTools(entity.getAvailableToolKeys());
		config.setClassName(entity.getClassName());
		config.setNamespace(entity.getNamespace());
		config.setBuiltIn(entity.getBuiltIn());
		DynamicModelPo model = entity.getModel();
		config.setModel(model == null ? null : model.mapToModelConfig());
		return config;
	}

	/**
	 * Get default namespace code when no namespace is specified. Uses the first
	 * available namespace from getAllNamespaces(), or "default" if no namespaces
	 * exist.
	 * 
	 * @return default namespace code
	 */
	private String getDefaultNamespace() {
		try {
			List<NamespaceConfig> namespaces = namespaceService.getAllNamespaces();
			if (!namespaces.isEmpty()) {
				// Find the namespace with code "default" first
				for (NamespaceConfig namespace : namespaces) {
					if ("default".equals(namespace.getCode())) {
						LoggerUtil.debug("Found default namespace with code: {0}", namespace.getCode());
						return namespace.getCode();
					}
				}
				// If no "default" code namespace found, use the first one
				String firstNamespaceCode = namespaces.get(0).getCode();
				LoggerUtil.debug("Using first namespace as default: {0}", firstNamespaceCode);
				return firstNamespaceCode;
			} else {
				// If no namespaces exist, return "default"
				LoggerUtil.warn("No namespaces found, using fallback default namespace code: default");
				return "default";
			}
		} catch (Exception e) {
			LoggerUtil.error("Error getting default namespace, using fallback: {0}", e.getMessage());
			return "default";
		}
	}

	private void updateEntityFromConfig(DynamicAgentPo entity, AgentConfig config) {
		// Set default namespace if namespace is null or empty
		if (config.getNamespace() == null || config.getNamespace().trim().isEmpty()) {
			String defaultNamespace = getDefaultNamespace();
			config.setNamespace(defaultNamespace);
			LoggerUtil.info("Namespace not specified for Agent: {0}, using default namespace: {1}", config.getName(),
					defaultNamespace);
		}

		entity.setAgentName(config.getName());
		entity.setAgentDescription(config.getDescription());
		String nextStepPrompt = config.getNextStepPrompt();
		entity = mergePrompts(entity, config.getName());
		entity.setNextStepPrompt(nextStepPrompt);

		// 1. Create new collection to ensure uniqueness and order
		java.util.Set<String> toolSet = new java.util.LinkedHashSet<>();
		List<String> availableTools = config.getAvailableTools();
		if (availableTools != null) {
			toolSet.addAll(availableTools);
		}
		// 2. Add TerminateTool (if not exists)
		if (!toolSet.contains(TerminateTool.name)) {
			LoggerUtil.info("Adding necessary tool for Agent[{0}]: {1}", config.getName(), TerminateTool.name);
			toolSet.add(TerminateTool.name);
		}
		// 3. Convert to List and set
		entity.setAvailableToolKeys(new java.util.ArrayList<>(toolSet));
		entity.setClassName(config.getName());
		ModelConfig model = config.getModel();
		if (model != null) {
			entity.setModel(new DynamicModelPo(model.getId()));
		}

		// 4. Set the user-selected namespace
		entity.setNamespace(config.getNamespace());

		// 5. Set builtIn if provided (only allow setting to false for existing built-in
		// agents)
		if (config.getBuiltIn() != null) {
			entity.setBuiltIn(config.getBuiltIn());
		}
	}

	private DynamicAgentPo mergePrompts(DynamicAgentPo entity, String agentName) {
		// The SystemPrompt property here is deprecated, use nextStepPrompt directly
		if (StringUtils.isNotBlank(entity.getSystemPrompt())) {
			String systemPrompt = entity.getSystemPrompt();
			String nextPrompt = entity.getNextStepPrompt();
			// The SystemPrompt property here is deprecated, use nextStepPrompt directly
			if (nextPrompt != null && !nextPrompt.trim().isEmpty()) {
				nextPrompt = systemPrompt + "\n" + nextPrompt;
			}
			LoggerUtil.warn(
					"Agent[{0}] SystemPrompt is not empty, but the property is deprecated, only keep nextPrompt. This time merge the agent content. If you need this content to take effect in prompt, please directly update the unique prompt in the interface. Current specified value: {1}",
					agentName, nextPrompt);
			entity.setSystemPrompt(" ");
		}
		return entity;
	}

	@Override
	public BaseAgent createDynamicBaseAgent(String name, String planId, String rootPlanId,
			Map<String, Object> initialAgentSetting, String expectedReturnInfo) {

		LoggerUtil.info("Create new BaseAgent: {0}, planId: {1}", name, planId);

		try {
			// Load existing Agent through dynamicAgentLoader
			DynamicAgent agent = dynamicAgentLoader.loadAgent(name, initialAgentSetting);

			// Set planId
			agent.setCurrentPlanId(planId);
			agent.setRootPlanId(rootPlanId);
			// Set tool callback mapping
			Map<String, ToolCallBackContext> toolCallbackMap = planningFactory.toolCallbackMap(planId, rootPlanId,
					expectedReturnInfo);
			agent.setToolCallbackProvider(new ToolCallbackProvider() {

				@Override
				public Map<String, ToolCallBackContext> getToolCallBackContext() {
					return toolCallbackMap;
				}
			});
			return agent;
		} catch (Exception e) {
			throw new RuntimeException("Failed to create dynamic base agent: " + name, e);
		}
	}

}
