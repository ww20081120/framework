/*******************************************************************************
 *  Copyright © 2024-2034 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 *  transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 *  or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ******************************************************************************/

package com.hbasesoft.framework.ai.demo.graph.conf;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.state.AgentStateFactory;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;

@Configuration
public class WritingAssistantAutoconfiguration {

    public StateGraph writingAssistantGraph(final ChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();

        // 创建状态图
        AgentStateFactory<OverAllState> stateFactory = (inputs) -> {
            OverAllState state = new OverAllState();
            state.registerKeyAndStrategy("original_text", new ReplaceStrategy());
            state.registerKeyAndStrategy("summary", new ReplaceStrategy());
            state.registerKeyAndStrategy("summary_feedback", new ReplaceStrategy());
            state.registerKeyAndStrategy("reworded", new ReplaceStrategy());
            state.registerKeyAndStrategy("title", new ReplaceStrategy());
            state.input(inputs);
            return state;
        };

        StateGraph graph = new StateGraph("Writing Assistant with Feedback Loop", stateFactory);

        // graph.addNode("summarizer", AsyncNodeAction.node_async())

        return graph;
    }
}
