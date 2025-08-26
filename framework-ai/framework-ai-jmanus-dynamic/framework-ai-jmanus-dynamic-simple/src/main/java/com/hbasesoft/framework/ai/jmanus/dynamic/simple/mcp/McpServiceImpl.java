/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.simple.mcp;

import java.util.List;

import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.enums.McpConfigStatus;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.model.vo.McpConfigVO;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.AbstractMcpService;
import com.hbasesoft.framework.ai.jmanus.dynamic.mcp.service.McpCacheManager;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.ai.jmanus.dynamic.simple.mcp <br>
 */
public class McpServiceImpl extends AbstractMcpService {

	/**
	 * @param cacheManager
	 */
	public McpServiceImpl(McpCacheManager cacheManager) {
		super(cacheManager);
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param id
	 * @param status
	 * @return <br>
	 */
	@Override
	public boolean updateMcpServerStatus(Long id, McpConfigStatus status) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	@Override
	public List<McpConfigVO> queryServices() {
		// TODO Auto-generated method stub
		return null;
	}

}
