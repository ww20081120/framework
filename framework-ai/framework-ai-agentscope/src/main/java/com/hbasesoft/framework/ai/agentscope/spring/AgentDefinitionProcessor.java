/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agentscope.spring;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.lang.NonNull;

import com.hbasesoft.framework.ai.agentscope.AgentConfig;
import com.hbasesoft.framework.ai.core.Agent;
import com.hbasesoft.framework.common.utils.bean.BasePackagesUtil;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;

import io.agentscope.core.tool.Tool;

/**
 * Agent Bean定义处理器<br>
 * 扫描@Agent注解的类，将其转换为AgentScope的ReActAgent<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @CreateDate 2026年1月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agentscope.spring <br>
 */
public class AgentDefinitionProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final Logger LOGGER = new Logger(AgentDefinitionProcessor.class);

    /** 扫描分隔线标记 */
    private static final String SEPARATOR_LINE = "**************************************************************";

    @Override
    public void postProcessBeanDefinitionRegistry(final @NonNull BeanDefinitionRegistry registry)
        throws BeansException {
        List<String> basePackages = BasePackagesUtil.getBasePackages(registry);

        LOGGER.info(SEPARATOR_LINE);
        LOGGER.info("***********************Agent扫描开始***************************");
        LOGGER.info(SEPARATOR_LINE);
        LOGGER.info("扫描包路径: {0}", basePackages);

        if (CollectionUtils.isNotEmpty(basePackages)) {
            for (String pack : basePackages) {
                if (StringUtils.isNotEmpty(pack)) {
                    String tempPack = pack.indexOf("*") == -1 ? pack + ".*" : pack;
                    BeanUtil.getClasses(tempPack, clazz -> clazz.isAnnotationPresent(Agent.class))
                        .forEach(clazz -> processAgentClass(clazz, registry));
                }
            }
        }

        LOGGER.info(SEPARATOR_LINE);
        LOGGER.info("***********************Agent扫描完成***************************");
        LOGGER.info(SEPARATOR_LINE);
    }

    @Override
    public void postProcessBeanFactory(final @NonNull ConfigurableListableBeanFactory beanFactory)
        throws BeansException {
        // 无需实现
    }

    /**
     * 处理单个Agent类<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz Agent类
     * @param registry Bean定义注册表<br>
     */
    private void processAgentClass(final Class<?> clazz, final BeanDefinitionRegistry registry) {
        Agent agentAnnotation = clazz.getAnnotation(Agent.class);

        LOGGER.info("发现Agent类: {0}", clazz.getName());

        // 检查是否实现了AgentConfig接口（可选）
        boolean implementsConfig = AgentConfig.class.isAssignableFrom(clazz);
        if (implementsConfig) {
            LOGGER.debug("Agent类 {0} 实现了AgentConfig接口", clazz.getName());
        }
        else {
            LOGGER.debug("Agent类 {0} 未实现AgentConfig接口，将仅使用@Agent注解配置", clazz.getName());
        }

        // 检查是否有方法使用了@Tools注解
        boolean hasToolsMethods = hasToolsAnnotation(clazz);
        if (hasToolsMethods) {
            LOGGER.debug("Agent类 {0} 包含@Tools注解的方法，将保留原Bean定义作为工具", clazz.getName());
        }

        try {
            String toolBeanName = StringUtils.uncapitalize(clazz.getSimpleName());

            // 如果有@Tools注解的方法，需要保留原Bean定义作为工具
            if (hasToolsMethods && registry.containsBeanDefinition(toolBeanName)) {
                // 将原有Bean定义重命名为随机名称
                BeanDefinition originalDef = registry.getBeanDefinition(toolBeanName);
                registry.removeBeanDefinition(toolBeanName);

                // 生成唯一的工具Bean名称
                toolBeanName = generateUniqueToolBeanName(clazz);
                registry.registerBeanDefinition(toolBeanName, originalDef);

                LOGGER.debug("将原Bean定义 {0} 重命名为 {1} 作为工具", StringUtils.uncapitalize(clazz.getSimpleName()),
                    toolBeanName);
            }
            else if (!hasToolsMethods && registry.containsBeanDefinition(toolBeanName)) {
                // 没有@Tools方法，直接移除原有Bean定义
                registry.removeBeanDefinition(toolBeanName);
                LOGGER.debug("移除原有Bean定义: {0}", toolBeanName);
            }

            // 创建AgentFactoryBean的Bean定义（使用@Agent.name()作为Bean名称）
            String agentBeanName = generateBeanName(clazz, agentAnnotation);
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AgentFactoryBean.class)
                .addPropertyValue("agentClass", clazz).addPropertyValue("agentAnnotation", agentAnnotation)
                .addPropertyValue("implementsAgentConfig", implementsConfig)
                .addPropertyValue("hasToolsMethods", hasToolsMethods).addPropertyValue("toolBeanName", toolBeanName)
                .setAutowireMode(ConfigurableListableBeanFactory.AUTOWIRE_BY_NAME);

            registry.registerBeanDefinition(agentBeanName, builder.getBeanDefinition());

            LOGGER.info("成功注册Agent: Agent Bean名称={0}, 类={1}, 实现AgentConfig={2}, 包含@Tools方法={3}", agentBeanName,
                clazz.getSimpleName(), implementsConfig, hasToolsMethods);

            if (hasToolsMethods) {
                LOGGER.info("工具Bean名称={0}, Agent可通过此名称访问工具类", toolBeanName);
            }
        }
        catch (Exception e) {
            LOGGER.error(e, "注册Agent失败: {0}", clazz.getName());
        }
    }

    /**
     * 生成Bean名称<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz 类
     * @param agentAnnotation Agent注解
     * @return Bean名称<br>
     */
    private @NonNull String generateBeanName(final @NonNull Class<?> clazz, final Agent agentAnnotation) {
        // 优先使用@Agent注解的name属性
        if (agentAnnotation != null && StringUtils.isNotBlank(agentAnnotation.name())) {
            return agentAnnotation.name();
        }
        // 否则使用类名首字母小写
        return StringUtils.uncapitalize(clazz.getSimpleName());
    }

    /**
     * 检查类是否有方法使用了@Tools注解<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz 类<br>
     * @return true表示有@Tools注解的方法，false表示没有<br>
     */
    private boolean hasToolsAnnotation(final Class<?> clazz) {

        // 检查所有方法是否有@Tools注解
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Tool.class)) {
                LOGGER.debug("发现@Tool注解的方法: {0}.{1}", clazz.getSimpleName(), method.getName());
                return true;
            }
        }

        // 检查父类和接口的方法
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && hasToolsAnnotation(superClass)) {
            return true;
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaceClass : interfaces) {
            if (hasToolsAnnotation(interfaceClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 生成唯一的工具Bean名称<br>
     * 格式：类名 + UUID前8位 + "Tool"<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz 类<br>
     * @return 唯一的工具Bean名称<br>
     */
    private String generateUniqueToolBeanName(final Class<?> clazz) {
        String className = clazz.getSimpleName();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return className + uuid + "Tool";
    }
}
