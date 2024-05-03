/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.core.advice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.tracing.core.TracerLog;

/** 
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年12月29日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.core.advice <br>
 */
@ConditionalOnBean(annotation = TracerLog.class)
@Configuration
public class TracerLogConfig {

    /** 包名称 */
    private Set<String> basePackagesSet = new HashSet<>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Bean
    public BeanDefinitionRegistryPostProcessor tracerLogScanner() {
        return new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanFactory(final ConfigurableListableBeanFactory registry) throws BeansException {
            }

            @Override
            public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry beanFactory)
                throws BeansException {
                for (String beanName : beanFactory.getBeanDefinitionNames()) {
                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                    if (beanDefinition != null && beanDefinition.getBeanClassName() != null) {
                        Class<?> beanClass;
                        try {
                            beanClass = Class.forName(beanDefinition.getBeanClassName());
                            TracerLog tracerLog = AnnotationUtils.findAnnotation(beanClass, TracerLog.class);
                            if (tracerLog != null) {
                                basePackagesSet.addAll(Arrays.asList(tracerLog.basePackages()));
                            }
                        }
                        catch (ClassNotFoundException e) {
                            LoggerUtil.error(e);
                        }
                    }
                }
            }
        };

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Bean
    public Advisor loggingAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // 从@TracerLog中获取
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isEmpty(basePackagesSet)) {
            basePackagesSet.add("com.hbasesoft");
        }
        for (String basePackage : basePackagesSet) {
            if (StringUtils.isNotEmpty(basePackage)) {
                if (sb.length() > 0) {
                    sb.append(" || ");
                }
                sb.append("execution(public * ").append(basePackage).append("..*(..))");
            }
        }
        pointcut.setExpression(sb.toString());
        return new DefaultPointcutAdvisor(pointcut, new TracerLogMethodInterceptor());
    }
}
