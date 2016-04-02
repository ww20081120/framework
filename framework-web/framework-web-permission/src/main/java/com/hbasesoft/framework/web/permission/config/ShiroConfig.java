/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.config;

import java.util.Properties;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年3月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.permission.config <br>
 */
@Configuration
public class ShiroConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    public FilterRegistrationBean registShiroFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new DelegatingFilterProxy());
        registration.addUrlPatterns("/*");
        registration.setName("shiroFilter");
        return registration;
    }

    @Bean(name = "securityManager")
    public SecurityManager registSecurityManager() {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(applicationContext.getBean(DatabaseRealm.class));
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());

        return securityManager;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean registShiroFilterFactoryBean() {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(applicationContext.getBean(SecurityManager.class));
        factoryBean.setLoginUrl("/login");
        factoryBean.setSuccessUrl("/");
        factoryBean.setUnauthorizedUrl("/login");
        return factoryBean;
    }

    @Bean
    public SimpleMappingExceptionResolver reigistExceptionResolver() {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        properties.setProperty("org.apache.shiro.authz.AuthorizationException", "/login");
        properties.setProperty("org.apache.shiro.authz.UnauthenticatedException", "/login");
        properties.setProperty("org.apache.struts.chain.commands.UnauthorizedActionException", "/login");
        resolver.setExceptionMappings(properties);
        return resolver;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor registBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor registAdvisor() {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(applicationContext.getBean(SecurityManager.class));
        return advisor;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param arg0
     * @throws BeansException <br>
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

}
