/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hbasesoft.framework.ai.agent.planning.listener;

import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.ai.agent.planning.model.vo.ExecutionStep;
import com.hbasesoft.framework.ai.agent.planning.model.vo.PlanInterface;

/**
 * 执行监听器接口，用于监听计划执行过程中的各种事件
 * 
 * @author 王伟
 * @version 1.0
 * @since 2025年10月14日
 */
public interface ExecutionListener {

    /**
     * 当计划创建完成时触发
     * 
     * @param plan 创建的执行计划
     */
    default void onPlanCreated(PlanInterface plan) {
        // 默认空实现
    }

    /**
     * 当执行步骤开始时触发
     * 
     * @param step 开始执行的步骤
     */
    default void onStepStart(ExecutionStep step) {
        // 默认空实现
    }

    /**
     * 当执行步骤有进度更新时触发
     * 
     * @param step 当前执行的步骤
     * @param progress 进度信息（可以是状态描述、部分结果等）
     */
    default void onStepProgress(ExecutionStep step, String progress) {
        // 默认空实现
    }

    /**
     * 当执行步骤完成时触发
     * 
     * @param step 完成的步骤
     * @param result 步骤执行结果
     */
    default void onStepComplete(ExecutionStep step, String result) {
        // 默认空实现
    }

    /**
     * 当总结生成时有流式输出时触发
     * 
     * @param chunk 总结的流式文本片段
     */
    default void onSummaryStream(String chunk) {
        // 默认空实现
    }

    /**
     * 当整个计划执行完成时触发
     * 
     * @param context 执行上下文
     */
    default void onExecutionComplete(ExecutionContext context) {
        // 默认空实现
    }

    /**
     * 当执行过程中发生错误时触发
     * 
     * @param error 发生的错误
     */
    default void onError(Exception error) {
        // 默认空实现
    }

    /**
     * 当计划状态发生变化时触发
     * 
     * @param context 执行上下文
     * @param status 状态描述
     */
    default void onStatusChange(ExecutionContext context, String status) {
        // 默认空实现
    }

    /**
     * 当有大模型响应流式输出时触发（不仅限于总结）
     * 
     * @param context 执行上下文
     * @param response 流式响应片段
     * @param responseType 响应类型（如：计划生成、步骤执行、总结等）
     */
    default void onLlmResponseStream(ExecutionContext context, String response, String responseType) {
        // 默认空实现
    }
}
