/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.simple;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年5月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.simple <br>
 */
@RequestMapping("/helloworld")
@RestController
public class HelloworldController {

    /** */
    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    /** */
    private static final double TOP_P = 0.7;

    /** */
    private final ChatClient dashScopeChatClient;

    /**
     * @param chatClientBuilder
     */
    public HelloworldController(final ChatClient.Builder chatClientBuilder) {
        dashScopeChatClient = chatClientBuilder.defaultSystem(DEFAULT_PROMPT)
            // 实现 Chat Memory 的 Advisor
            // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
            .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
            // 实现 Logger 的 Advisor
            .defaultAdvisors(new SimpleLoggerAdvisor())
            // 设置 ChatClient 中 ChatModel 的 Options 参数
            .defaultOptions(DashScopeChatOptions.builder().withTopP(TOP_P).build()).build();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param query
     * @return <br>
     */
    @GetMapping("/simple/chat")
    public String simpleChat(final String query) {
        return dashScopeChatClient.prompt(query).call().content();
    }
}
