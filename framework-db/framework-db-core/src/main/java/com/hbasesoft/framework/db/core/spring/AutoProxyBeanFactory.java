/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.annotation.handler.DaoHandler;
import com.hbasesoft.framework.db.core.annotation.handler.SQLHandler;
import com.hbasesoft.framework.db.core.config.DaoConfig;
import com.hbasesoft.framework.db.core.executor.ISqlExcutor;
import com.hbasesoft.framework.db.core.executor.ISqlExcutorFactory;

import lombok.RequiredArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.hbasesoft.framework.dao.beanfactory <br>
 */
@RequiredArgsConstructor
public class AutoProxyBeanFactory implements BeanFactoryPostProcessor {

    /** basePackage */
    private static String[] basePackage;

    /**
     * logger
     */
    private static Logger logger = new Logger(AutoProxyBeanFactory.class);

    /**
     * executor的工厂
     */
    private final ISqlExcutorFactory sqlExcutorFactory;

    /**
     * 注解名称
     */
    private final Class<? extends Annotation> annotationClazz;

    /** interceptors */
    private String[] interceptors;

    /** config */
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
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {

        ComponentScan componentScanAnnotation = findComponentScanAnnotation(beanFactory);

        logger.info("*************************Dao注入列表 {0}***************************", componentScanAnnotation);

        if (componentScanAnnotation != null) {
            basePackage = componentScanAnnotation.basePackages();

            SQLHandler sqlHandler = new SQLHandler(config);

            try {
                for (String pack : basePackage) {
                    if (StringUtils.isNotEmpty(pack)) {
                        String tempPack = pack.indexOf("*") == -1 ? pack + ".*" : pack;
                        Set<Class<?>> clazzSet = BeanUtil.getClasses(tempPack,
                            clazz -> clazz.isAnnotationPresent(annotationClazz));
                        String className = null;
                        for (Class<?> clazz : clazzSet) {
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
            catch (Exception e) {
                logger.error("------->自动扫描jar包失败", e);
            }
        }

        logger.info("***********************************************************");
    }

    private ComponentScan findComponentScanAnnotation(final ConfigurableListableBeanFactory beanFactory) {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            Class<?> beanClass;
            try {
                beanClass = Class.forName(beanDefinition.getBeanClassName());
                ComponentScan componentScan = AnnotationUtils.findAnnotation(beanClass, ComponentScan.class);
                if (componentScan != null) {
                    return componentScan;
                }
            }
            catch (ClassNotFoundException e) {
                LoggerUtil.error(e);
            }
        }
        return null;
    }

    private DaoHandler getDaoHandler(final Class<?> clazz) {
        DaoHandler handler = new DaoHandler(config);
        ISqlExcutor baseDao = sqlExcutorFactory.create();
        handler.setSqlExcutor(baseDao);

        // 获取实体类类型
        Class<?> entityClazz = findEntityClass(clazz);

        if (entityClazz != null) {
            baseDao.setEntityClazz(entityClazz);
        }
        return handler;
    }

    /**
     * 递归查找 BaseEntity 的子类
     * @param clazz 当前类
     * @return 找到的实体类类型，如果没有找到则返回 null
     */
    private Class<?> findEntityClass(final Class<?> clazz) {
        // 如果当前类是 Object，则停止递归
        if (clazz == null || clazz.equals(Object.class)) {
            return null;
        }

        // 检查当前类的泛型父类
        ParameterizedType genericSuperclass = getParameterizedType(clazz.getGenericSuperclass());
        if (genericSuperclass != null) {
            for (Type typeArgument : genericSuperclass.getActualTypeArguments()) {
                if (typeArgument instanceof Class<?>) {
                    Class<?> candidate = (Class<?>) typeArgument;
                    if (BaseEntity.class.isAssignableFrom(candidate)) {
                        return candidate;
                    }
                }
            }
        }

        // 检查当前类的泛型接口
        for (Type interfaceType : clazz.getGenericInterfaces()) {
            ParameterizedType parameterizedInterface = getParameterizedType(interfaceType);
            if (parameterizedInterface != null) {
                for (Type typeArgument : parameterizedInterface.getActualTypeArguments()) {
                    if (typeArgument instanceof Class<?>) {
                        Class<?> candidate = (Class<?>) typeArgument;
                        if (BaseEntity.class.isAssignableFrom(candidate)) {
                            return candidate;
                        }
                    }
                }
            }
        }
        if (clazz.isInterface()) {
            for (Class<?> superInterface : clazz.getInterfaces()) {
                Class<?> result = findEntityClass(superInterface);
                if (result != null) {
                    return result;
                }
            }
        }
        // 如果当前类没有找到，递归查找父类
        return null;
    }

    /**
     * 获取 ParameterizedType 对象
     * @param type 类型
     * @return 如果是 ParameterizedType 则返回，否则返回 null
     */
    private ParameterizedType getParameterizedType(final Type type) {
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param config <br>
     */
    public void setConfig(final DaoConfig config) {
        this.config = config;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param interceptors <br>
     */
    public void setInterceptors(final String... interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String[] getBasePackage() {
        return basePackage;
    }
}
