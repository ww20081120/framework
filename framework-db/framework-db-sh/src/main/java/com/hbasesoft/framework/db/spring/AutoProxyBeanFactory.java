/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.spring;

import java.util.Set;

import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.cache.core.ICache;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.handler.SQLHandler;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.hbasesoft.framework.dao.beanfactory <br>
 */
public class AutoProxyBeanFactory implements BeanFactoryPostProcessor {

    /**
     * logger
     */
    private static Logger logger = new Logger(AutoProxyBeanFactory.class);

    /** 扫描路径 */
    private String[] packagesToScan;

    /** interceptors */
    private String[] interceptors;

    /** SQL handler */
    private SQLHandler handler;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param beanFactory <br>
     * @throws BeansException <br>
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("*************************Dao注入列表***************************");
        try {
            ICache cache = CacheHelper.getCache();
            cache.removeNode(CacheConstant.SQL_PARAM_DIR);
            cache.removeNode(CacheConstant.SQL_DIR);
            for (String pack : packagesToScan) {
                if (CommonUtil.isNotEmpty(pack)) {
                    Set<Class<?>> clazzSet = BeanUtil.getClasses(pack);
                    String className = null;
                    for (Class<?> clazz : clazzSet) {
                        if (clazz.isAnnotationPresent(Dao.class)) {
                            className = clazz.getName();
                            String beanName = CommonUtil.lowerCaseFirstChar(clazz.getSimpleName());
                            if (beanFactory.containsBean(beanName)) {
                                beanName = className;
                                if (beanFactory.containsBean(beanName)) {
                                    continue;
                                }
                            }

                            // 此处不缓存SQL
                            handler.invoke(clazz);

                            // 单独加载一个接口的代理类
                            ProxyFactoryBean factoryBean = new ProxyFactoryBean();
                            factoryBean.setBeanFactory(beanFactory);
                            factoryBean.setInterfaces(clazz);
                            factoryBean.setInterceptorNames(interceptors);
                            beanFactory.registerSingleton(beanName, factoryBean);
                            logger.info("    success create interface [{0}] with name {1}", className, beanName);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("------->自动扫描jar包失败", e);
        }
        logger.info("***********************************************************");
    }

    public void setPackagesToScan(String... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setInterceptors(String... interceptors) {
        this.interceptors = interceptors;
    }

    public void setHandler(SQLHandler handler) {
        this.handler = handler;
    }
}
