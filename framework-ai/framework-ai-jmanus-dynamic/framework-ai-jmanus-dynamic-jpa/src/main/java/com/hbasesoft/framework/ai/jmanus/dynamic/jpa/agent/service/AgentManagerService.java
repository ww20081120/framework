/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.agent.service;

import java.util.List;

import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.vo.AgentConfig;
import com.hbasesoft.framework.ai.jmanus.dynamic.agent.model.vo.Tool;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.jpa.agent.service <br>
 */
public interface AgentManagerService {

	List<AgentConfig> getAllAgentsByNamespace(String namespace);

	AgentConfig getAgentById(String id);

	AgentConfig createAgent(AgentConfig agentConfig);

	void deleteAgent(String id);

	List<Tool> getAvailableTools();

}
