/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.tx.core.annotation.Tx;

import javassist.Modifier;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 26, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.core <br>
 */
public class TxAnnotationMethodRegistStartupListener implements StartupListener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void complete(final ApplicationContext context) {
        List<String> beanNames = new ArrayList<String>();
        try {
            beanNames.addAll(Arrays.asList(context.getBeanNamesForAnnotation(RestController.class)));
            beanNames.addAll(Arrays.asList(context.getBeanNamesForAnnotation(Controller.class)));
            beanNames.addAll(Arrays.asList(context.getBeanNamesForAnnotation(Component.class)));
            beanNames.addAll(Arrays.asList(context.getBeanNamesForAnnotation(Service.class)));
            for (String beanName : context.getBeanDefinitionNames()) {
                LoggerUtil.info("***********************扫描{0}类,注册分布式事务************************************", beanName);
                Object targetbean = context.getBean(beanName);
                Class<?> clazz = targetbean.getClass();
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(Tx.class)) {
                        Tx[] txs = method.getDeclaredAnnotationsByType(Tx.class);
                        if (txs != null && txs.length > 0) {
                            try {
                                Object target = context.getBean(clazz);
                                Tx tx = txs[0];
                                String targetName = tx.name();
                                if (org.apache.commons.lang3.StringUtils.isEmpty(targetName)) {
                                    targetName = TxManager.getMarker(method);
                                }
                                TxManager.regist(targetName, target, method);

                                LoggerUtil.info("success regist distributed transaction with name {0}", targetName);
                            }
                            catch (Exception e) {
                                LoggerUtil.error(e);
                            }
                        }
                    }

                }

            }
        }
        catch (Exception e) {
            LoggerUtil.error("------->Tx自动扫描jar包失败", e);
        }
        LoggerUtil.info("***********************************************************");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public LoadOrder getOrder() {
        return LoadOrder.LAST;
    }

}
