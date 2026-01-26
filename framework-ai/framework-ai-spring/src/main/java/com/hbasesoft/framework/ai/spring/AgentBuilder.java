/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.spring;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.advisor.observation.AdvisorObservationConvention;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.beans.factory.BeanFactory;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.agent.Builder;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.Hook;
import com.alibaba.cloud.ai.graph.agent.interceptor.Interceptor;
import com.alibaba.cloud.ai.graph.checkpoint.BaseCheckpointSaver;
import com.alibaba.cloud.ai.graph.serializer.StateSerializer;
import com.hbasesoft.framework.ai.core.Agent;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.Logger;

import io.micrometer.observation.ObservationRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Agent构建器<br>
 * 负责将AgentConfig和@Agent注解配置转换为ReActAgent<br>
 * 配置优先级：AgentConfig实现 > @Agent注解（从容器获取）> ReActAgent默认值<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @CreateDate 2026年1月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.spring <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AgentBuilder {

    /** 日志记录器 */
    private static final Logger LOGGER = new Logger(AgentBuilder.class);

    /** 日志最大显示长度 */
    private static final int LOG_MAX_LENGTH = 50;

    /**
     * 构建ReActAgent<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param config AgentConfig接口实现（可选）
     * @param agentAnnotation @Agent注解
     * @param hasToolsMethods 是否包含@Tools方法
     * @param toolBeanName 工具Bean名称
     * @param beanFactory Spring BeanFactory（用于延迟获取Bean）
     * @return ReActAgent实例<br>
     */
    public static ReactAgent build(final AgentConfig config, final Agent agentAnnotation, final boolean hasToolsMethods,
        final String toolBeanName, final BeanFactory beanFactory) {
        // 创建Builder
        Builder builder = ReactAgent.builder();

        // 1. 设置名称（仅从注解获取）
        String name = agentAnnotation.name();
        Assert.notEmpty(name, ErrorCodeDef.PARAM_NOT_NULL, "agent name");
        builder.name(name);
        LOGGER.debug("设置Agent名称: {0}", name);

        // 2. 设置描述（注解优先）
        String description = getConfigValue(agentAnnotation.description(),
            config != null ? config.description() : null);
        if (StringUtils.isNotBlank(description)) {
            builder.description(description);
            LOGGER.debug("设置Agent描述: {0}", description);
        }

        // 3. 设置指令（AgentConfig优先）
        String instruction = config != null ? config.instruction() : null;
        if (StringUtils.isNotBlank(instruction)) {
            builder.instruction(instruction);
            String displayInstruction = instruction.length() > LOG_MAX_LENGTH
                ? instruction.substring(0, LOG_MAX_LENGTH) + "..."
                : instruction;
            LOGGER.debug("设置指令: {0}", displayInstruction);
        }

        // 4. 设置系统提示词（注解优先）
        String systemPrompt = getConfigValue(agentAnnotation.systemPrompt(),
            config != null ? config.systemPrompt() : null);
        if (StringUtils.isNotBlank(systemPrompt)) {
            builder.systemPrompt(systemPrompt);
            String displayPrompt = systemPrompt.length() > LOG_MAX_LENGTH
                ? systemPrompt.substring(0, LOG_MAX_LENGTH) + "..."
                : systemPrompt;
            LOGGER.debug("设置系统提示词: {0}", displayPrompt);
        }

        // 5. 设置模型（优先级：AgentConfig > @Agent注解Bean名称）
        ChatModel model = getConfigBean(agentAnnotation.model(), ChatModel.class,
            config != null ? config.model() : null, beanFactory);
        if (model != null) {
            builder.model(model);
            LOGGER.debug("设置模型: {0}", model.getClass().getSimpleName());
        }

        // 6. 设置钩子（优先级：AgentConfig > @Agent注解Bean名称）
        List<Hook> hooks = null;
        // 优先使用AgentConfig中的钩子
        if (config != null && config.hooks() != null && !config.hooks().isEmpty()) {
            hooks = new ArrayList<>(config.hooks());
        }
        // 否则从注解中获取
        else if (org.apache.commons.lang3.ArrayUtils.isNotEmpty(agentAnnotation.hooks()) && beanFactory != null) {
            hooks = java.util.Arrays.stream(agentAnnotation.hooks()).filter(StringUtils::isNotEmpty)
                .map(hookName -> (Hook) beanFactory.getBean(hookName, Hook.class)).toList();
        }
        if (hooks != null && !hooks.isEmpty()) {
            builder.hooks(hooks);
            LOGGER.debug("设置钩子数量: {0}", hooks.size());
        }

        // 7. 其他AgentConfig配置（如果提供了config）
        if (config != null) {
            applyConfigFromInterface(builder, config);
        }

        // 8. 设置注解上的工具
        List<ToolCallback> toolkits = getConfigBeans(agentAnnotation.tools(), ToolCallback.class, null, beanFactory);
        if (CollectionUtils.isNotEmpty(toolkits)) {
            builder.tools(toolkits);
            LOGGER.debug("设置注解上的工具数量: {0}", toolkits.size());
        }

        // 9. 设置对象上的工具
        if (hasToolsMethods && StringUtils.isNotEmpty(toolBeanName)) {
            Object toolBean = beanFactory.getBean(toolBeanName);
            ToolCallback[] tools = ToolCallbacks.from(toolBean);
            builder.tools(tools);
            LOGGER.debug("设置Agent上的工具: {0}", toolBeanName);
        }

        // 构建并返回
        return builder.build();
    }

    /**
     * 应用AgentConfig接口的其他配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applyConfigFromInterface(final Builder builder, final AgentConfig config) {
        applyOutputConfig(builder, config);
        applyChatAndToolConfig(builder, config);
        applyToolContextConfig(builder, config);
        applyExecutionConfig(builder, config);
        applySchemaConfig(builder, config);
        applyContentConfig(builder, config);
        applyInterceptorConfig(builder, config);
        applyObservationConfig(builder, config);
        applyStateAndExecutorConfig(builder, config);
    }

    /**
     * 应用输出相关配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applyOutputConfig(final Builder builder, final AgentConfig config) {
        // 输出Key
        String outputKey = config.outputKey();
        if (StringUtils.isNotBlank(outputKey)) {
            builder.outputKey(outputKey);
            LOGGER.debug("设置输出Key: {0}", outputKey);
        }

        // 输出Key策略
        KeyStrategy outputKeyStrategy = config.outputKeyStrategy();
        if (outputKeyStrategy != null) {
            builder.outputKeyStrategy(outputKeyStrategy);
            LOGGER.debug("设置输出Key策略: {0}", outputKeyStrategy);
        }
    }

    /**
     * 应用聊天和工具相关配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applyChatAndToolConfig(final Builder builder, final AgentConfig config) {
        // 聊天选项
        ChatOptions chatOptions = config.chatOptions();
        if (chatOptions != null) {
            builder.chatOptions(chatOptions);
            LOGGER.debug("设置聊天选项");
        }

        // 方法工具
        List<?> methodTools = config.methodTools();
        if (methodTools != null && !methodTools.isEmpty()) {
            builder.methodTools(methodTools);
            LOGGER.debug("设置方法工具数量: {0}", methodTools.size());
        }

        // 工具回调提供者
        List<ToolCallbackProvider> toolCallbackProviders = config.toolCallbackProviders();
        if (toolCallbackProviders != null && !toolCallbackProviders.isEmpty()) {
            builder.toolCallbackProviders(toolCallbackProviders.toArray(new ToolCallbackProvider[0]));
            LOGGER.debug("设置工具回调提供者数量: {0}", toolCallbackProviders.size());
        }

        // 工具回调解析器
        ToolCallbackResolver resolver = config.resolver();
        if (resolver != null) {
            builder.resolver(resolver);
            LOGGER.debug("设置工具回调解析器");
        }

        // 工具执行异常处理器
        ToolExecutionExceptionProcessor toolExecutionExceptionProcessor = config.toolExecutionExceptionProcessor();
        if (toolExecutionExceptionProcessor != null) {
            builder.toolExecutionExceptionProcessor(toolExecutionExceptionProcessor);
            LOGGER.debug("设置工具执行异常处理器");
        }
    }

    /**
     * 应用工具上下文配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applyToolContextConfig(final Builder builder, final AgentConfig config) {
        // 工具上下文
        Map<String, Object> toolContext = config.toolContext();
        if (toolContext != null && !toolContext.isEmpty()) {
            builder.toolContext(toolContext);
            LOGGER.debug("设置工具上下文，键数量: {0}", toolContext.size());
        }
    }

    /**
     * 应用执行相关配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applyExecutionConfig(final Builder builder, final AgentConfig config) {
        // 释放线程
        boolean releaseThread = config.releaseThread();
        if (releaseThread) {
            builder.releaseThread(releaseThread);
            LOGGER.debug("启用释放线程");
        }

        // 检查点保存器
        BaseCheckpointSaver saver = config.saver();
        if (saver != null) {
            builder.saver(saver);
            LOGGER.debug("设置检查点保存器");
        }

        // 编译配置
        CompileConfig compileConfig = config.compileConfig();
        if (compileConfig != null) {
            builder.compileConfig(compileConfig);
            LOGGER.debug("设置编译配置");
        }
    }

    /**
     * 应用Schema和类型相关配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applySchemaConfig(final Builder builder, final AgentConfig config) {
        // 输入Schema
        String inputSchema = config.inputSchema();
        if (StringUtils.isNotBlank(inputSchema)) {
            builder.inputSchema(inputSchema);
            LOGGER.debug("设置输入Schema");
        }

        // 输入类型
        Type inputType = config.inputType();
        if (inputType != null) {
            builder.inputType(inputType);
            LOGGER.debug("设置输入类型: {0}", inputType.getTypeName());
        }

        // 输出Schema
        String outputSchema = config.outputSchema();
        if (StringUtils.isNotBlank(outputSchema)) {
            builder.outputSchema(outputSchema);
            LOGGER.debug("设置输出Schema");
        }

        // 输出类型
        Class<?> outputType = config.outputType();
        if (outputType != null) {
            builder.outputType(outputType);
            LOGGER.debug("设置输出类型: {0}", outputType.getName());
        }
    }

    /**
     * 应用内容相关配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applyContentConfig(final Builder builder, final AgentConfig config) {
        // 包含内容
        boolean includeContents = config.includeContents();
        if (includeContents) {
            builder.includeContents(includeContents);
            LOGGER.debug("启用包含内容");
        }

        // 返回推理内容
        boolean returnReasoningContents = config.returnReasoningContents();
        if (returnReasoningContents) {
            builder.returnReasoningContents(returnReasoningContents);
            LOGGER.debug("启用返回推理内容");
        }
    }

    /**
     * 应用拦截器配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applyInterceptorConfig(final Builder builder, final AgentConfig config) {
        // 拦截器
        List<? extends Interceptor> interceptors = config.interceptors();
        if (interceptors != null && !interceptors.isEmpty()) {
            builder.interceptors(interceptors);
            LOGGER.debug("设置拦截器数量: {0}", interceptors.size());
        }
    }

    /**
     * 应用观察相关配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applyObservationConfig(final Builder builder, final AgentConfig config) {
        // 观察注册表
        ObservationRegistry observationRegistry = config.observationRegistry();
        if (observationRegistry != null) {
            builder.observationRegistry(observationRegistry);
            LOGGER.debug("设置观察注册表");
        }

        // 自定义观察约定
        ChatClientObservationConvention customObservationConvention = config.customObservationConvention();
        if (customObservationConvention != null) {
            builder.customObservationConvention(customObservationConvention);
            LOGGER.debug("设置自定义观察约定");
        }

        // 顾问观察约定
        AdvisorObservationConvention advisorObservationConvention = config.advisorObservationConvention();
        if (advisorObservationConvention != null) {
            builder.advisorObservationConvention(advisorObservationConvention);
            LOGGER.debug("设置顾问观察约定");
        }

        // 启用日志
        boolean enableLogging = config.enableLogging();
        if (enableLogging) {
            builder.enableLogging(enableLogging);
            LOGGER.debug("启用日志");
        }
    }

    /**
     * 应用状态和执行器配置<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param builder Builder对象<br>
     * @param config AgentConfig实例<br>
     */
    private static void applyStateAndExecutorConfig(final Builder builder, final AgentConfig config) {
        // 状态序列化器
        StateSerializer stateSerializer = config.stateSerializer();
        if (stateSerializer != null) {
            builder.stateSerializer(stateSerializer);
            LOGGER.debug("设置状态序列化器");
        }

        // 执行器
        Executor executor = config.executor();
        if (executor != null) {
            builder.executor(executor);
            LOGGER.debug("设置执行器");
        }
    }

    /**
     * 获取配置值（优先使用配置的值，没有在使用注解的值）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param annotationValue 注解值<br>
     * @param configValue AgentConfig值<br>
     * @return 优先值<br>
     */
    private static String getConfigValue(final String annotationValue, final String configValue) {
        if (StringUtils.isNotBlank(configValue)) {
            return configValue;
        }
        return annotationValue;
    }

    /**
     * 获取配置Bean（优先级：AgentConfig > @Agent注解Bean名称）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param annotationBeanName 注解中的Bean名称<br>
     * @param beanType Bean类型<br>
     * @param configValue AgentConfig中的值<br>
     * @param beanFactory Spring BeanFactory<br>
     * @param <T> 泛型类型<br>
     * @return Bean实例<br>
     */
    private static <T> T getConfigBean(final String annotationBeanName, final Class<T> beanType, final T configValue,
        final BeanFactory beanFactory) {
        // 优先使用AgentConfig中的值
        if (configValue != null) {
            return configValue;
        }

        // 从容器获取Bean（使用BeanFactory延迟获取）
        if (StringUtils.isNotBlank(annotationBeanName) && beanFactory != null) {
            try {
                T bean = beanFactory.getBean(annotationBeanName, beanType);
                LOGGER.debug("从容器获取Bean: name={0}, type={1}", annotationBeanName, beanType.getSimpleName());
                return bean;
            }
            catch (Exception e) {
                LOGGER.warn("从容器获取Bean失败: name={0}, type={1}", annotationBeanName, beanType.getName());
            }
        }

        return null;
    }

    /**
     * 获取配置Bean列表（优先级：AgentConfig > @Agent注解Bean名称）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param annotationBeanNames 注解中的Bean名称数组<br>
     * @param beanType Bean类型<br>
     * @param configValue AgentConfig中的值<br>
     * @param beanFactory Spring BeanFactory<br>
     * @param <T> 泛型类型<br>
     * @return Bean实例列表<br>
     */
    private static <T> List<T> getConfigBeans(final String[] annotationBeanNames, final Class<T> beanType,
        final List<T> configValue, final BeanFactory beanFactory) {
        // 优先使用AgentConfig中的值
        if (configValue != null) {
            return configValue;
        }

        // 从容器获取Bean（使用BeanFactory延迟获取）
        if (org.apache.commons.lang3.ArrayUtils.isNotEmpty(annotationBeanNames) && beanFactory != null) {
            return java.util.Arrays.stream(annotationBeanNames)
                .filter(StringUtils::isNotEmpty)
                .map(annotationBeanName -> {
                    try {
                        T bean = beanFactory.getBean(annotationBeanName, beanType);
                        LOGGER.debug("从容器获取Bean: name={0}, type={1}", annotationBeanName,
                            beanType.getSimpleName());
                        return bean;
                    }
                    catch (Exception e) {
                        LOGGER.warn("从容器获取Bean失败: name={0}, type={1}", annotationBeanName,
                            beanType.getName(), e);
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        }

        return null;
    }
}
