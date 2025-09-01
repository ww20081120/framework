/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.file.agent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbasesoft.framework.ai.agent.dynamic.agent.service.AbstractAgentService;
import com.hbasesoft.framework.ai.agent.dynamic.agent.service.IDynamicAgentLoader;
import com.hbasesoft.framework.ai.agent.dynamic.agent.vo.AgentConfig;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.service.IMcpService;
import com.hbasesoft.framework.ai.agent.dynamic.model.service.DynamicModelService;
import com.hbasesoft.framework.ai.agent.planning.IPlanningFactory;
import com.hbasesoft.framework.ai.agent.tool.Action;
import com.hbasesoft.framework.ai.agent.tool.AnnotatedMethodToolAdapter;
import com.hbasesoft.framework.ai.agent.tool.AnnotatedToolRegistry;
import com.hbasesoft.framework.common.utils.ContextHolder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.file.agent <br>
 */
@Service
public class AgentServiceImpl extends AbstractAgentService  {

	/** 缓存Agent配置，确保ID一致性 */
	private static final Map<String, AgentConfig> AGENT_CONFIG_CACHE = new ConcurrentHashMap<>();

	/**
	 * @param planningFactory
	 * @param dynamicAgentLoader
	 * @param mcpService
	 */
	public AgentServiceImpl(IPlanningFactory planningFactory, IDynamicAgentLoader dynamicAgentLoader,
			IMcpService mcpService) {
		super(planningFactory, dynamicAgentLoader, mcpService);
	}

	/**
	 * Description: 更新Agent配置 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param agentConfig
	 * @return <br>
	 */
	@Override
	public AgentConfig updateAgent(AgentConfig agentConfig) {
		if (agentConfig != null) {
			// 生成或保持ID一致性
			if (agentConfig.getId() == null || agentConfig.getId().isEmpty()) {
				agentConfig.setId(generateConsistentId(agentConfig.getName()));
			}
			AGENT_CONFIG_CACHE.put(getAgentKey(agentConfig.getNamespace(), agentConfig.getName()), agentConfig);
		}
		return agentConfig;
	}

	/**
	 * Description: 根据名称获取Agent配置 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param namespace
	 * @param agentName
	 * @return <br>
	 */
	@Override
	public AgentConfig getAgentByName(String namespace, String agentName) {
		String key = getAgentKey(namespace, agentName);

		// 从缓存中获取
		return AGENT_CONFIG_CACHE.get(key);
	}

	/**
	 * Description: 获取指定命名空间下的所有Agent配置 <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param namespace
	 * @return <br>
	 */
	@Override
	public List<AgentConfig> getAllAgentsByNamespace(String namespace) {
		// 直接从缓存中获取指定命名空间下的所有Agent配置
		return AGENT_CONFIG_CACHE.values().stream().filter(config -> namespace.equals(config.getNamespace()))
				.collect(Collectors.toList());
	}

	/**
	 * Description: 在系统启动时初始化所有Agent配置<br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param context <br>
	 */
	void init(ApplicationContext context) {
		// 清空缓存
		AGENT_CONFIG_CACHE.clear();

		// 从Spring容器中获取所有带有@Agent注解的Bean
		Map<String, Object> agentBeans = context.getBeansWithAnnotation(Agent.class);
		// 遍历所有Bean，创建对应的AgentConfig对象并缓存
		for (Map.Entry<String, Object> entry : agentBeans.entrySet()) {
			Object bean = entry.getValue();
			Agent agentAnnotation = bean.getClass().getAnnotation(Agent.class);
			if (agentAnnotation != null) {
				String beanName = getBeanName(bean.getClass(), agentAnnotation);
				String namespace = agentAnnotation.namespace();
				String key = getAgentKey(namespace, beanName);

				// 创建AgentConfig对象并缓存
				AgentConfig config = createAgentConfigFromAnnotation(agentAnnotation, beanName, bean);
				AGENT_CONFIG_CACHE.put(key, config);

				// 扫描Bean中的@Action注解方法并注册为工具
				registerAnnotatedMethodsAsTools(bean, context, config);
			}
		}
	}

