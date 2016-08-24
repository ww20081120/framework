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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class WebApplication extends SpringBootServletInitializer {

    private static final Logger LOG = new Logger(WebApplication.class);

    private static List<StartupListener> listenerList = null;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param builder
     * @return <br>
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param application
     * @return <br>
     */
    @Override
    protected WebApplicationContext run(SpringApplication application) {
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

        WebApplicationContext context = null;
        try {
            LOG.info("*********************初始化StartupListener*************************");
            if (CommonUtil.isNotEmpty(listenerList)) {
                for (StartupListener listener : listenerList) {
                    listener.init();
                    LOG.info("   {0} 初始化", listener.getClass().getName());
                }
            }

            LOG.info("====================>准备加载Spring配置文件<====================");
            context = super.run(application);
            LOG.info("====================>Spring配置文件加载完毕<====================");

            if (CommonUtil.isNotEmpty(listenerList)) {
                for (StartupListener listener : listenerList) {
                    listener.complete(context);
                    LOG.info("   {0} 初始化成功。", listener.getClass().getName());
                }
            }

            LOG.info("**********************************************************");

            System.out.println(new StringBuilder().append("\n***************************************").append('\n')
                .append("         ").append(ManagementFactory.getRuntimeMXBean().getName()).append('\n')
                .append("            ").append(PropertyHolder.getProperty("project.code")).append("模块启动成功！")
                .append('\n').append("***************************************"));

            LOG.info("====================>系统正常启动<====================");
        }
        catch (Exception e) {
            LOG.error("系统启动失败", e);
        }
        return context;
    }
}
