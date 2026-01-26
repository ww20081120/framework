/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.spring;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.ai.chat.client.advisor.observation.AdvisorObservationConvention;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.agent.hook.Hook;
import com.alibaba.cloud.ai.graph.agent.interceptor.Interceptor;
import com.alibaba.cloud.ai.graph.checkpoint.BaseCheckpointSaver;
import com.alibaba.cloud.ai.graph.serializer.StateSerializer;

import io.micrometer.observation.ObservationRegistry;

/**
 * Agent配置接口<br>
 * 定义了Agent代理的各种配置选项，用于构建和定制Agent行为<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年1月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.spring <br>
 */
public interface AgentConfig {

    /**
     * 获取Agent的描述信息<br>
     *
     * @return Agent描述<br>
     */
    default String description() {
        return null;
    }

    /**
     * 获取Agent的指令<br>
     * 指令用于指导Agent的行为和响应方式<br>
     *
     * @return 指令文本<br>
     */
    default String instruction() {
        return null;
    }

    /**
     * 获取Agent的系统提示词<br>
     * 系统提示词定义了Agent的角色和行为准则<br>
     *
     * @return 系统提示词<br>
     */
    default String systemPrompt() {
        return null;
    }

    /**
     * 获取输出键名<br>
     * 用于指定Agent输出的键名<br>
     *
     * @return 输出键名<br>
     */
    default String outputKey() {
        return null;
    }

    /**
     * 获取输出键策略<br>
     * 定义如何处理输出键的策略<br>
     *
     * @return 输出键策略<br>
     */
    default KeyStrategy outputKeyStrategy() {
        return null;
    }

    /**
     * 获取Agent使用的语言模型<br>
     *
     * @return ChatModel实例<br>
     */
    default ChatModel model() {
        return null;
    }

    /**
     * 获取聊天选项<br>
     * 用于配置模型生成的参数，如温度、最大令牌数等<br>
     *
     * @return ChatOptions实例<br>
     */
    default ChatOptions chatOptions() {
        return null;
    }

    /**
     * 获取方法工具列表<br>
     * 这些工具可以被Agent调用<br>
     *
     * @return 方法工具列表<br>
     */
    default List<?> methodTools() {
        return null;
    }

    /**
     * 获取工具回调提供者列表<br>
     * 用于提供和管理工具回调<br>
     *
     * @return 工具回调提供者列表<br>
     */
    default List<ToolCallbackProvider> toolCallbackProviders() {
        return null;
    }

    /**
     * 获取工具回调解析器<br>
     * 用于解析和匹配工具回调<br>
     *
     * @return 工具回调解析器<br>
     */
    default ToolCallbackResolver resolver() {
        return null;
    }

    /**
     * 获取工具执行异常处理器<br>
     * 用于处理工具执行过程中的异常<br>
     *
     * @return 工具执行异常处理器<br>
     */
    default ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
        return null;
    }

    /**
     * 获取工具上下文<br>
     * 提供额外的上下文信息给工具使用<br>
     *
     * @return 工具上下文映射<br>
     */
    default Map<String, Object> toolContext() {
        return null;
    }

    /**
     * 是否释放线程<br>
     * 配置Agent在执行后是否释放线程资源<br>
     *
     * @return true表示释放线程，false表示不释放<br>
     */
    default boolean releaseThread() {
        return false;
    }

    /**
     * 获取检查点保存器<br>
     * 用于保存Agent执行过程中的检查点状态<br>
     *
     * @return 检查点保存器<br>
     */
    default BaseCheckpointSaver saver() {
        return null;
    }

    /**
     * 获取编译配置<br>
     * 用于配置Agent的编译行为<br>
     *
     * @return 编译配置<br>
     */
    default CompileConfig compileConfig() {
        return null;
    }

    /**
     * 获取输入Schema<br>
     * 定义Agent输入的JSON Schema<br>
     *
     * @return 输入Schema字符串<br>
     */
    default String inputSchema() {
        return null;
    }

    /**
     * 获取输入类型<br>
     * 定义Agent期望的输入类型<br>
     *
     * @return 输入类型<br>
     */
    default Type inputType() {
        return null;
    }

    /**
     * 获取输出Schema<br>
     * 定义Agent输出的JSON Schema<br>
     *
     * @return 输出Schema字符串<br>
     */
    default String outputSchema() {
        return null;
    }

    /**
     * 获取输出类型<br>
     * 定义Agent返回的输出类型<br>
     *
     * @return 输出类型<br>
     */
    default Class<?> outputType() {
        return null;
    }

    /**
     * 是否包含内容<br>
     * 控制Agent输出是否包含详细内容<br>
     *
     * @return true表示包含内容，false表示不包含<br>
     */
    default boolean includeContents() {
        return false;
    }

    /**
     * 是否返回推理内容<br>
     * 控制是否返回Agent的推理过程内容<br>
     *
     * @return true表示返回推理内容，false表示不返回<br>
     */
    default boolean returnReasoningContents() {
        return false;
    }

    /**
     * 获取钩子列表<br>
     * 钩子用于在Agent执行的关键点进行拦截和处理<br>
     *
     * @return 钩子列表<br>
     */
    default List<? extends Hook> hooks() {
        return null;
    }

    /**
     * 获取拦截器列表<br>
     * 拦截器用于拦截和修改Agent的请求和响应<br>
     *
     * @return 拦截器列表<br>
     */
    default List<? extends Interceptor> interceptors() {
        return null;
    }

    /**
     * 获取观察注册表<br>
     * 用于监控和观察Agent的执行情况<br>
     *
     * @return 观察注册表<br>
     */
    default ObservationRegistry observationRegistry() {
        return null;
    }

    /**
     * 获取自定义观察约定<br>
     * 用于自定义ChatClient的观察行为<br>
     *
     * @return 自定义观察约定<br>
     */
    default ChatClientObservationConvention customObservationConvention() {
        return null;
    }

    /**
     * 获取顾问观察约定<br>
     * 用于自定义Advisor的观察行为<br>
     *
     * @return 顾问观察约定<br>
     */
    default AdvisorObservationConvention advisorObservationConvention() {
        return null;
    }

    /**
     * 是否启用日志<br>
     * 控制是否记录Agent的执行日志<br>
     *
     * @return true表示启用日志，false表示不启用<br>
     */
    default boolean enableLogging() {
        return false;
    }

    /**
     * 获取状态序列化器<br>
     * 用于序列化和反序列化Agent的状态<br>
     *
     * @return 状态序列化器<br>
     */
    default StateSerializer stateSerializer() {
        return null;
    }

    /**
     * 获取并行节点执行器<br>
     * <p>
     * 此执行器将用于Agent执行图中的所有并行节点。当执行并行节点时，
     * 将使用此执行器并发运行并行分支。<br>
     *
     * @return 执行器实例<br>
     */
    default Executor executor() {
        return null;
    }
}
