/*******************************************************************************
 *  Copyright © 2024-2034 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 *  transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 *  or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ******************************************************************************/

package com.hbasesoft.framework.ai.demo.graph.conf;

import static com.alibaba.cloud.ai.graph.StateGraph.END;

import java.util.Map;

import com.alibaba.cloud.ai.graph.OverAllStateFactory;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.state.AgentStateFactory;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.hbasesoft.framework.ai.demo.graph.dispatcher.FeedbackDispatcher;
import com.hbasesoft.framework.ai.demo.graph.node.RewordingNode;
import com.hbasesoft.framework.ai.demo.graph.node.SummarizerNode;
import com.hbasesoft.framework.ai.demo.graph.node.SummaryFeedbackClassifierNode;
import com.hbasesoft.framework.ai.demo.graph.node.TitleGeneratorNode;

/**
 * 配置类，用于构建写作助手的状态图流程。 状态图包含摘要生成、反馈分类、改写和标题生成节点。 支持根据用户反馈进行循环优化摘要。
 */
@Configuration
public class WritingAssistantAutoconfiguration {

    /**
     * 创建并配置状态图 Bean。
     *
     * @param chatModel Spring AI 的 ChatModel 实例，用于驱动大模型交互。
     * @return 构建完成的状态图实例。
     * @throws GraphStateException 如果图构建过程中发生错误。
     */
    @Bean
    public StateGraph writingAssistantGraph(final ChatModel chatModel) throws GraphStateException {
        // 使用 ChatModel 构建 ChatClient，并添加日志插件以便调试查看对话内容
        ChatClient chatClient = ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();

        // 定义全局状态工厂，创建 OverAllState 实例，并注册状态字段及更新策略
        OverAllStateFactory stateFactory = () -> {
            OverAllState state = new OverAllState();
            // 注册状态字段及其更新策略为 ReplaceStrategy（覆盖更新）

            // 存储原始输入文本内容，通常是用户提供的待处理文本存储原始输入文本内容，通常是用户提供的待处理文本
            state.registerKeyAndStrategy("original_text", new ReplaceStrategy());

            // 保存由 summarizer 节点生成的摘要内容。
            state.registerKeyAndStrategy("summary", new ReplaceStrategy());

            // 记录用户对摘要内容的反馈（例如满意或不满意），用于决定是否需要重新生成摘要。
            state.registerKeyAndStrategy("summary_feedback", new ReplaceStrategy());

            // 存储经过 reworder 节点改写后的更生动、优化的摘要内容。
            state.registerKeyAndStrategy("reworded", new ReplaceStrategy());

            // 保存由 title_generator 节点基于改写后的内容生成的标题。
            state.registerKeyAndStrategy("title", new ReplaceStrategy());
            return state;
        };

        // 创建状态图对象，指定名称和状态工厂
        StateGraph graph = new StateGraph("Writing Assistant with Feedback Loop", stateFactory.create());

        // 添加各个功能节点
        graph
            // 摘要生成节点：使用 SummarizerNode 对原始文本进行摘要
            .addNode("summarizer", AsyncNodeAction.node_async(new SummarizerNode(chatClient)))
            // 反馈分类节点：判断摘要是否满意，决定后续流程
            .addNode("feedback_classifier",
                AsyncNodeAction.node_async(new SummaryFeedbackClassifierNode(chatClient, "summary")))
            // 改写节点：将摘要内容用更生动的语言重新表达
            .addNode("reworder", AsyncNodeAction.node_async(new RewordingNode(chatClient)))
            // 标题生成节点：基于改写后的内容生成吸引人的标题
            .addNode("title_generator", AsyncNodeAction.node_async(new TitleGeneratorNode(chatClient)))

            // 设置图的起始点为“summarizer”节点
            .addEdge(StateGraph.START, "summarizer")
            // 从“summarizer”到“feedback_classifier”的边，顺序执行
            .addEdge("summarizer", "feedback_classifier")
            // 条件分支边：根据“feedback_classifier”的结果决定下一步跳转
            .addConditionalEdges("feedback_classifier", AsyncEdgeAction.edge_async(new FeedbackDispatcher()),
                Map.of("positive", "reworder", "negative", "summarizer"))
            // “reworder”完成后跳转到“title_generator”
            .addEdge("reworder", "title_generator")
            // “title_generator”是最终节点，流程结束
            .addEdge("title_generator", END);

        // 获取图的 PlantUML 表示形式，并打印出来，便于可视化理解流程
        GraphRepresentation representation = graph.getGraph(GraphRepresentation.Type.MERMAID,
            "writing assistant flow");

        System.out.println("\n=== Writing Assistant UML Flow ===");
        System.out.println(representation.content());
        System.out.println("==================================\n");

        return graph;
    }
}
