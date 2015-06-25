/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.db.spring;

import java.util.List;
import java.util.Set;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.bean.BeanUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.handler.SQLHandler;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.fccfc.framework.dao.beanfactory <br>
 */
public class AutoProxyBeanFactory implements BeanFactoryPostProcessor {

    private static Logger logger = new Logger(AutoProxyBeanFactory.class);

    /** 扫描路径 */
    private List<String> packagesToScan;

    /** interceptors */
    private String[] interceptors;

    /** SQL handler */
    private SQLHandler handler;

    /**
     * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            for (String pack : packagesToScan) {
                if (CommonUtil.isNotEmpty(pack)) {
                    Set<Class<?>> clazzSet = BeanUtil.getClasses(pack);
                    String className = null;
                    for (Class<?> clazz : clazzSet) {
                        if (clazz.isAnnotationPresent(Dao.class)) {
                            className = clazz.getName();
                            logger.info("----->before set dao class[{0}]", className);
                            String beanName = CommonUtil.lowerCaseFirstChar(clazz.getSimpleName());
                            if (!beanFactory.containsBean(beanName)) {
                                // 此处不缓存SQL
                                // handler.invoke(clazz);

                                // 单独加载一个接口的代理类
                                ProxyFactoryBean factoryBean = new ProxyFactoryBean();
                                factoryBean.setBeanFactory(beanFactory);
                                factoryBean.setInterfaces(clazz);
                                factoryBean.setInterceptorNames(interceptors);
                                beanFactory.registerSingleton(beanName, factoryBean);
                                logger.info("Interface [{0}] init name is [{1}]", clazz.getName(), beanName);
                            }
                            logger.info("----->success set dao class[{0}]", className);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("------->自动扫描jar包失败", e);
        }
    }

    public void setPackagesToScan(List<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setInterceptors(String[] interceptors) {
        this.interceptors = interceptors;
    }

    public void setHandler(SQLHandler handler) {
        this.handler = handler;
    }
}
