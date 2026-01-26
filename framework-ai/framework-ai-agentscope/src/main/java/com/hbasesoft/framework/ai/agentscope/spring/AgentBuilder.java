/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agentscope.spring;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;

import com.hbasesoft.framework.ai.agentscope.AgentConfig;
import com.hbasesoft.framework.ai.core.Agent;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.Logger;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.hook.Hook;
import io.agentscope.core.memory.Memory;
import io.agentscope.core.model.Model;
import io.agentscope.core.tool.AgentTool;
import io.agentscope.core.tool.Toolkit;
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
 * @see com.hbasesoft.framework.ai.agentscope.spring <br>
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
    public static ReActAgent build(final AgentConfig config, final Agent agentAnnotation, final boolean hasToolsMethods,
        final String toolBeanName, final BeanFactory beanFactory) {
        // 创建Builder
        ReActAgent.Builder builder = ReActAgent.builder();

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

        // 3. 设置系统提示词（注解优先）
        String systemPrompt = getConfigValue(agentAnnotation.systemPrompt(),
            config != null ? config.systemPrompt() : null);
        if (StringUtils.isNotBlank(systemPrompt)) {
            builder.sysPrompt(systemPrompt);
            String displayPrompt = systemPrompt.length() > LOG_MAX_LENGTH
                ? systemPrompt.substring(0, LOG_MAX_LENGTH) + "..."
                : systemPrompt;
            LOGGER.debug("设置系统提示词: {0}", displayPrompt);
        }

        // 4. 设置模型（优先级：AgentConfig > @Agent注解Bean名称）
        Model model = getConfigBean(agentAnnotation.model(), Model.class, config != null ? config.model() : null,
            beanFactory);
        if (model != null) {
            builder.model(model);
            LOGGER.debug("设置模型: {0}", model.getClass().getSimpleName());
        }

        // 5. 设置记忆（优先级：AgentConfig > @Agent注解Bean名称）
        Memory memory = getConfigBean(agentAnnotation.memery(), Memory.class, config != null ? config.memory() : null,
            beanFactory);
        if (memory != null) {
            builder.memory(memory);
            LOGGER.debug("设置记忆: {0}", memory.getClass().getSimpleName());
        }

        // 6. 设置钩子（优先级：AgentConfig > @Agent注解Bean名称）
        List<Hook> hooks = getConfigBeans(agentAnnotation.hooks(), Hook.class, config != null ? config.hooks() : null,
            beanFactory);
        if (hooks != null && !hooks.isEmpty()) {
            builder.hooks(hooks);
            LOGGER.debug("设置钩子数量: {0}", hooks.size());
        }

        // 7. 其他AgentConfig配置（如果提供了config）
        if (config != null) {
            applyConfigFromInterface(builder, config);
        }

        Toolkit toolkit = new Toolkit();
        // 8. 设置注解上的工具
        List<AgentTool> agentTools = getConfigBeans(agentAnnotation.tools(), AgentTool.class, null, beanFactory);
        if (CollectionUtils.isNotEmpty(agentTools)) {
            agentTools.forEach(tool -> {
                toolkit.registerAgentTool(tool);
            });
            builder.toolkit(toolkit);
            LOGGER.debug("设置注解上的工具: {0}", Arrays.toString(agentAnnotation.tools()));
        }

        // 9. 设置对象上的工具
        if (hasToolsMethods && StringUtils.isNotEmpty(toolBeanName)) {
            Object toolBean = beanFactory.getBean(toolBeanName);
            toolkit.registerTool(toolBean);
            builder.toolkit(toolkit);
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
    private static void applyConfigFromInterface(final ReActAgent.Builder builder, final AgentConfig config) {
        // 最大迭代次数
        int maxIters = config.maxIters();
        if (maxIters > 0) {
            builder.maxIters(maxIters);
            LOGGER.debug("设置最大迭代次数: {0}", maxIters);
        }

        // 元工具
        builder.enableMetaTool(config.enableMetaTool());
        LOGGER.debug("启用元工具");

        // 模型执行配置
        if (config.modelExecutionConfig() != null) {
            builder.modelExecutionConfig(config.modelExecutionConfig());
            LOGGER.debug("设置模型执行配置");
        }

        // 工具执行配置
        if (config.toolExecutionConfig() != null) {
            builder.toolExecutionConfig(config.toolExecutionConfig());
            LOGGER.debug("设置工具执行配置");
        }

        // 结构化输出提醒器
        if (config.structuredOutputReminder() != null) {
            builder.structuredOutputReminder(config.structuredOutputReminder());
            LOGGER.debug("设置结构化输出提醒器");
        }

        // 启用计划功能
        if (config.enablePlan()) {
            builder.enablePlan();
            LOGGER.debug("启用计划功能");
        }

        // 计划笔记本
        if (config.planNotebook() != null) {
            builder.planNotebook(config.planNotebook());
            LOGGER.debug("设置计划笔记本");
        }

        // 技能箱
        if (config.skillBox() != null) {
            builder.skillBox(config.skillBox());
            LOGGER.debug("设置技能箱");
        }

        // 长期记忆
        if (config.longTermMemory() != null) {
            builder.longTermMemory(config.longTermMemory());
            LOGGER.debug("设置长期记忆");
        }

        // 长期记忆模式
        if (config.longTermMemoryMode() != null) {
            builder.longTermMemoryMode(config.longTermMemoryMode());
            LOGGER.debug("设置长期记忆模式: {0}", config.longTermMemoryMode());
        }

        // 状态持久化配置
        if (config.statePersistence() != null) {
            builder.statePersistence(config.statePersistence());
            LOGGER.debug("设置状态持久化配置");
        }

        // 知识库列表
        if (config.knowledges() != null && !config.knowledges().isEmpty()) {
            builder.knowledges(config.knowledges());
            LOGGER.debug("设置知识库数量: {0}", config.knowledges().size());
        }

        // RAG模式
        if (config.ragMode() != null) {
            builder.ragMode(config.ragMode());
            LOGGER.debug("设置RAG模式: {0}", config.ragMode());
        }

        // 检索配置
        if (config.retrieveConfig() != null) {
            builder.retrieveConfig(config.retrieveConfig());
            LOGGER.debug("设置检索配置");
        }

        // 工具执行上下文
        if (config.toolExecutionContext() != null) {
            builder.toolExecutionConfig(config.toolExecutionConfig());
            LOGGER.debug("设置工具执行上下文");
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
        return configValue;
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
            return Arrays.stream(annotationBeanNames).filter(t -> StringUtils.isNotEmpty(t)).map(annotationBeanName -> {
                try {
                    T bean = beanFactory.getBean(annotationBeanName, beanType);
                    LOGGER.debug("从容器获取Bean: name={0}, type={1}", annotationBeanName, beanType.getSimpleName());
                    return bean;
                }
                catch (Exception e) {
                    LOGGER.warn("从容器获取Bean失败: name={0}, type={1}", annotationBeanName, beanType.getName(), e);
                    throw new RuntimeException(e);
                }
            }).toList();
        }

        return null;
    }
}
