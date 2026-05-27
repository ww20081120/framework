/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.openai.agent;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import com.openai.client.okhttp.OpenAIOkHttpClient;

import com.hbasesoft.framework.ai.core.Agent;
import com.hbasesoft.framework.ai.spring.AgentConfig;

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

    /** 默认温度参数 */
    private static final double DEFAULT_TEMPERATURE = 0.7;

    /** 默认核心采样参数 */
    private static final double DEFAULT_TOP_P = 0.9;

    /** 默认最大令牌数 */
    private static final int DEFAULT_MAX_TOKENS = 2000;

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
    public ChatModel model() {

        var openAiClient = OpenAIOkHttpClient.builder()
            .baseUrl("http://127.0.0.1:11434")
            .apiKey("your api key")
            .build();

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder().temperature(DEFAULT_TEMPERATURE) // 控制随机性
            .model("qwen3-coder:30b-a3b-fp16").maxTokens(DEFAULT_MAX_TOKENS) // 最大输出长度
            .topP(DEFAULT_TOP_P) // 核采样参数
            .streamUsage(true).build();

        return OpenAiChatModel.builder().options(chatOptions).openAiClient(openAiClient).build();

    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param city 城市名称<br>
     * @return <br>
     */
    @Tool(description = "查询天气的工具")
    public String weather(@ToolParam(description = "城市名称") final String city) {
        return "今天是晴天";
    }
}
