/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent.service;

import java.util.List;
import java.util.Map;

import com.hbasesoft.framework.ai.jmanus.agent.BaseAgent;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.vo.AgentConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.vo.Tool;

/**
 * <Description> <br>
 * AgentService 接口定义了与智能代理相关的操作方法， 用于管理智能代理的增删改查、获取可用工具以及创建动态代理实例等功能。
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.planning.service <br>
 */
public interface AgentService {

    /**
     * 获取所有智能代理的配置信息。
     * 
     * @return 包含所有智能代理配置信息的列表。
     */
    List<AgentConfig> getAllAgents();

    /**
     * 根据智能代理的唯一标识符获取对应的配置信息。
     * 
     * @param id 智能代理的唯一标识符。
     * @return 匹配到的智能代理配置信息，如果未找到则可能返回 null。
     */
    AgentConfig getAgentById(String id);

    /**
     * 创建一个新的智能代理。
     * 
     * @param agentConfig 包含新智能代理配置信息的对象。
     * @return 创建成功后的智能代理配置信息，可能包含系统自动生成的部分信息。
     */
    AgentConfig createAgent(AgentConfig agentConfig);

    /**
     * 更新现有智能代理的配置信息。
     * 
     * @param agentConfig 包含更新后智能代理配置信息的对象。
     * @return 更新成功后的智能代理配置信息。
     */
    AgentConfig updateAgent(AgentConfig agentConfig);

    /**
     * 根据智能代理的唯一标识符删除对应的智能代理。
     * 
     * @param id 智能代理的唯一标识符。
     */
    void deleteAgent(String id);

    /**
     * 获取所有可用的工具列表。
     * 
     * @return 包含所有可用工具信息的列表。
     */
    List<Tool> getAvailableTools();

    /**
     * 创建并返回一个可用的 BaseAgent 对象，类似于 PlanningFactory 中的 createPlanningCoordinator 方法。
     * 
     * @param name 智能代理的名称。
     * @param planId 计划 ID，用于标识智能代理所属的计划。
     * @param initialAgentSetting 智能代理的初始设置，以键值对形式存储。
     * @return 创建成功后的 BaseAgent 对象。
     */
    BaseAgent createDynamicBaseAgent(String name, String planId, Map<String, Object> initialAgentSetting);
}
