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

/**
 * 执行事件类型枚举
 * 
 * @author 王伟
 * @version 1.0
 * @since 2025年10月14日
 */
public enum ExecutionEventType {

    /**
     * 计划创建完成
     */
    PLAN_CREATED("计划创建完成"),

    /**
     * 步骤开始执行
     */
    STEP_START("步骤开始"),

    /**
     * 步骤进度更新
     */
    STEP_PROGRESS("步骤进度"),

    /**
     * 步骤执行完成
     */
    STEP_COMPLETE("步骤完成"),

    /**
     * 总结流式输出
     */
    SUMMARY_STREAM("总结流式输出"),

    /**
     * 整个执行完成
     */
    EXECUTION_COMPLETE("执行完成"),

    /**
     * 执行错误
     */
    ERROR("执行错误"),

    /**
     * 状态变化
     */
    STATUS_CHANGE("状态变化"),

    /**
     * 大模型流式响应
     */
    LLM_RESPONSE_STREAM("大模型流式响应");

    private final String description;

    ExecutionEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
