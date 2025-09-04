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

    /**
     * Extracts a coding task from user input.
     *
     * @param userInput the user input
     * @param context   the operation context
     * @return the extracted coding task
     */
    @Action
    public CodingTask extractCodingTask(final String userInput, final OperationContext context) {
        return context.ai().withDefaultLlm().createObjectIfPossible(
                "从用户输入中提取编码任务。识别:\n" + "1. 编程语言\n" + "2. 任务描述\n" + 
        "3. 任何特定要求\n\n" + "用户输入: " + userInput,
                CodingTask.class);
    }

    /**
     * Generates code for a coding task.
     *
     * @param task    the coding task
     * @param context the operation context
     * @return the generated code solution
     */
    @Action
    public CodeSolution generateCode(final CodingTask task, final OperationContext context) {
        return context.ai().withDefaultLlm()
                .createObject(
                        "为以下任务生成代码解决方案:\n" + "语言: " + task.language() + "\n" + 
                "任务: " + task.description() + "\n"
                                + "要求: " + task.requirements() + "\n\n" + 
                "提供干净、文档齐全的代码，遵循该语言的最佳实践。",
                        CodeSolution.class);
    }

    /**
     * Explains the generated code solution.
     *
     * @param solution the code solution
     * @param context  the operation context
     * @return the explanation of the code solution
     */
    @Action
    public Explanation explainCode(final CodeSolution solution, final OperationContext context) {
        return context.ai().withDefaultLlm().createObject("提供以下代码解决方案的详细解释:\n" + 
    "代码:\n" + solution.code() + "\n\n"
                + "解释:\n" + "1. 采用的方法\n" + "2. 关键组件及其目的\n" + 
    "3. 任何重要的考虑因素或权衡", Explanation.class);
    }

    /**
     * Checks if code generation is needed.
     *
     * @param task the coding task
     * @return true if code generation is needed, false otherwise
     */
    @Condition
    public boolean isCodeGenerationNeeded(final CodingTask task) {
        // 这个条件检查是否需要生成代码
        return task != null && !task.description().isEmpty();
    }

    /**
     * Provides a complete solution with explanation.
     *
     * @param task        the coding task
     * @param solution    the code solution
     * @param explanation the explanation
     * @param context     the operation context
     * @return the final response
     */
    @AchievesGoal(description = "提供带有解释的完整编码解决方案")
    @Action
    public FinalResponse provideCompleteSolution(final CodingTask task, final CodeSolution solution,
            final Explanation explanation, final OperationContext context) {

        return context.ai().withDefaultLlm().createObject(
                "创建一个结合了编码任务、解决方案和解释的最终格式化响应:\n" + "任务: "
        + task.description() + "\n" + "解决方案: " + solution.code()
                        + "\n" + "解释: " + explanation.text() + "\n\n" 
        + "格式化为干净、专业的响应，并带有适当的 markdown 格式。",
                FinalResponse.class);
    }

    /**
     * Represents a coding task to be solved.
     * 
     * @param language     the programming language
     * @param description  the task description
     * @param requirements the specific requirements or constraints
     */
    @JsonClassDescription("要解决的编码任务")
    public record CodingTask(@JsonPropertyDescription("要使用的编程语言") String language,
            @JsonPropertyDescription("编码任务描述") String description,
            @JsonPropertyDescription("特定要求或约束") String requirements) {
        /**
         * Constructor for CodingTask.
         *
         * @param language     the programming language
         * @param description  the task description
         * @param requirements the specific requirements or constraints
         */
        @JsonCreator
        public CodingTask(@JsonProperty("language") final String language,
                @JsonProperty("description") final String description,
                @JsonProperty("requirements") final String requirements) {
            this.language = language;
            this.description = description;
            this.requirements = requirements;
        }
    }

    /**
     * Represents a generated code solution.
     * 
     * @param code  the generated code
     * @param notes any additional notes about the solution
     */
    @JsonClassDescription("生成的代码解决方案")
    public record CodeSolution(@JsonPropertyDescription("生成的代码") String code,
            @JsonPropertyDescription("关于解决方案的任何附加说明") String notes) {
        /**
         * Constructor for CodeSolution.
         *
         * @param code  the generated code
         * @param notes any additional notes about the solution
         */
        @JsonCreator
        public CodeSolution(@JsonProperty("code") final String code, 
        		@JsonProperty("notes") final String notes) {
            this.code = code;
            this.notes = notes;
        }
    }

    /**
     * Represents an explanation of a code solution.
     * 
     * @param text the detailed explanation of the approach and implementation
     */
    @JsonClassDescription("代码解决方案的解释")
    public record Explanation(@JsonPropertyDescription("方法和实现的详细解释") String text) {
        /**
         * Constructor for Explanation.
         *
         * @param text the detailed explanation of the approach and implementation
         */
        @JsonCreator
        public Explanation(@JsonProperty("text") final String text) {
            this.text = text;
        }
    }

    /**
     * Represents a final formatted response containing the complete solution.
     * 
     * @param text the complete formatted response
     */
    @JsonClassDescription("包含完整解决方案的最终格式化响应")
    public record FinalResponse(@JsonPropertyDescription("完整的格式化响应") 
    String text) implements HasContent {
        /**
         * Constructor for FinalResponse.
         *
         * @param text the complete formatted response
         */
        @JsonCreator
        public FinalResponse(@JsonProperty("text") final String text) {
            this.text = text;
        }

        @Override
        public String getContent() {
            return text;
        }
    }
}
