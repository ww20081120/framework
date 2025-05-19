/*******************************************************************************
 *  Copyright © 2024-2034 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 *  transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 *  or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ******************************************************************************/

package com.hbasesoft.framework.ai.demo.graph.node;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;

public class SummarizerNode implements NodeAction {

    private final ChatClient chatClient;

    public SummarizerNode(final ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * @param t
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> apply(final OverAllState state) {
        String text = (String) state.value("original_text").orElse("");
        String prompt = "请对以下中文文本进行简洁明了的摘要：\n\n" + text;

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        String summary = response.getResult().getOutput().getText();

        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        return Map.of();
    }
}
