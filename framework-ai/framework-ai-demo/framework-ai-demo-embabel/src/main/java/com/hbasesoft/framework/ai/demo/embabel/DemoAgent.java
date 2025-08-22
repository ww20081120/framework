/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.embabel;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Condition;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.library.HasContent;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.embabel <br>
 */

@Agent(description = "一个使用 Qwen3-Coder 模型帮助处理编码任务的演示代理")
public class DemoAgent {

    @Action
    public CodingTask extractCodingTask(String userInput, OperationContext context) {
        return context.ai()
                .withDefaultLlm()
                .createObjectIfPossible(
                        "从用户输入中提取编码任务。识别:\n" +
                        "1. 编程语言\n" +
                        "2. 任务描述\n" +
                        "3. 任何特定要求\n\n" +
                        "用户输入: " + userInput,
                        CodingTask.class
                );
    }

    @Action
    public CodeSolution generateCode(CodingTask task, OperationContext context) {
        return context.ai()
                .withDefaultLlm()
                .createObject(
                        "为以下任务生成代码解决方案:\n" +
                        "语言: " + task.language() + "\n" +
                        "任务: " + task.description() + "\n" +
                        "要求: " + task.requirements() + "\n\n" +
                        "提供干净、文档齐全的代码，遵循该语言的最佳实践。",
                        CodeSolution.class
                );
    }

    @Action
    public Explanation explainCode(CodeSolution solution, OperationContext context) {
        return context.ai()
                .withDefaultLlm()
                .createObject(
                        "提供以下代码解决方案的详细解释:\n" +
                        "代码:\n" + solution.code() + "\n\n" +
                        "解释:\n" +
                        "1. 采用的方法\n" +
                        "2. 关键组件及其目的\n" +
                        "3. 任何重要的考虑因素或权衡",
                        Explanation.class
                );
    }

    @Condition
    public boolean isCodeGenerationNeeded(CodingTask task) {
        // 这个条件检查是否需要生成代码
        return task != null && !task.description().isEmpty();
    }

    @AchievesGoal(
            description = "提供带有解释的完整编码解决方案"
    )
    @Action
    public FinalResponse provideCompleteSolution(
            CodingTask task,
            CodeSolution solution,
            Explanation explanation,
            OperationContext context) {
        
        return context.ai()
                .withDefaultLlm()
                .createObject(
                        "创建一个结合了编码任务、解决方案和解释的最终格式化响应:\n" +
                        "任务: " + task.description() + "\n" +
                        "解决方案: " + solution.code() + "\n" +
                        "解释: " + explanation.text() + "\n\n" +
                        "格式化为干净、专业的响应，并带有适当的 markdown 格式。",
                        FinalResponse.class
                );
    }

    @JsonClassDescription("要解决的编码任务")
    public record CodingTask(
            @JsonPropertyDescription("要使用的编程语言") String language,
            @JsonPropertyDescription("编码任务描述") String description,
            @JsonPropertyDescription("特定要求或约束") String requirements
    ) {
        @JsonCreator
        public CodingTask(
                @JsonProperty("language") String language,
                @JsonProperty("description") String description,
                @JsonProperty("requirements") String requirements) {
            this.language = language;
            this.description = description;
            this.requirements = requirements;
        }
    }

    @JsonClassDescription("生成的代码解决方案")
    public record CodeSolution(
            @JsonPropertyDescription("生成的代码") String code,
            @JsonPropertyDescription("关于解决方案的任何附加说明") String notes
    ) {
        @JsonCreator
        public CodeSolution(
                @JsonProperty("code") String code,
                @JsonProperty("notes") String notes) {
            this.code = code;
            this.notes = notes;
        }
    }

    @JsonClassDescription("代码解决方案的解释")
    public record Explanation(
            @JsonPropertyDescription("方法和实现的详细解释") String text
    ) {
        @JsonCreator
        public Explanation(@JsonProperty("text") String text) {
            this.text = text;
        }
    }

    @JsonClassDescription("包含完整解决方案的最终格式化响应")
    public record FinalResponse(
            @JsonPropertyDescription("完整的格式化响应") String text
    ) implements HasContent {
        @JsonCreator
        public FinalResponse(@JsonProperty("text") String text) {
            this.text = text;
        }

        @Override
        public String getContent() {
            return text;
        }
    }
}
