/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent.service;

import java.util.List;
import java.util.Map;

import com.hbasesoft.framework.ai.jmanus.agent.BaseAgent;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.vo.AgentConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.vo.Tool;

/**
 * 智能代理服务接口<br>
 * 该接口定义了与智能代理相关的操作方法，用于管理智能代理的增删改查、获取可用工具以及创建动态代理实例等功能。
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
	 * 根据代理名称获取智能代理配置
	 * 
	 * @param agentName 代理名称
	 * @param namespace 命名空间
	 * @return 代理配置信息
	 */
	AgentConfig getAgentByName(String namespace, String agentName);

	/**
	 * 获取指定命名空间下的所有智能代理配置
	 * 
	 * @param namespace 命名空间
	 * @return 代理配置信息列表
	 */
	List<AgentConfig> getAllAgentsByNamespace(String namespace);

	/**
	 * 更新智能代理配置
	 * 
	 * @param agentConfig 代理配置信息
	 * @return 更新后的代理配置
	 */
	AgentConfig updateAgent(AgentConfig agentConfig);

	/**
	 * 创建并返回一个可用的BaseAgent对象 该方法类似于PlanningFactory中的createPlanningCoordinator方法
	 * 
	 * @param name                代理名称
	 * @param currentPlanId       当前计划ID，用于标识代理所属的计划
	 * @param rootPlanId          根计划ID
	 * @param initialAgentSetting 初始代理设置
	 * @param expectedReturnInfo  期望返回信息
	 * @return 创建的BaseAgent对象
	 */
	BaseAgent createDynamicBaseAgent(String name, String currentPlanId, String rootPlanId,
			Map<String, Object> initialAgentSetting, String expectedReturnInfo);

	List<Tool> getAvailableTools();

}