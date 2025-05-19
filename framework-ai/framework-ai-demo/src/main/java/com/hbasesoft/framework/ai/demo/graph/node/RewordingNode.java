/*******************************************************************************
 *  Copyright © 2024-2034 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 *  transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 *  or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ******************************************************************************/

package com.hbasesoft.framework.ai.demo.graph.node;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;

public class RewordingNode implements NodeAction {

    private final ChatClient chatClient;

    public RewordingNode(final ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(final OverAllState state) throws Exception {
        // 从全局状态中获取摘要内容
        String summary = (String) state.value("summary").orElse("");

        // 构建提示词，要求模型将摘要用更优美、生动的语言改写，同时保持信息不变
        String prompt = "请将以下摘要用更优美、生动的语言改写， 同时保持信息不便：\n\n" + summary;

        // 调用大模型进行改写
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        // 获取大模型返回的改写后文本内容
        String reworded = response.getResult().getOutput().getText();

        // 返回改写后的结果，供后续节点使用
        return Map.of("reworded", reworded);
    }

}
