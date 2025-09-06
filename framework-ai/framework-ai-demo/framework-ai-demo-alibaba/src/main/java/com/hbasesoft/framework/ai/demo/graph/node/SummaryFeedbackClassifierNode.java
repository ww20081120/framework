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
import org.springframework.util.StringUtils;

import java.util.Map;

public class SummaryFeedbackClassifierNode implements NodeAction {

    /** 聊天客户端 */
    private final ChatClient chatClient;

    /** 输入键 */
    private final String inputKey;

    /**
     * 构造函数
     *
     * @param chatClient 聊天客户端
     * @param inputKey 输入键
     */
    public SummaryFeedbackClassifierNode(final ChatClient chatClient, final String inputKey) {
        this.chatClient = chatClient;
        this.inputKey = inputKey;
    }

    /**
     * 分类摘要反馈
     *
     * @param state 全局状态
     * @return 包含反馈分类的映射
     * @throws Exception 异常
     */
    @Override
    public Map<String, Object> apply(final OverAllState state) throws Exception {
        String summary = (String) state.value(inputKey).orElse("");
        if (!StringUtils.hasText(summary)) {
            throw new IllegalArgumentException("summary is empty in state");
        }

        String prompt = """
            以下是一个自动生成的中文摘要。请你判断它是否让用户满意。如果满意，请返回 "positive"，否则返回 "negative"：

            摘要内容：
            %s
            """.formatted(summary);

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        String output = response.getResult().getOutput().getText();

        String classification = output.toLowerCase().contains("positive") ? "positive" : "negative";

        return Map.of("summary_feedback", classification);
    }
}