	private AgentConfig createAgentConfigFromAnnotation(Agent agentAnnotation, String beanName, Object beanInstance) {
		AgentConfig config = new AgentConfig();
		config.setId(generateConsistentId(beanName));
		config.setName(beanName);
		config.setDescription(agentAnnotation.description());
		config.setNamespace(agentAnnotation.namespace());
		config.setBuiltIn(agentAnnotation.builtIn());
		config.setIsBuiltIn(agentAnnotation.isBuiltIn());
		config.setClassName(agentAnnotation.agent().getName());
		config.setSystemPrompt(agentAnnotation.systemPrompt());
		config.setNextStepPrompt(agentAnnotation.nextStepPrompt());
		List<String> availableTools = new ArrayList<String>();
		if (ArrayUtils.isNotEmpty(agentAnnotation.acions())) {
			for (String action : agentAnnotation.acions()) {
				if (!availableTools.contains(action)) {
					availableTools.add(action);
				}
			}
		}

		DynamicModelService dynamicModelService = ContextHolder.getContext().getBean(DynamicModelService.class);
		if (StringUtils.isNotEmpty(agentAnnotation.model())) {
			dynamicModelService.queryAll().stream()
					.filter(model -> model.getModelName().equals(agentAnnotation.model())).findFirst()
					.ifPresent(model -> config.setModel(model));
		}
		if (config.getModel() == null) {
			config.setModel(dynamicModelService.getDefault());
		}

		// 如果Bean实现了AgentConfigProvider接口，则从接口方法获取配置信息
		if (beanInstance instanceof AgentConfigProvider) {
			AgentConfigProvider provider = (AgentConfigProvider) beanInstance;
			config.setSystemPrompt(provider.getSystemPrompt());
			config.setNextStepPrompt(provider.getNextStepPrompt());
			if (ArrayUtils.isNotEmpty(provider.getActions())) {
				for (String action : provider.getActions()) {
					if (!availableTools.contains(action)) {
						availableTools.add(action);
					}
				}
			}
		}

		config.setAvailableTools(availableTools);

		return config;
	}

	/**
	 * 生成一致性的ID，确保每次项目启动时如果agent名称不变，ID保持不变
	 * 
	 * @param agentName
	 * @return
	 */
	private String generateConsistentId(String agentName) {
		// 使用简单的哈希算法生成一致的ID
		// 在实际应用中，可能需要更复杂的算法来确保唯一性和一致性
		return "agent_" + agentName.hashCode();
	}

	/**
	 * 获取Agent的缓存键
	 * 
	 * @param namespace
	 * @param agentName
	 * @return
	 */
	private String getAgentKey(String namespace, String agentName) {
		return namespace + ":" + agentName;
	}

	/**
	 * 获取Bean名称
	 * 
	 * @param beanClass
	 * @param agentAnnotation
	 * @return
	 */
	private String getBeanName(Class<?> beanClass, Agent agentAnnotation) {
		// 如果注解中指定了名称，则使用注解中的名称
		if (agentAnnotation.name() != null && !agentAnnotation.name().isEmpty()) {
			return agentAnnotation.name();
		}
		// 否则使用类名作为名称
		return beanClass.getSimpleName();
	}

	/**
	 * 扫描Bean中的@Action注解方法并注册为工具
	 * 
	 * @param bean
	 * @param context
	 * @param config
	 */
	private void registerAnnotatedMethodsAsTools(Object bean, ApplicationContext context, AgentConfig config) {
		// 获取Bean的所有方法
		Method[] methods = bean.getClass().getDeclaredMethods();

		// 获取ObjectMapper实例
		ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

		// 获取AnnotatedToolRegistry实例
		AnnotatedToolRegistry toolRegistry = context.getBean(AnnotatedToolRegistry.class);

		// 遍历所有方法，查找带有@Action注解的方法
		for (Method method : methods) {
			Action actionAnnotation = method.getAnnotation(Action.class);
			if (actionAnnotation != null) {
				// 创建适配器并注册为工具
				AnnotatedMethodToolAdapter toolAdapter = new AnnotatedMethodToolAdapter(bean, method, actionAnnotation,
						objectMapper);

				// 将工具适配器注册到工具注册表中
				toolRegistry.registerTool(toolAdapter);

				if (!config.getAvailableTools().contains(toolAdapter.getName())) {
					config.getAvailableTools().add(toolAdapter.getName());
				}
			}
		}
	}
}
