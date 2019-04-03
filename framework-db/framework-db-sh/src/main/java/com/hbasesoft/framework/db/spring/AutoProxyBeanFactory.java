/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.spring;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.annotation.handler.DaoHandler;
import com.hbasesoft.framework.db.core.annotation.handler.SQLHandler;
import com.hbasesoft.framework.db.core.config.DaoConfig;
import com.hbasesoft.framework.db.hibernate.BaseHibernateDao;
import com.hbasesoft.framework.db.hibernate.IBaseDAO;

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

    private DaoConfig config;

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
        SQLHandler sqlHandler = new SQLHandler();
        sqlHandler.setDaoConfig(config);

        try {
            for (String pack : packagesToScan) {
                if (StringUtils.isNotEmpty(pack)) {
                    Set<Class<?>> clazzSet = BeanUtil.getClasses(pack);
                    String className = null;
                    for (Class<?> clazz : clazzSet) {
                        if (clazz.isAnnotationPresent(Dao.class)) {
                            className = clazz.getName();
                            String beanName = StringUtils.uncapitalize(clazz.getSimpleName());
                            if (beanFactory.containsBean(beanName)) {
                                beanName = className;
                                if (beanFactory.containsBean(beanName)) {
                                    continue;
                                }
                            }

                            // 此处不缓存SQL
                            sqlHandler.invoke(clazz);

                            // 单独加载一个接口的代理类
                            ProxyFactoryBean factoryBean = new ProxyFactoryBean();
                            factoryBean.setBeanFactory(beanFactory);
                            factoryBean.setInterfaces(clazz);
                            factoryBean.setInterceptorNames(interceptors);
                            factoryBean.setTarget(getDaoHandler(clazz));
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

    private DaoHandler getDaoHandler(Class<?> clazz) {
        DaoHandler handler = new DaoHandler();
        handler.setDaoConfig(config);
        BaseHibernateDao baseDao = new BaseHibernateDao();
        handler.setSqlExcutor(baseDao);

        // 继承泛型的类需要获取到范性类
        if (IBaseDAO.class.isAssignableFrom(clazz)) {
            Class<?> entityClazz = null;
            ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
            if (type != null) {
                for (Type t2 : type.getActualTypeArguments()) {
                    Class<?> t3 = (Class<?>) t2;
                    if (BaseEntity.class.isAssignableFrom(t3)) {
                        entityClazz = t3;
                    }
                }
            }

            if (entityClazz == null) {
                Type[] interfacesTypes = clazz.getGenericInterfaces();
                for (Type t : interfacesTypes) {
                    Type[] genericType2 = ((ParameterizedType) t).getActualTypeArguments();
                    for (Type t2 : genericType2) {
                        Class<?> t3 = (Class<?>) t2;
                        if (BaseEntity.class.isAssignableFrom(t3)) {
                            entityClazz = t3;
                        }
                    }
                }
            }
            Assert.notNull(entityClazz, ErrorCodeDef.GENERIC_TYPE_ERROR, clazz.getName());
            baseDao.setEntityClazz(entityClazz);
        }
        return handler;
    }

    public void setConfig(DaoConfig config) {
        this.config = config;
    }

    public void setPackagesToScan(String... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setInterceptors(String... interceptors) {
        this.interceptors = interceptors;
    }
}
