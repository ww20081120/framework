/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.agentscope.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.agentscope.core.model.Model;
import io.agentscope.core.model.OpenAIChatModel;
import io.agentscope.core.tool.coding.ShellCommandTool;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年1月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.agentscope.config <br>
 */
@Configuration
public class AiConfig {

    /**
     * Description: 创建默认的AI模型Bean<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return OpenAI兼容的模型实例<br>
     */
    @Bean
    public Model defaultModel() {
        return OpenAIChatModel.builder().baseUrl("http://127.0.0.1:11434").modelName("qwen3-coder:30b-a3b-fp16")
            .build();
    }

    /**
     * Description: 创建Shell命令工具Bean<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return Shell命令工具实例<br>
     */
    @Bean
    public ShellCommandTool shellCommandTool() {
        return new ShellCommandTool();
    }
}
