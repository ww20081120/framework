/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.planning.service;

import java.util.Map;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.ai.jmanus.tool.ToolCallBiFunctionDef;

import lombok.Data;

/**
 * <Description> 负责创建规划协调器和工具回调映射<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.planning.service <br>
 */
@Service
public class PlanningFactory {

    @Data
    public static class ToolCallBackContext {

        private final ToolCallback toolCallback;

        private final ToolCallBiFunctionDef<?> functionInstance;

    }

    /**
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param uuid
     * @return <br>
     */ 
    public Map<String, ToolCallBackContext> toolCallbackMap(String uuid) {
        // TODO Auto-generated method stub
        return null;
    }
}
