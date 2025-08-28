/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.service;

import java.util.List;

import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpServiceVo;

import lombok.RequiredArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.mcp.service <br>
 */
@RequiredArgsConstructor
public abstract class AbstractMcpService implements IMcpService {

	protected final McpCacheManager cacheManager;

	/**
	 * Get MCP service entity list
	 * 
	 * @param planId Plan ID
	 * @return MCP service entity list
	 */
	@Override
	public List<McpServiceVo> getFunctionCallbacks(String planId) {
		return cacheManager.getServiceEntities(planId);
	}

	/**
	 * Close MCP service for specified plan
	 * 
	 * @param planId Plan ID
	 */
	@Override
	public void close(String planId) {
		cacheManager.invalidateCache(planId);
	}
}
