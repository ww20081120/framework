/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.bootstrap;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.config.core.ConfigHelper;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年6月23日 <br>
 * @see com.hbasesoft.framework.bootstrap <br>
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.hbasesoft")
@ImportResource("classpath*:META-INF/spring/*.xml")
public class Startup {

    /**
     * logger
     */
    private static Logger logger = new Logger(Startup.class);

    /**
     * context
     */
    private static ApplicationContext context;

    private static List<StartupListener> listenerList = null;

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param args <br>
     * @throws IOException <br>
     */
    public static void main(String[] args) throws Exception {

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
        if (CommonUtil.isNotEmpty(listenerList)) {
            for (StartupListener listener : listenerList) {
                listener.init();
                logger.info("   {0} 初始化", listener.getClass().getName());
            }
        }

        logger.info("====================>准备加载Spring配置文件<====================");
        context = SpringApplication.run(Startup.class, args);
        logger.info("====================>Spring配置文件加载完毕<====================");

        if (CommonUtil.isNotEmpty(listenerList)) {
            for (StartupListener listener : listenerList) {
                listener.complete(context);
                logger.info("   {0} 初始化成功。", listener.getClass().getName());
            }
        }

        logger.info("**********************************************************");

        System.out.println(new StringBuilder().append("\n***************************************").append('\n')
            .append("         ").append(ManagementFactory.getRuntimeMXBean().getName()).append('\n')
            .append("            ").append(ConfigHelper.getModuleCode()).append("模块启动成功！").append('\n')
            .append("***************************************"));

        if (CommonUtil.isNotEmpty(listenerList)) {
            for (int i = listenerList.size() - 1; i >= 0; i--) {
                listenerList.get(i).destory();
            }
        }

        logger.info("====================>系统正常启动<====================");
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
