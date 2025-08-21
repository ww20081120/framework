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
	List<AgentConfig> getAllAgentsByNamespace(String namespace);

	AgentConfig getAgentById(String id);

	AgentConfig createAgent(AgentConfig agentConfig);

	AgentConfig updateAgent(AgentConfig agentConfig);

	void deleteAgent(String id);

	List<Tool> getAvailableTools();

	/**
	 * Create and return a usable BaseAgent object, similar to the
	 * createPlanningCoordinator method in PlanningFactory
	 * 
	 * @param name          Agent name
	 * @param currentPlanId Plan ID, used to identify the plan the agent belongs to
	 * @return Created BaseAgent object
	 */
	BaseAgent createDynamicBaseAgent(String name, String currentPlanId, String rootPlanId,
			Map<String, Object> initialAgentSetting, String expectedReturnInfo);;
}
