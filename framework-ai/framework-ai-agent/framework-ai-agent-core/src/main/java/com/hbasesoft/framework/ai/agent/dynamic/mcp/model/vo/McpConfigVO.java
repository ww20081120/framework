/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo;

import java.util.List;
import java.util.Map;

import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.enums.McpConfigStatus;
import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.enums.McpConfigType;

import lombok.Data;

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
@Data
public class McpConfigVO {

    private Long id;

    private String mcpServerName;

    private McpConfigType connectionType;

    private String connectionConfig;

    private List<String> toolNames; // Add tool name list for frontend display

    // New field-based properties
    private String command;

    private String url;

    private List<String> args;

    private Map<String, String> env;

    private McpConfigStatus status;
}
