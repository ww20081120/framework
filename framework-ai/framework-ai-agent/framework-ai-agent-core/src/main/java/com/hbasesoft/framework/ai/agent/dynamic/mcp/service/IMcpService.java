/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.service;

import java.util.List;

import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.enums.McpConfigStatus;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpConfigVO;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpServiceVo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.mcp.service <br>
 */
public interface IMcpService {

	/**
	 * Get MCP service entity list
	 * 
	 * @param planId Plan ID
	 * @return MCP service entity list
	 */
	List<McpServiceVo> getFunctionCallbacks(String planId);

	/**
	 * Close MCP services for specified plan
	 * 
	 * @param planId Plan ID
	 */
	void close(String planId);

	/**
	 * Update MCP server status
	 * 
	 * @param id     MCP server ID
	 * @param status Target status
	 * @return true if updated successfully, false otherwise
	 */
	boolean updateMcpServerStatus(Long id, McpConfigStatus status);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	List<McpConfigVO> queryServices();
}
