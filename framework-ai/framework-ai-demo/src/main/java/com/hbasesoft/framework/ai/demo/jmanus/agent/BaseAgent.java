package com.hbasesoft.framework.ai.demo.jmanus.agent;

import com.hbasesoft.framework.ai.agent.AgentState;

/**
 * 一个用于实现可执行多步任务的AI代理的抽象基类。 该类提供了管理代理状态、对话流程和任务逐步执行的核心功能。
 * <p>
 * 此代理支持有限数量的执行步骤，并包含以下机制：
 * <ul>
 * <li>状态管理（空闲、运行、完成）</li>
 * <li>对话跟踪</li>
 * <li>步骤限制与监控</li>
 * <li>线程安全执行</li>
 * <li>卡顿状态检测与处理</li>
 * </ul>
 * <p>
 * 实现此类的子类必须定义以下方法：
 * <ul>
 * <li>{@link #getName()} - 返回代理的名称</li>
 * <li>{@link #getDescription()} - 返回代理的描述</li>
 * <li>{@link #addThinkPrompt(List)} - 实现思维链逻辑</li>
 * <li>{@link #getNextStepMessage()} - 提供下一步的提示模板</li>
 * <li>{@link #step()} - 实现每一步的核心逻辑</li>
 * </ul>
 *
 * @see AgentState
 * @see LlmService
 */
public abstract class BaseAgent {

    /**
     * 获取智能体的名称 实现要求： 1. 返回一个简短但具有描述性的名称
     * 2. 名称应该反映该智能体的主要功能或特性
     * 3. 名称应该是唯一的，便于日志和调试
     *
     * 示例实现： - ToolCallAgent 返回 "ToolCallAgent" - BrowserAgent 返回 "BrowserAgent"
     *
     * @return 智能体的名称
     */
    public abstract String getName();

    /**
     * 获取智能体的详细描述
     * 实现要求： 1. 返回对该智能体功能的详细描述
     * 2. 描述应包含智能体的主要职责和能力
     * 3. 应说明该智能体与其他智能体的区别
     *
     * 示例实现： - ToolCallAgent: "负责管理和执行工具调用的智能体，支持多工具组合调用" - ReActAgent: "实现思考(Reasoning)和行动(Acting)交替执行的智能体"
     *
     * @return 智能体的详细描述文本
     */
    public abstract String getDescription();
}