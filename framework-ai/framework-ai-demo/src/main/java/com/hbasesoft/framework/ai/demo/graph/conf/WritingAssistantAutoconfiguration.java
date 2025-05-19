/*******************************************************************************
 *  Copyright © 2024-2034 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 *  transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 *  or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ******************************************************************************/

package com.hbasesoft.framework.ai.demo.graph.conf;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.state.AgentStateFactory;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.hbasesoft.framework.ai.demo.graph.dispatcher.FeedbackDispatcher;
import com.hbasesoft.framework.ai.demo.graph.node.RewordingNode;
import com.hbasesoft.framework.ai.demo.graph.node.SummarizerNode;
import com.hbasesoft.framework.ai.demo.graph.node.SummaryFeedbackClassifierNode;
import com.hbasesoft.framework.ai.demo.graph.node.TitleGeneratorNode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;

@Configuration
public class WritingAssistantAutoconfiguration {

    @Bean
    public StateGraph writingAssistantGraph(final ChatModel chatModel) throws GraphStateException {
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

        graph.addNode("summarizer", AsyncNodeAction.node_async(new SummarizerNode(chatClient)))
            .addNode("feedback_classifier",
                AsyncNodeAction.node_async(new SummaryFeedbackClassifierNode(chatClient, "summary")))
            .addNode("reworder", AsyncNodeAction.node_async(new RewordingNode(chatClient)))
            .addNode("title_generator", AsyncNodeAction.node_async(new TitleGeneratorNode(chatClient)))

            .addEdge(StateGraph.START, "summarizer").addEdge("summarizer", "feedback_classifier")
            .addConditionalEdges("feedback_classifier", AsyncEdgeAction.edge_async(new FeedbackDispatcher()),
                Map.of("positive", "reworder", "negative", "summarizer"))

            .addEdge("reworder", "title_generator").addEdge("title_generator", END);

        GraphRepresentation representation = graph.getGraph(GraphRepresentation.Type.PLANTUML,
            "writing assistant flow");

        System.out.println("\n=== Writing Assistant UML Flow ===");
        System.out.println(representation.content());
        System.out.println("==================================\n");

        return graph;
    }
}
