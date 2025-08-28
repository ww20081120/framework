/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.service;

import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpState;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.mcp.service <br>
 */
public interface IMcpStateHolderService {

	/**
	 * Get MCP state
	 * 
	 * @param key state key
	 * @return MCP state
	 */
	McpState getMcpState(String key);

	/**
	 * Set MCP state
	 * 
	 * @param key   state key
	 * @param state MCP state
	 */
	void setMcpState(String key, McpState state);

	/**
	 * Remove MCP state
	 * 
	 * @param key state key
	 */
	void removeMcpState(String key);
}
