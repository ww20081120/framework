/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.DynamicAgentLoader;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.McpService;
import com.hbasesoft.framework.ai.jmanus.planning.service.PlanningFactory;
import com.hbasesoft.framework.ai.jmanus.planning.service.PlanningFactory.ToolCallBackContext;
import com.hbasesoft.framework.ai.jmanus.tool.TerminateTool;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
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

    private static final String DEFAULT_AGENT_NAME = "DEFAULT_AGENT";

    private final DynamicAgentLoader dynamicAgentLoader;

    private final DynamicAgentDao dynamicAgentDao;

    private final PlanningFactory planningFactory;

    private final McpService mcpService;

    @Autowired
    public AgentServiceImpl(@Lazy DynamicAgentLoader dynamicAgentLoader, DynamicAgentDao dynamicAgentDao,
        @Lazy PlanningFactory planningFactory, @Lazy McpService mcpService) {
        this.dynamicAgentLoader = dynamicAgentLoader;
        this.dynamicAgentDao = dynamicAgentDao;
        this.planningFactory = planningFactory;
        this.mcpService = mcpService;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public List<AgentConfig> getAllAgents() {
        return dynamicAgentDao.queryAll().stream().map(this::mapToAgentConfig).collect(Collectors.toList());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Override
    public AgentConfig getAgentById(String id) {
        DynamicAgentPo entity = dynamicAgentDao.get(Long.parseLong(id));
        Assert.notNull(entity, ErrorCodeDef.PARAM_NOT_NULL, id);
        return mapToAgentConfig(entity);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param agentConfig
     * @return <br>
     */
    @Override
    public AgentConfig createAgent(AgentConfig agentConfig) {
        try {
            DynamicAgentPo existingAgent = dynamicAgentDao
                .getByLambda(q -> q.eq(DynamicAgentPo::getAgentName, agentConfig.getName()));
            if (existingAgent != null) {
                LoggerUtil.info("找到名称一样的Agent：{0}, 做更新!", agentConfig.getName());
                agentConfig.setId(existingAgent.getId().toString());
                return updateAgent(agentConfig);
            }

            DynamicAgentPo po = new DynamicAgentPo();
            updateEntityFromConfig(po, agentConfig);
            dynamicAgentDao.save(po);
            LoggerUtil.info("成功创建一个新的Agent：{0}", agentConfig.getName());
            return mapToAgentConfig(po);
        }
        catch (Exception e) {
            LoggerUtil.error(e);
            // 如果是唯一性约束冲突异常，尝试返回现有代理
            if (e.getMessage() != null && e.getMessage().contains("Unique")) {
                DynamicAgentPo existingAgent = dynamicAgentDao
                    .getByLambda(q -> q.eq(DynamicAgentPo::getAgentName, agentConfig.getName()));
                if (existingAgent != null) {
                    LoggerUtil.info("返回现有代理: {0}", agentConfig.getName());
                    return mapToAgentConfig(existingAgent);
                }
            }
            throw e;
        }
    }

    private void updateEntityFromConfig(DynamicAgentPo po, AgentConfig agentConfig) {
        po.setAgentName(agentConfig.getName());
        po.setAgentDescription(agentConfig.getDescription());
        po.setNextStepPrompt(agentConfig.getNextStepPrompt());

        // 1. 创建新集合以确保工具列表的唯一性和顺序
        java.util.Set<String> toolSet = new java.util.LinkedHashSet<>();
        List<String> availableTools = agentConfig.getAvailableTools();
        if (availableTools != null) {
            toolSet.addAll(availableTools);
        }

        // 2. 添加终止工具（若集合中不存在）
        if (!toolSet.contains(TerminateTool.NAME)) {
            LoggerUtil.info("为代理[{0}]添加必要工具: {1}", agentConfig.getName(), TerminateTool.NAME);
            toolSet.add(TerminateTool.NAME);
        }

        // 3. 转换为列表并设置到实体中
        po.setAvailableToolKeys(new java.util.ArrayList<>(toolSet));
        po.setClassName(agentConfig.getName());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param agentConfig
     * @return <br>
     */
    @Override
    public AgentConfig updateAgent(AgentConfig agentConfig) {
        DynamicAgentPo entity = dynamicAgentDao.get(Long.parseLong(agentConfig.getId()));
        updateEntityFromConfig(entity, agentConfig);
        dynamicAgentDao.update(entity);
        return mapToAgentConfig(entity);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public void deleteAgent(String id) {
        DynamicAgentPo entity = dynamicAgentDao.get(Long.parseLong(id));
        if (DEFAULT_AGENT_NAME.equals(entity.getAgentName())) {
            throw new IllegalArgumentException("默认智能体不能删除");
        }
        dynamicAgentDao.deleteById(entity.getId());
    }

    /**
     * 获取所有可用工具的列表。 该方法会生成一个唯一的 UUID，使用该 UUID 从 PlanningFactory 获取工具回调上下文， 并将其转换为 Tool 对象列表。最后，无论操作是否成功，都会调用 McpService
     * 的 close 方法关闭相关资源。
     * 
     * @return 可用工具的列表
     */
    @Override
    public List<Tool> getAvailableTools() {
        String uuid = CommonUtil.getTransactionID();

        try {
            Map<String, ToolCallBackContext> toolcallContext = planningFactory.toolCallbackMap(uuid);
            return toolcallContext.entrySet().stream().map(entry -> {
                Tool tool = new Tool();
                tool.setKey(entry.getKey());
                tool.setName(entry.getKey()); // You might want to provide a more friendly
                tool.setDescription(entry.getValue().getFunctionInstance().getDescription());
                tool.setEnabled(true);
                tool.setServiceGroup(entry.getValue().getFunctionInstance().getServiceGroup());
                return tool;
            }).collect(Collectors.toList());
        }
        finally {
            mcpService.close(uuid);
        }
    }

    /**
     * 创建一个动态的基础代理对象。 该方法会根据传入的代理名称、计划 ID 和初始代理设置，尝试加载一个动态代理对象， 并为其设置计划 ID 和工具回调映射，最后返回加载好的基础代理对象。
     * 
     * @param name 代理的名称，用于标识要加载的代理
     * @param planId 计划的 ID，将关联到代理对象上
     * @param initialAgentSetting 初始代理设置，包含一些用于初始化代理的配置信息
     * @return 加载好的基础代理对象
     * @throws 若在加载基础代理过程中出现异常，则抛出该异常
     */
    @Override
    public BaseAgent createDynamicBaseAgent(String name, String planId, Map<String, Object> initialAgentSetting) {
        LoggerUtil.info("创建一个新的BaseAgent: {0}, planId: {1}", name, planId);

        try {
            // 通过 dynamicAgentLoader 根据代理名称和初始设置加载已存在的动态代理对象
            DynamicAgent agent = dynamicAgentLoader.loadAgent(name, initialAgentSetting);
            agent.setPlanId(planId);

            // 从 PlanningFactory 获取工具回调映射，该映射包含工具与其回调上下文的关联信息
            Map<String, ToolCallBackContext> toolCallbackMap = planningFactory.toolCallbackMap(planId);
            // 为动态代理对象设置工具回调提供者
            agent.setToolCallbackProvider(new ToolCallbackProvider() {

                @Override
                public Map<String, ToolCallBackContext> getToolCallBackContext() {
                    return toolCallbackMap;
                }
            });
            LoggerUtil.info("成功加载BaseAgent: {0}, 可用的工具量为: {1}", name, agent.getToolCallList().size());
            return agent;
        }
        catch (Exception e) {
            LoggerUtil.error(e);
            throw new ServiceException(e);
        }
    }

    /**
     * 合并提示信息，处理已弃用的 SystemPrompt 属性。 如果 DynamicAgentEntity 对象的 SystemPrompt 属性不为空，将其内容合并到 NextStepPrompt 中， 并将
     * SystemPrompt 属性置空。
     * 
     * @param entity 动态代理实体对象，包含系统提示和下一步提示信息
     * @param agentName 代理的名称，用于日志记录
     * @return 处理后的 DynamicAgentEntity 对象
     */
    private AgentConfig mapToAgentConfig(DynamicAgentPo entity) {
        AgentConfig config = new AgentConfig();
        config.setId(entity.getId().toString());
        config.setName(entity.getAgentName());
        config.setDescription(entity.getAgentDescription());
        config.setSystemPrompt(GlobalConstants.BLANK);
        config.setNextStepPrompt(entity.getNextStepPrompt());
        config.setAvailableTools(entity.getAvailableToolKeys());
        config.setClassName(entity.getClassName());
        return config;
    }

}
