/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.simple;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年5月14日 <br>
 * @see com.hbasesoft.framework.ai.demo.simple <br>
 * @since V1.0<br>
 */
@RequestMapping("/simple/helloworld")
@RestController
public class HelloworldController {

    /** 默认的 Prompt */
    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    /** 默认的 核心采样 Nucleus Sampling */
    private static final double TOP_P = 0.7;

    /** 默认的 最大 token */
    private static final Integer MAX_TOKEN = 150;

    /** ChatClient */
    private final ChatClient dashScopeChatClient;

    /**
     * @param chatClientBuilder
     */
    public HelloworldController(final ChatClient.Builder chatClientBuilder) {
        dashScopeChatClient = chatClientBuilder.defaultSystem(DEFAULT_PROMPT)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                // .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                // 实现 Logger 的 Advisor
                .defaultAdvisors(new SimpleLoggerAdvisor())
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(DashScopeChatOptions.builder().withTopP(TOP_P).build()).build();
    }

    /**
     * Description: <br>
     *
     * @param input 用户输入
     * @return 聊天返回结果
     * @author 王伟<br>
     * @taskId <br>
     */
    @GetMapping("/chat")
    public String simpleChat(final String input) {
        return dashScopeChatClient.prompt(input).call().content();
    }

    /**
     * Description: 使用prompt作为参数进行输入 <br>
     *
     * @param input 用户输入
     * @return 聊天返回
     */
    @GetMapping("/chatWithPrompt")
    public String simpleChatPrompt(final String input) {
        Prompt prompt = new Prompt(input);
        return dashScopeChatClient.prompt(prompt).call().content();
    }

    /**
     * Description: ChatClient 流式调用 <br>
     *
     * @param input    用户输入
     * @param response HTTP响应对象
     * @return 聊天返回
     */
    @GetMapping("/chatWithStream")
    public Flux<String> simpleChatStream(final String input, final HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        ChatOptions options = DashScopeChatOptions.builder().withTopP(TOP_P).withMaxToken(MAX_TOKEN).build();
        return dashScopeChatClient.prompt(input).options(options).stream().content();
    }

    /**
     * Description: 使用 ChatOptions 配置参数进行输入 <br>
     *
     * @param input 用户输入
     * @return 聊天返回
     */
    @GetMapping("/chatWithOptions")
    public String simpleChatWithOptions(final String input) {
        ChatOptions options = DashScopeChatOptions.builder().withTopP(TOP_P).withMaxToken(MAX_TOKEN).build();
        return dashScopeChatClient.prompt(input).options(options).call().content();
    }
}
