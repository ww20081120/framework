/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.agent.service;

import java.util.List;
import java.util.Map;

import com.hbasesoft.framework.ai.jmanus.dynamic.agent.DynamicAgent;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.agent.service <br>
 */
public interface IDynamicAgentLoader {

	/**
	 * Load Agent
	 * 
	 * @param agentName           Agent name
	 * @param initialAgentSetting Initial Agent settings
	 * @return Dynamic Agent
	 */
	DynamicAgent loadAgent(String agentName, Map<String, Object> initialAgentSetting);

	/**
	 * Get all Agents
	 * 
	 * @return List of Agent entities
	 */
	List<DynamicAgent> getAllAgents();

	default List<DynamicAgent> getAgents(ExecutionContext context) {
		return getAllAgents();
	}

}
