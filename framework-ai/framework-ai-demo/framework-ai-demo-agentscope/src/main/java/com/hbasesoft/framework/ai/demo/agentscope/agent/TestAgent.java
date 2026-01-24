/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.agentscope.agent;

import com.hbasesoft.framework.ai.agentscope.AgentConfig;
import com.hbasesoft.framework.ai.core.Agent;

import io.agentscope.core.model.Model;
import io.agentscope.core.model.OllamaChatModel;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年1月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.agentscope.agent <br>
 */
@Agent(name = "TestAgent")
public class TestAgent implements AgentConfig {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String systemPrompt() {
        return "我是万能的小助手，可以使用weather方法查询天气预报！！";
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Model model() {
        return OllamaChatModel.builder().baseUrl("http://127.0.0.1:11434").modelName("qwen3:8b")
            .build();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Tool(description = "查询天气的工具")
    public String weather(@ToolParam(name = "city", description = "城市名称") String city) {
        return "今天是晴天";
    }
}
