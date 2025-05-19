package com.hbasesoft.framework.ai.demo.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.Map;

public class TitleGeneratorNode implements NodeAction {

    private final ChatClient chatClient;

    public TitleGeneratorNode(final ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String content = (String) state.value("reworded").orElse("");
        String prompt = "请为以下内容生成一个简洁有吸引力的中文标题：\n\n" + content;

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        String title = response.getResult().getOutput().getText();

        return Map.of("title", title);
    }
}
