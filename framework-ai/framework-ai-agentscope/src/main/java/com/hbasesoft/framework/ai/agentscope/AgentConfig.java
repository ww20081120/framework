/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agentscope;

import java.util.List;

import io.agentscope.core.hook.Hook;
import io.agentscope.core.memory.LongTermMemory;
import io.agentscope.core.memory.LongTermMemoryMode;
import io.agentscope.core.memory.Memory;
import io.agentscope.core.model.ExecutionConfig;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.StructuredOutputReminder;
import io.agentscope.core.plan.PlanNotebook;
import io.agentscope.core.rag.Knowledge;
import io.agentscope.core.rag.RAGMode;
import io.agentscope.core.rag.model.RetrieveConfig;
import io.agentscope.core.skill.SkillBox;
import io.agentscope.core.state.StatePersistence;
import io.agentscope.core.tool.ToolExecutionContext;

/**
 * Agent代理配置接口<br>
 * 定义了Agent代理的各种配置选项，包括名称、描述、系统提示、模型、记忆等核心配置<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年1月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agentscope <br>
 */
public interface AgentConfig {

    /**
     * 设置Agent的描述<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return Agent描述<br>
     */
    default String description() {
        return null;
    }

    /**
     * 设置Agent的系统提示词<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 系统提示词<br>
     */
    default String systemPrompt() {
        return null;
    }

    /**
     * 检查Agent是否正在运行<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return true表示正在运行，false表示未运行<br>
     */
    default boolean checkRunning() {
        return true;
    }

    /**
     * 设置Agent使用的语言模型<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 语言模型实例<br>
     */
    default Model model() {
        return null;
    }

    /**
     * 设置用于存储对话历史的记忆组件<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 记忆组件实例<br>
     */
    default Memory memory() {
        return null;
    }

    /**
     * 设置推理-行动的最大迭代次数<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 最大迭代次数，默认为10<br>
     */
    default int maxIters() {
        return 10;
    }

    /**
     * 添加多个钩子用于监控和拦截Agent执行事件<br>
     * <p>
     * 钩子可以观察或修改推理、行动等阶段的事件。可以添加多个钩子，并按优先级顺序执行（优先级值较小的先执行）。
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 钩子列表<br>
     */
    default List<Hook> hooks() {
        return null;
    }

    /**
     * 启用或禁用元工具功能<br>
     * <p>
     * 启用后，工具集将自动注册一个元工具，向Agent提供可用工具的信息。这可以帮助Agent在不完全依赖系统提示的情况下了解可用的工具。
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return true表示启用元工具，false表示禁用<br>
     */
    default boolean enableMetaTool() {
        return false;
    }

    /**
     * 设置模型API调用的执行配置<br>
     * <p>
     * 此配置控制推理阶段模型请求的超时、重试行为和退避策略。如果未设置，Agent将使用模型的默认执行配置。
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 模型执行配置<br>
     */
    default ExecutionConfig modelExecutionConfig() {
        return null;
    }

    /**
     * 设置工具执行的执行配置<br>
     * <p>
     * 此配置控制行动阶段工具调用的超时、重试行为和退避策略。如果未设置，工具集将使用其默认执行配置。
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 工具执行配置<br>
     */
    default ExecutionConfig toolExecutionConfig() {
        return null;
    }

    /**
     * 设置结构化输出强制模式<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 结构化输出提醒器<br>
     */
    default StructuredOutputReminder structuredOutputReminder() {
        return null;
    }

    /**
     * 设置用于基于计划的任务执行的PlanNotebook<br>
     * <p>
     * 提供后，PlanNotebook将被集成到Agent中：
     * <ul>
     * <li>计划管理工具将自动注册到工具集</li>
     * <li>将添加一个钩子，在每个推理步骤之前注入计划提示</li>
     * </ul>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 计划笔记本实例<br>
     */
    default PlanNotebook planNotebook() {
        return null;
    }

    /**
     * 设置Agent的技能箱<br>
     * <p>
     * 技能箱用于管理Agent的技能。它将用于将技能注册到工具集。
     * <ul>
     * <li>技能加载工具将自动注册到工具集</li>
     * <li>将添加技能钩子以注入技能提示并管理技能激活</li>
     * </ul>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 技能箱实例<br>
     */
    default SkillBox skillBox() {
        return null;
    }

    /**
     * 设置Agent的长期记忆<br>
     * <p>
     * 长期记忆使Agent能够跨会话记住信息。它可以与{@link #longTermMemoryMode(LongTermMemoryMode)}结合使用，以控制记忆管理是自动的、Agent控制的，还是两者兼有。
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 长期记忆实例<br>
     */
    default LongTermMemory longTermMemory() {
        return null;
    }

    /**
     * 设置长期记忆模式<br>
     * <p>
     * 这决定了长期记忆如何与Agent集成：
     * <ul>
     * <li><b>AGENT_CONTROL：</b> 注册记忆工具供Agent调用</li>
     * <li><b>STATIC_CONTROL：</b> 框架自动检索/记录记忆</li>
     * <li><b>BOTH：</b> 结合两种方法（默认）</li>
     * </ul>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 长期记忆模式<br>
     */
    default LongTermMemoryMode longTermMemoryMode() {
        return null;
    }

    /**
     * 设置状态持久化配置<br>
     * <p>
     * 使用此项控制在saveFrom/loadFrom操作期间哪些组件的状态由Agent管理。默认情况下，管理所有组件。
     * <p>
     * 使用示例：
     *
     * <pre>{@code
     * ReActAgent agent = ReActAgent.builder().name("assistant").model(model)
     *     .statePersistence(StatePersistence.builder().planNotebookManaged(false) // 让用户单独管理PlanNotebook
     *         .build())
     *     .build();
     * }</pre>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 状态持久化配置<br>
     */
    default StatePersistence statePersistence() {
        return null;
    }

    /**
     * 使用默认配置启用计划功能<br>
     * <p>
     * 这是一个便捷方法，等同于：
     *
     * <pre>{@code
     * planNotebook(PlanNotebook.builder().build())
     * }</pre>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return true表示启用计划功能，false表示禁用<br>
     */
    default boolean enablePlan() {
        return false;
    }

    /**
     * 添加多个知识库用于RAG（检索增强生成）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 知识库列表<br>
     */
    default List<Knowledge> knowledges() {
        return null;
    }

    /**
     * 设置RAG模式<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return RAG模式<br>
     */
    default RAGMode ragMode() {
        return null;
    }

    /**
     * 设置RAG的检索配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 检索配置<br>
     */
    default RetrieveConfig retrieveConfig() {
        return null;
    }

    /**
     * 设置Agent的工具执行上下文<br>
     * <p>
     * 此上下文将传递给此Agent调用的所有工具，可以包括用户身份、会话信息、权限和其他元数据。此Agent级别的上下文将覆盖工具集级别的上下文，但可以被调用级别的上下文覆盖。
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 工具执行上下文<br>
     */
    default ToolExecutionContext toolExecutionContext() {
        return null;
    }
}
