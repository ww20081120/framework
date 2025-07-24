/*******************************************************************************
 *  Copyright © 2024-2034 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 *  transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 *  or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ******************************************************************************/

package com.hbasesoft.framework.ai.demo.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.Map;

public class SummarizerNode implements NodeAction {

    /** 聊天客户端 */
    private final ChatClient chatClient;

    /**
     * 构造函数
     *
     * @param chatClient 聊天客户端
     */
    public SummarizerNode(final ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 对原始文本进行摘要
     *
     * @param state 全局状态
     * @return 包含摘要的映射
     */
    @Override
    public Map<String, Object> apply(final OverAllState state) {
        String text = (String) state.value("original_text").orElse("");
        String prompt = "请对以下中文文本进行简洁明了的摘要：\n\n" + text;

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        String summary = response.getResult().getOutput().getText();

        return Map.of("summary", summary);
    }
}
