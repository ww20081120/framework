/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo;

import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;

import io.modelcontextprotocol.client.McpAsyncClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo <br>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class McpServiceVo {

    private McpAsyncClient mcpAsyncClient;

    private AsyncMcpToolCallbackProvider asyncMcpToolCallbackProvider;

    private String serviceGroup;
}
