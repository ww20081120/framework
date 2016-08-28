/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年6月23日 <br>
 * @see com.hbasesoft.framework.bootstrap <br>
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.hbasesoft")
@ImportResource("classpath*:META-INF/spring/*.xml")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Application extends WebMvcConfigurerAdapter implements EmbeddedServletContainerCustomizer {

    /**
     * logger
     */
    private static Logger logger = new Logger(Application.class);

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
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
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
            .append("            ").append(PropertyHolder.getProperty("project.code")).append("模块启动成功！").append('\n')
            .append("***************************************"));

        // if (CommonUtil.isNotEmpty(listenerList)) {
        // for (int i = listenerList.size() - 1; i >= 0; i--) {
        // listenerList.get(i).destory();
        // }
        // }

        logger.info("====================>系统正常启动<====================");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param container <br>
     */
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.setPort(PropertyHolder.getIntProperty("app.port", 8080));
    }
}
