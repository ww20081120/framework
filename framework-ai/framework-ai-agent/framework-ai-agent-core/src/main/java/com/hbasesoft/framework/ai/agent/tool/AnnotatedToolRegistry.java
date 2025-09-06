/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * <Description> Registry for tools created from annotated methods <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
@Component
public class AnnotatedToolRegistry {

    /** Registry of tools created from annotated methods */
    private static final Map<String, ToolCallBiFunctionDef<?>> ANNOTATED_TOOLS = new ConcurrentHashMap<>();

    /**
     * Register a tool created from an annotated method
     * 
     * @param toolName The name of the tool
     * @param tool The tool instance
     */
    public void registerTool(ToolCallBiFunctionDef<?> tool) {
        ANNOTATED_TOOLS.put(tool.getName(), tool);
    }

    /**
     * Get all registered annotated tools
     * 
     * @return Map of tool names to tool instances
     */
    public Map<String, ToolCallBiFunctionDef<?>> getRegisteredTools() {
        return ANNOTATED_TOOLS;
    }

    /**
     * Get a specific registered tool by name
     * 
     * @param toolName The name of the tool
     * @return The tool instance, or null if not found
     */
    public ToolCallBiFunctionDef<?> getTool(String toolName) {
        return ANNOTATED_TOOLS.get(toolName);
    }

    /**
     * Clear all registered tools
     */
    public void clearTools() {
        ANNOTATED_TOOLS.clear();
    }
}
