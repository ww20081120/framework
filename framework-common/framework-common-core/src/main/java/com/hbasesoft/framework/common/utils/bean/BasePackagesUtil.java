/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationUtils;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.bean <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BasePackagesUtil {

    /** registPackages */
    private static List<String> registPackages = new ArrayList<>();

    /** componentScaned */
    private static boolean componentScaned = false;

    /**
     * Description: 获取基础包路径 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beanFactory beanFactory
     * @return basePackages <br>
     */
    public static List<String> getBasePackages(final ConfigurableListableBeanFactory beanFactory) {
        synchronized (BasePackagesUtil.class) {
            if (!componentScaned) {
                ComponentScan componentScan = findComponentScanAnnotation(beanFactory);
                if (componentScan != null) {
                    for (String pkg : componentScan.basePackages()) {
                        if (StringUtils.isNotBlank(pkg) && !registPackages.contains(pkg)) {
                            registPackages.add(pkg);
                        }
                    }
                    for (Class<?> clazz : componentScan.basePackageClasses()) {
                        if (clazz != null && clazz.getPackage() != null
                            && !registPackages.contains(clazz.getPackage().getName())) {
                            registPackages.add(clazz.getPackage().getName());
                        }
                    }
                }
                componentScaned = true;
            }
        }
        return registPackages;
    }

    /**
     * Description: 获取基础包路径 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return basePackages <br>
     */
    public static List<String> getBasePackages() {
        return registPackages;
    }

    /**
     * Description: 添加基础包路径 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param basePackage basePackage
     */
    public static void addBasePackage(final String basePackage) {
        if (StringUtils.isNotBlank(basePackage) && !registPackages.contains(basePackage)) {
            registPackages.add(basePackage);
        }
    }

    /**
     * Description: 添加基础包路径 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param basePackages basePackages
     */
    public static void addBasePackages(final String... basePackages) {
        if (ArrayUtils.isNotEmpty(basePackages)) {
            for (String basePackage : basePackages) {
                addBasePackage(basePackage);
            }
        }
    }

    private static ComponentScan findComponentScanAnnotation(final ConfigurableListableBeanFactory beanFactory) {
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

}
