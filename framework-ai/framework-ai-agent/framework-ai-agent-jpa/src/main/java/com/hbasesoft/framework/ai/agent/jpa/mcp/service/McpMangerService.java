/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.mcp.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpServerRequestVO;
import com.hbasesoft.framework.ai.agent.jpa.mcp.po.McpConfigPo4Jpa;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.jpa.mcp.service <br>
 */
public interface McpMangerService {

    /**
     * Batch save MCP server configurations
     * 
     * @param configJson MCP configuration JSON string
     * @return Configuration entity list
     * @throws IOException IO exception
     */
    List<McpConfigPo4Jpa> saveMcpServers(String configJson) throws IOException;

    /**
     * Save single MCP server configuration
     * 
     * @param requestVO MCP server form request
     * @return Configuration entity
     * @throws IOException IO exception
     */
    McpConfigPo4Jpa saveMcpServer(McpServerRequestVO requestVO) throws IOException;

    /**
     * Delete MCP server
     * 
     * @param id MCP server ID
     */
    void removeMcpServer(long id);

    /**
     * Delete MCP server
     * 
     * @param mcpServerName MCP server name
     */
    void removeMcpServer(String mcpServerName);

    /**
     * Get all MCP server configurations
     * 
     * @return MCP configuration entity list
     */
    List<McpConfigPo4Jpa> getMcpServers();

    /**
     * Find MCP configuration by ID
     * 
     * @param id MCP configuration ID
     * @return Optional MCP configuration entity
     */
    Optional<McpConfigPo4Jpa> findById(Long id);
}
