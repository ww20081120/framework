/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.NonNull;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.hbasesoft.framework.ai.core.Agent;
import com.hbasesoft.framework.common.utils.logger.Logger;

import lombok.Setter;

/**
 * Agent工厂Bean<br>
 * 负责将@Agent注解和AgentConfig接口配置转换为ReActAgent实例<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @CreateDate 2026年1月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agentscope.spring <br>
 */
@Setter
public class AgentFactoryBean implements FactoryBean<ReactAgent>, BeanFactoryAware {

    /** 日志记录器 */
    private static final Logger LOGGER = new Logger(AgentFactoryBean.class);

    /** Agent类 */
    private Class<?> agentClass;

    /** Agent注解 */
    private Agent agentAnnotation;

    /** 是否实现了AgentConfig接口 */
    private boolean implementsAgentConfig;

    /** 是否包含@Tools注解的方法 */
    private boolean hasToolsMethods;

    /** 工具Bean名称（如果有@Tools方法） */
    private String toolBeanName;

    /** Spring BeanFactory（用于延迟获取Bean） */
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(final @NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public ReactAgent getObject() throws Exception {
        LOGGER.info("开始构建Agent: {0}", agentClass.getName());

        try {
            // 创建AgentConfig实例（如果实现了接口）
            AgentConfig config = null;
            if (implementsAgentConfig) {
                config = createConfigInstance();
                LOGGER.debug("已创建AgentConfig实例");
            }
            else {
                LOGGER.debug("未实现AgentConfig接口，仅使用@Agent注解配置");
            }

            // 使用AgentBuilder构建ReActAgent，传入BeanFactory用于延迟获取Bean
            ReactAgent reactAgent = AgentBuilder.build(config, agentAnnotation, hasToolsMethods, toolBeanName,
                beanFactory);

            LOGGER.info("Agent构建完成: 类型={0}", reactAgent.getClass().getSimpleName());
            return reactAgent;
        }
        catch (Exception e) {
            LOGGER.error("构建Agent失败: {0}", agentClass.getName(), e);
            throw e;
        }
    }

    @Override
    public Class<?> getObjectType() {
        return ReactAgent.class;
    }

    /**
     * 创建AgentConfig实例<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return AgentConfig实例<br>
     * @throws Exception 创建失败抛出异常<br>
     */
    private AgentConfig createConfigInstance() throws Exception {
        return (AgentConfig) agentClass.getDeclaredConstructor().newInstance();
    }
}
