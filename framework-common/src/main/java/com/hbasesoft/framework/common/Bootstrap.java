/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 * 
 * @SpringBootApplication
 * @ComponentScan(basePackages = "com.hbasesoft") <br>
 *                             @ImportResource("classpath*:META-INF/spring/*.xml") <br>
 * @EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class Bootstrap {

    /**
     * logger
     */
    private static Logger logger = new Logger(Bootstrap.class);

    private static List<StartupListener> listenerList = null;

    public static void before() {
        ServiceLoader<StartupListener> loader = ServiceLoader.load(StartupListener.class);
        if (loader != null) {
            listenerList = new ArrayList<StartupListener>();
            for (StartupListener listener : loader) {
                listenerList.add(listener);
            }

            Collections.sort(listenerList, new Comparator<StartupListener>() {
                @Override
                public int compare(StartupListener o1, StartupListener o2) {
                    return o1.getOrder().compareTo(o2.getOrder());
                }
            });
        }

        logger.info("*********************初始化StartupListener*************************");
        if (CollectionUtils.isNotEmpty(listenerList)) {
            for (StartupListener listener : listenerList) {
                listener.init();
                logger.info("   {0} 初始化", listener.getClass().getName());
            }
        }

        logger.info("====================>准备加载Spring配置文件<====================");
    }

    public static void after(ApplicationContext context) {
        logger.info("====================>Spring配置文件加载完毕<====================");

        if (CollectionUtils.isNotEmpty(listenerList)) {
            for (StartupListener listener : listenerList) {
                listener.complete(context);
                logger.info("   {0} 初始化成功。", listener.getClass().getName());
            }
        }

        logger.info("**********************************************************");

        System.out.println(new StringBuilder().append("\n***************************************").append('\n')
            .append("         ").append(ManagementFactory.getRuntimeMXBean().getName()).append('\n').append("         ")
            .append(PropertyHolder.getProjectName()).append("模块启动成功！").append('\n')
            .append("***************************************"));
        logger.info("====================>系统正常启动<====================");

    }
}
