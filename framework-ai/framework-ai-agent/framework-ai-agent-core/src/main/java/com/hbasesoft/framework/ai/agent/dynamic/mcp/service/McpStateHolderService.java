/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.mcp.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.agent.dynamic.mcp.model.vo.McpState;

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
@Service
public class McpStateHolderService implements IMcpStateHolderService {

    private Map<String, McpState> mcpStateMap = new ConcurrentHashMap<>();

    public McpState getMcpState(String key) {
        return mcpStateMap.get(key);
    }

    public void setMcpState(String key, McpState state) {
        mcpStateMap.put(key, state);
    }

    public void removeMcpState(String key) {
        mcpStateMap.remove(key);
    }

}