/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.core.init;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.config.core.ConfigHelper;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.core.init <br>
 */
public class StartupServlet extends org.springframework.web.servlet.DispatcherServlet {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 6364903462514241142L;

    private static final Logger LOG = new Logger(StartupServlet.class);

    /**
     * context
     */
    private static ServletContext context;

    /**
     * applicationContext
     */
    private static ApplicationContext applicationContext;

    private List<StartupListener> listenerList = null;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param config <br>
     * @throws ServletException <br>
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        context = config.getServletContext();

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

        LOG.info("*********************初始化StartupListener*************************");
        try {
            if (CommonUtil.isNotEmpty(listenerList)) {
                for (StartupListener listener : listenerList) {
                    listener.init();
                    LOG.info("   {0} 初始化", listener.getClass().getName());
                }
            }

            // 加载spring配置
            super.init(config);
            applicationContext = super.getWebApplicationContext();

            if (CommonUtil.isNotEmpty(listenerList)) {
                for (StartupListener listener : listenerList) {
                    listener.complete(applicationContext);
                    LOG.info("   {0} 初始化成功。", listener.getClass().getName());
                }
            }

        }
        catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
        LOG.info("**********************************************************");

        System.out.println(new StringBuilder().append("\n***************************************").append('\n')
            .append("*            ").append(ManagementFactory.getRuntimeMXBean().getName()).append("               *")
            .append('\n').append("*            ").append(ConfigHelper.getModuleCode()).append("模块启动成功！")
            .append("                  *").append('\n').append("***************************************"));

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void destroy() {
        if (CommonUtil.isNotEmpty(listenerList)) {
            for (int i = listenerList.size() - 1; i >= 0; i--) {
                listenerList.get(i).destory();
            }
        }
        super.destroy();
    }

    public static ServletContext getContext() {
        return context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
