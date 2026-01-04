/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.openai;

import java.time.LocalDateTime;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.modelcalllimit.ModelCallLimitHook;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年12月28日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.openai <br>
 */
@RequestMapping("/test")
@RestController
public class TestController {

    /** 默认温度参数 */
    private static final double DEFAULT_TEMPERATURE = 0.7;

    /** 默认最大令牌数 */
    private static final int DEFAULT_MAX_TOKENS = 2000;

    /** 默认核心采样参数 */
    private static final double DEFAULT_TOP_P = 0.9;

    /** 最大执行次数 */
    private static final int MAX_TIMES = 5;

    /** agent */
    private ReactAgent agent;

    /** chatModel */
    private ChatModel chatModel;

    /**
     * <Description> <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2025年12月28日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.ai.demo.openai <br>
     */
    public static class SearchTool {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @return <br>
         */
        @Tool(description = "获取用户时区的当前日期和时间")
        String getCurrentDateTime() {
            return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
        }

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param query
         * @return <br>
         */
        @Tool(description = "搜索功能")
        public String search(final String query) {
            // 实现搜索逻辑
            return "搜索结果: " + query;
        }
    }

    /**
     * 
     */
    public TestController() {

        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl("http://127.0.0.1:11434").apiKey("your api key").build();

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder().temperature(DEFAULT_TEMPERATURE) // 控制随机性
            .model("qwen3:8b").maxTokens(DEFAULT_MAX_TOKENS) // 最大输出长度
            .topP(DEFAULT_TOP_P) // 核采样参数
            .streamUsage(true).build();

        chatModel = OpenAiChatModel.builder().defaultOptions(chatOptions).openAiApi(openAiApi).build();

        agent = ReactAgent.builder().model(chatModel).name("weather agent")
            .hooks(ModelCallLimitHook.builder().runLimit(MAX_TIMES).build()).tools(ToolCallbacks.from(new SearchTool()))
            .build();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param text
     * @return <br>
     */
    @GetMapping("/say")
    public String say(final @RequestParam("text") @NonNull String text) {
        try {
            UserMessage userMessage = new UserMessage(text);
            AssistantMessage response = agent.call(userMessage);
            return response.getText();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
