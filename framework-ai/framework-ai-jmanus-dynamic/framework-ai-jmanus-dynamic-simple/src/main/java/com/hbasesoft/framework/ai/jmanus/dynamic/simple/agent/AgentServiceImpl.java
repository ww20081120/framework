/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.simple.agent;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.AbstractAgentService;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.service.IDynamicAgentLoader;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.vo.AgentConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.IMcpService;
import com.hbasesoft.framework.ai.jmanus.planning.IPlanningFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.simple.agent <br>
 */
@Service
public class AgentServiceImpl extends AbstractAgentService {

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
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param agentConfig
	 * @return <br>
	 */
	@Override
	public AgentConfig updateAgent(AgentConfig agentConfig) {
		return null;
	}

	/**
	 * Description: <br> 
	 *  
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param namespace
	 * @param agentName
	 * @return <br>
	 */ 
	@Override
	public AgentConfig getAgentByName(String namespace, String agentName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Description: <br> 
	 *  
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param namespace
	 * @return <br>
	 */ 
	@Override
	public List<AgentConfig> getAllAgentsByNamespace(String namespace) {
		return null;
	}

}
